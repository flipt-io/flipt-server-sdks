package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"maps"
	"os"
	"strings"

	"dagger.io/dagger"
	"golang.org/x/sync/errgroup"
)

var (
	sdks    string
	sdkToFn = map[string]integrationTestFn{
		"python": pythonTest,
		"node":   nodeTest,
		"rust":   rustTest,
		"java":   javaTest,
		"php":    phpTest,
		"csharp": csharpTest,
	}
)

type integrationTestFn func(context.Context, *dagger.Client, *dagger.Container, *dagger.Directory) error

func init() {
	flag.StringVar(&sdks, "sdks", "", "comma separated list of which sdk(s) to run integration tests for")
}

func main() {
	flag.Parse()

	if err := run(); err != nil {
		log.Fatal(err)
	}
}

func run() error {
	var tests = make(map[string]integrationTestFn, len(sdkToFn))

	maps.Copy(tests, sdkToFn)

	if sdks != "" {
		l := strings.Split(sdks, ",")
		subset := make(map[string]integrationTestFn, len(l))
		for _, sdk := range l {
			testFn, ok := sdkToFn[sdk]
			if !ok {
				return fmt.Errorf("sdk %s is not supported", sdk)
			}
			subset[sdk] = testFn
		}

		tests = subset
	}

	ctx := context.Background()

	client, err := dagger.Connect(ctx, dagger.WithLogOutput(os.Stdout))
	if err != nil {
		return err
	}
	defer client.Close()

	dir := client.Host().Directory(".")

	flipt, hostDirectory := getTestDependencies(ctx, client, dir)

	var g errgroup.Group

	for _, fn := range tests {
		fn := fn
		g.Go(func() error {
			return fn(ctx, client, flipt, hostDirectory)
		})
	}

	return g.Wait()
}

func getTestDependencies(_ context.Context, client *dagger.Client, dir *dagger.Directory) (*dagger.Container, *dagger.Directory) {
	// Flipt
	flipt := client.Container().From("flipt/flipt:latest").
		WithUser("root").
		WithExec([]string{"mkdir", "-p", "/var/data/flipt"}).
		WithDirectory("/var/data/flipt", dir.Directory("test/fixtures/testdata")).
		WithExec([]string{"chown", "-R", "flipt:flipt", "/var/data/flipt"}).
		WithUser("flipt").
		WithEnvVariable("FLIPT_STORAGE_TYPE", "local").
		WithEnvVariable("FLIPT_STORAGE_LOCAL_PATH", "/var/data/flipt").
		WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_TOKEN_ENABLED", "1").
		WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_TOKEN_BOOTSTRAP_TOKEN", "secret").
		WithEnvVariable("FLIPT_AUTHENTICATION_REQUIRED", "1").
		WithExposedPort(8080)

	return flipt, dir
}

// pythonTest runs the python integration test suite against a container running Flipt.
func pythonTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("python:3.11-bookworm").
		WithExec([]string{"pip", "install", "poetry==1.7.1"}).
		WithWorkdir("/src").
		WithDirectory("/src", hostDirectory.Directory("flipt-python")).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"poetry", "install"}).
		WithExec([]string{"make", "test"}).
		Sync(ctx)

	return err
}

// nodeTest runs the node integration test suite against a container running Flipt.
func nodeTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("node:21.2-bookworm").
		WithWorkdir("/src").
		// The node_modules should never be version controlled, but we will exclude it here
		// just to be safe.
		WithDirectory("/src", hostDirectory.Directory("flipt-node"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{".node_modules/"},
		}).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"npm", "install"}).
		WithExec([]string{"npm", "test"}).
		Sync(ctx)

	return err
}

func csharpTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("mcr.microsoft.com/dotnet/sdk:8.0").
		WithDirectory("/src", hostDirectory.Directory("flipt-csharp")).
		WithWorkdir("/src").
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"dotnet", "test"}).
		Sync(ctx)

	return err
}

// rustTest runs the rust integration test suite against a container running Flipt.
func rustTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("rust:1.85.0-bookworm").
		WithWorkdir("/src").
		// Exclude target directory which contain the build artifacts for Rust.
		WithDirectory("/src", hostDirectory.Directory("flipt-rust"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{"./target/"},
		}).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"cargo", "test", "--features", "flipt_integration", "--test", "integration"}).
		Sync(ctx)

	return err
}

// javaTest runs the java integration test suite against a container running Flipt.
func javaTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("gradle:8.5.0-jdk11").
		WithWorkdir("/src").
		WithDirectory("/src", hostDirectory.Directory("flipt-java"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{"./.gradle", "./.idea", "./build"},
		}).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"./gradlew", "test"}).
		Sync(ctx)

	return err
}

// phpTest runs the php integration test suite against a container running Flipt.
func phpTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory) error {
	_, err := client.Container().From("php:8-cli").
		WithEnvVariable("COMPOSER_ALLOW_SUPERUSER", "1").
		WithExec([]string{"apt-get", "update"}).
		WithExec([]string{"apt-get", "install", "-y", "git"}).
		WithExec([]string{"sh", "-c", "curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer"}).
		WithWorkdir("/src").
		WithDirectory("/src", hostDirectory.Directory("flipt-php"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{"./vendor", "./composer.lock"},
		}).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithExec([]string{"composer", "install"}).
		WithExec([]string{"composer", "test"}).
		Sync(ctx)

	return err
}
