package main

import (
	"context"
	"encoding/base64"
	"errors"
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
	push    bool
	tag     string
	sdkToFn = map[string]buildFn{
		"python": pythonBuild,
		"rust":   rustBuild,
		"node":   nodeBuild,
		"java":   javaBuild,
		"php":    phpBuild,
		"csharp": csharpBuild,
	}
)

func init() {
	flag.StringVar(&sdks, "sdks", "", "comma separated list of which sdk(s) to run builds for")
	flag.BoolVar(&push, "push", false, "push built artifacts to registry")
	flag.StringVar(&tag, "tag", "", "tag to use for release")
}

func main() {
	flag.Parse()

	if err := run(); err != nil {
		log.Fatal(err)
	}
}

type buildFn func(context.Context, *dagger.Client, *dagger.Directory) error

func run() error {
	var tests = make(map[string]buildFn, len(sdkToFn))

	maps.Copy(tests, sdkToFn)

	if sdks != "" {
		l := strings.Split(sdks, ",")
		subset := make(map[string]buildFn, len(l))
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

	dir := client.Host().Directory(".", dagger.HostDirectoryOpts{
		Exclude: []string{"build/", "test/", ".git/"},
	})

	var g errgroup.Group

	for _, fn := range tests {
		fn := fn
		g.Go(func() error {
			return fn(ctx, client, dir)
		})
	}

	return g.Wait()
}

func pythonBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("python:3.11-bookworm").
		WithExec([]string{"pip", "install", "poetry==1.7.0"}).
		WithDirectory("/src", hostDirectory.Directory("flipt-python")).
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

func csharpBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("mcr.microsoft.com/dotnet/sdk:8.0").
		WithDirectory("/src", hostDirectory.Directory("flipt-csharp")).
		WithWorkdir("/src").
		WithExec([]string{"dotnet", "build", "-c", "Release"}).
		WithExec([]string{"dotnet", "pack", "-c", "Release", "-o", "bin/Release"})

	var err error

	if !push {
		_, err = container.Sync(ctx)
		return err
	}

	if os.Getenv("NUGET_API_KEY") == "" {
		return fmt.Errorf("NUGET_API_KEY is not set")
	}

	nugetAPIKeySecret := client.SetSecret("nuget-api-key", os.Getenv("NUGET_API_KEY"))

	_, err = container.WithSecretVariable("NUGET_API_KEY", nugetAPIKeySecret).
		WithExec([]string{"sh", "-c", "dotnet nuget push bin/Release/*.nupkg --api-key $NUGET_API_KEY --source https://api.nuget.org/v3/index.json --skip-duplicate"}).
		Sync(ctx)

	return err
}

func rustBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("rust:1.90.0-bookworm").
		WithDirectory("/src", hostDirectory.Directory("flipt-rust"), dagger.ContainerWithDirectoryOpts{
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
		WithExec([]string{"sh", "-c", "cargo publish --token $CRATES_TOKEN"}).
		Sync(ctx)

	return err
}

func nodeBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("node:21.2-bookworm").
		WithDirectory("/src", hostDirectory.Directory("flipt-node"), dagger.ContainerWithDirectoryOpts{
			Exclude: []string{".node_modules/"},
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

func javaBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	container := client.Container().From("gradle:8.5.0-jdk11").
		WithDirectory("/src", hostDirectory.Directory("flipt-java")).
		WithWorkdir("/src").
		WithExec([]string{"./gradlew", "-x", "test", "build"})

	var err error

	if !push {
		_, err = container.Sync(ctx)
		return err
	}

	if os.Getenv("SONATYPE_PORTAL_USERNAME") == "" {
		return fmt.Errorf("SONATYPE_PORTAL_USERNAME is not set")
	}
	if os.Getenv("SONATYPE_PORTAL_PASSWORD") == "" {
		return fmt.Errorf("SONATYPE_PORTAL_PASSWORD is not set")
	}
	if os.Getenv("PGP_PRIVATE_KEY") == "" {
		return fmt.Errorf("PGP_PRIVATE_KEY is not set")
	}
	if os.Getenv("PGP_PASSPHRASE") == "" {
		return fmt.Errorf("PGP_PASSPHRASE is not set")
	}

	var (
		sonatypeUsername = client.SetSecret("sonatype-username", os.Getenv("SONATYPE_PORTAL_USERNAME"))
		sonatypePassword = client.SetSecret("sonatype-password", os.Getenv("SONATYPE_PORTAL_PASSWORD"))
		pgpPrivateKey    = client.SetSecret("pgp-private-key", os.Getenv("PGP_PRIVATE_KEY"))
		pgpPassphrase    = client.SetSecret("pgp-passphrase", os.Getenv("PGP_PASSPHRASE"))
	)

	_, err = container.WithSecretVariable("SONATYPE_PORTAL_USERNAME", sonatypeUsername).
		WithSecretVariable("SONATYPE_PORTAL_PASSWORD", sonatypePassword).
		WithSecretVariable("PGP_PRIVATE_KEY", pgpPrivateKey).
		WithSecretVariable("PGP_PASSPHRASE", pgpPassphrase).
		WithExec([]string{"./gradlew", "publish"}).
		Sync(ctx)

	return err
}

func phpBuild(ctx context.Context, client *dagger.Client, hostDirectory *dagger.Directory) error {
	if tag == "" {
		return fmt.Errorf("tag is not set")
	}

	const tagPrefix = "refs/tags/flipt-php-"

	if !strings.HasPrefix(tag, tagPrefix) {
		return fmt.Errorf("tag %q must start with %q", tag, tagPrefix)
	}

	// because of how Composer works, we need to create a new repo that contains
	// only the php client code.
	targetRepo := os.Getenv("TARGET_REPO")
	if targetRepo == "" {
		targetRepo = "https://github.com/flipt-io/flipt-php.git"
	}

	targetTag := strings.TrimPrefix(tag, tagPrefix)

	pat := os.Getenv("GITHUB_TOKEN")
	if pat == "" {
		return errors.New("GITHUB_TOKEN environment variable must be set")
	}

	var (
		encodedPAT = base64.URLEncoding.EncodeToString([]byte("pat:" + pat))
		ghToken    = client.SetSecret("gh-token", encodedPAT)
	)

	gitUserName := os.Getenv("GIT_USER_NAME")
	if gitUserName == "" {
		gitUserName = "flipt-bot"
	}

	gitUserEmail := os.Getenv("GIT_USER_EMAIL")
	if gitUserEmail == "" {
		gitUserEmail = "dev@flipt.io"
	}

	git := client.Container().From("golang:1.21.3-bookworm").
		WithSecretVariable("GITHUB_TOKEN", ghToken).
		WithExec([]string{"git", "config", "--global", "user.email", gitUserEmail}).
		WithExec([]string{"git", "config", "--global", "user.name", gitUserName}).
		WithExec([]string{"sh", "-c", `git config --global http.https://github.com/.extraheader "AUTHORIZATION: Basic ${GITHUB_TOKEN}"`})

	repository := git.
		WithExec([]string{"git", "clone", "https://github.com/flipt-io/flipt-server-sdks.git", "--branch", "flipt-php-" + targetTag, "/src"}).
		WithWorkdir("/src")

	if !push {
		_, err := repository.Sync(ctx)
		return err
	}

	gitCmd := fmt.Sprintf("git push %s `git subtree split --prefix flipt-php`:refs/heads/main --force", targetRepo)
	_, err := repository.WithExec([]string{"sh", "-c", gitCmd}).
		Sync(ctx)
	if err != nil {
		return err
	}

	// tag the release
	_, err = git.WithExec([]string{"git", "clone", targetRepo, "/dst"}).
		WithWorkdir("/dst").
		WithExec([]string{"git", "tag", targetTag}).
		WithExec([]string{"git", "push", "origin", targetTag}).
		Sync(ctx)
	return err
}
