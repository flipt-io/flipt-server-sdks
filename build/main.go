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
	languages    string
	push         bool
	languageToFn = map[string]buildFn{
		"python": pythonBuild,
		"rust":   rustBuild,
		"node":   nodeBuild,
	}
	sema = make(chan struct{}, 4)
)

func init() {
	flag.StringVar(&languages, "languages", "", "comma separated list of which language(s) to run builds for")
	flag.BoolVar(&push, "push", false, "push built artifacts to registry")
}

func main() {
	flag.Parse()

	if err := run(); err != nil {
		log.Fatal(err)
	}
}

type buildFn func(context.Context, *dagger.Client, *dagger.Directory) error

func run() error {
	var tests = make(map[string]buildFn, len(languageToFn))

	maps.Copy(tests, languageToFn)

	if languages != "" {
		l := strings.Split(languages, ",")
		subset := make(map[string]buildFn, len(l))
		for _, language := range l {
			testFn, ok := languageToFn[language]
			if !ok {
				return fmt.Errorf("language %s is not supported", language)
			}
			subset[language] = testFn
		}

		tests = subset
	}

	ctx := context.Background()

	client, err := dagger.Connect(ctx, dagger.WithLogOutput(os.Stdout))
	if err != nil {
		return err
	}

	defer client.Close()

	dir := client.Host().Directory(".", dagger.HostDirectoryOpts{
		Exclude: []string{"build/", "test/", ".git/"},
	})

	var g errgroup.Group

	for _, fn := range tests {
		fn := fn
		g.Go(take(func() error {
			return fn(ctx, client, dir)
		}))
	}

	return g.Wait()
}

func take(fn func() error) func() error {
	return func() error {
		// insert into semaphore channel to maintain
		// a max concurrency
		sema <- struct{}{}
		defer func() { <-sema }()

		return fn()
	}
}

func pythonBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("python:3.11-bookworm").
		WithExec([]string{"pip", "install", "poetry==1.7.0"}).
		WithDirectory("/src", hostDirectory.Directory("flipt-client-python")).
		WithWorkdir("/src").
		WithExec([]string{"poetry", "install", "--without=dev", "-v"}).
		WithExec([]string{"poetry", "build", "-v"})

	var err error

	if !push {
		_, err = container.Sync(ctx)
		return err
	}

	if os.Getenv("PYPI_API_KEY") == "" {
		return fmt.Errorf("PYPI_API_KEY is not set")
	}

	pypiAPIKeySecret := client.SetSecret("pypi-api-key", os.Getenv("PYPI_API_KEY"))

	_, err = container.WithSecretVariable("POETRY_PYPI_TOKEN_PYPI", pypiAPIKeySecret).
		WithExec([]string{"poetry", "publish", "-v"}).
		Sync(ctx)

	return err
}

func rustBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("rust:1.73.0-bookworm").
		WithDirectory("/src", hostDirectory.Directory("flipt-client-rust"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{"./target/"},
		}).
		WithWorkdir("/src").
		WithExec([]string{"cargo", "build", "--release"})

	var err error

	if !push {
		_, err = container.Sync(ctx)
		return err
	}

	if os.Getenv("CRATES_TOKEN") == "" {
		return fmt.Errorf("CRATES_TOKEN is not set")
	}

	cargoAPIKeySecret := client.SetSecret("rust-api-key", os.Getenv("CRATES_TOKEN"))

	_, err = container.WithSecretVariable("CRATES_TOKEN", cargoAPIKeySecret).
		WithExec([]string{"cargo", "publish", "--token", "${CRATES_TOKEN}"}).
		Sync(ctx)

	return err
}

func nodeBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("node:21.2-bookworm").
		WithDirectory("/src", hostDirectory.Directory("flipt-client-node"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{"./node_modules/"},
		}).
		WithWorkdir("/src").
		WithExec([]string{"npm", "install"}).
		WithExec([]string{"npm", "run", "build"}).
		WithExec([]string{"npm", "pack"})

	var err error

	if !push {
		_, err = container.Sync(ctx)
		return err
	}

	if os.Getenv("NPM_API_KEY") == "" {
		return fmt.Errorf("NPM_API_KEY is not set")
	}

	npmAPIKeySecret := client.SetSecret("npm-api-key", os.Getenv("NPM_API_KEY"))

	_, err = container.WithSecretVariable("NPM_TOKEN", npmAPIKeySecret).
		WithExec([]string{"npm", "config", "set", "--", "//registry.npmjs.org/:_authToken", "${NPM_TOKEN}"}).
		WithExec([]string{"npm", "publish", "--access", "public"}).
		Sync(ctx)

	return err
}
