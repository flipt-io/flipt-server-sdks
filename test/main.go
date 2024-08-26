package main

import (
	"bytes"
	"context"
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"
	"flag"
	"fmt"
	"log"
	"maps"
	"math/big"
	"os"
	"strings"
	"time"

	"dagger.io/dagger"
	"github.com/go-jose/go-jose/v3"
	jjwt "github.com/go-jose/go-jose/v3/jwt"
	"github.com/hashicorp/cap/jwt"
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

type containerOpt func(*dagger.Container) *dagger.Container

type integrationTestFn func(context.Context, *dagger.Client, *dagger.Container, *dagger.Directory, ...containerOpt) error

func applyOpts(c *dagger.Container, opts ...containerOpt) *dagger.Container {
	for _, opt := range opts {
		c = opt(c)
	}
	return c
}

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

	flipt, hostDirectory, opts, err := getTestDependencies(ctx, client, dir)
	if err != nil {
		return err
	}

	var g errgroup.Group

	for _, fn := range tests {
		fn := fn
		g.Go(func() error {
			return fn(ctx, client, flipt, hostDirectory, opts...)
		})
	}

	return g.Wait()
}

func getTestDependencies(ctx context.Context, client *dagger.Client, dir *dagger.Directory) (_ *dagger.Container, _ *dagger.Directory, opts []containerOpt, err error) {
	// Flipt
	flipt := client.Container().From("flipt/flipt:latest").
		WithUser("root").
		WithExec([]string{"mkdir", "-p", "/var/data/flipt"}).
		WithDirectory("/var/data/flipt", dir.Directory("test/fixtures/testdata")).
		WithExec([]string{"chown", "-R", "flipt:flipt", "/var/data/flipt"}).
		WithUser("flipt").
		WithEnvVariable("FLIPT_STORAGE_TYPE", "local").
		WithEnvVariable("FLIPT_STORAGE_LOCAL_PATH", "/var/data/flipt").
		WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_TOKEN_ENABLED", "true").
		WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_TOKEN_BOOTSTRAP_TOKEN", "secret").
		WithEnvVariable("FLIPT_AUTHENTICATION_REQUIRED", "true").
		WithExposedPort(8080)

	{
		// K8s auth configuration
		flipt = flipt.
			WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_KUBERNETES_ENABLED", "true")

		var saToken string
		// run an OIDC server which exposes a JWKS url and returns
		// the associated private key bytes
		flipt, saToken, err = serveOIDC(ctx, client, client.Container().
			From("golang:1.23").
			WithMountedDirectory("/src", dir.Directory("test")).
			WithWorkdir("/src").
			WithExec([]string{"go", "mod", "download"}), flipt)
		if err != nil {
			return nil, nil, nil, err
		}

		opts = append(opts, func(c *dagger.Container) *dagger.Container {
			return c.WithNewFile("/var/run/secrets/kubernetes.io/serviceaccount/token", dagger.ContainerWithNewFileOpts{
				Contents: saToken,
			})
		})
	}

	return flipt, dir, opts, nil
}

// pythonTest runs the python integration test suite against a container running Flipt.
func pythonTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) (err error) {
	container := applyOpts(client.Container().From("python:3.11-bookworm").
		WithExec([]string{"pip", "install", "poetry==1.7.1"}).
		WithWorkdir("/src").
		WithDirectory("/src", hostDirectory.Directory("flipt-python")).
		WithServiceBinding("flipt", flipt.WithExec(nil).AsService()).
		WithEnvVariable("FLIPT_URL", "http://flipt:8080").
		WithEnvVariable("FLIPT_AUTH_TOKEN", "secret").
		WithEnvVariable("UNIQUE", time.Now().String()).
		WithExec([]string{"poetry", "install"}), opts...)

	_, err = container.WithExec([]string{"make", "test"}).Sync(ctx)
	return err
}

// nodeTest runs the node integration test suite against a container running Flipt.
func nodeTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) error {
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

func csharpTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) error {
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
func rustTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) error {
	_, err := client.Container().From("rust:1.73.0-bookworm").
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
func javaTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) error {
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
func phpTest(ctx context.Context, client *dagger.Client, flipt *dagger.Container, hostDirectory *dagger.Directory, opts ...containerOpt) error {
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

func signJWT(key crypto.PrivateKey, claims interface{}) string {
	sig, err := jose.NewSigner(
		jose.SigningKey{Algorithm: jose.SignatureAlgorithm(string(jwt.RS256)), Key: key},
		(&jose.SignerOptions{}).WithType("JWT"),
	)
	if err != nil {
		panic(err)
	}

	raw, err := jjwt.Signed(sig).
		Claims(claims).
		CompactSerialize()
	if err != nil {
		panic(err)
	}

	return raw
}

// serveOIDC runs a mini OIDC-style key provider and mounts it as a service onto Flipt.
// This provider is designed to mimic how kubernetes exposes JWKS endpoints for its service account tokens.
// The function creates signing keys and TLS CA certificates which is shares with the provider and
// with Flipt itself. This is to facilitate Flipt using the custom CA to authenticate the provider.
// The function generates two JWTs, one for Flipt to identify itself and one which is returned to the caller.
// The caller can use this as the service account token identity to be mounted into the container with the
// client used for running the test and authenticating with Flipt.
func serveOIDC(_ context.Context, _ *dagger.Client, base, flipt *dagger.Container) (*dagger.Container, string, error) {
	priv, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		return nil, "", err
	}

	rsaSigningKey := &bytes.Buffer{}
	if err := pem.Encode(rsaSigningKey, &pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(priv),
	}); err != nil {
		return nil, "", err
	}

	// generate a SA style JWT for identifying the Flipt service
	fliptSAToken := signJWT(priv, map[string]any{
		"exp": time.Now().Add(24 * time.Hour).Unix(),
		"iss": "https://discover.srv",
		"kubernetes.io": map[string]any{
			"namespace": "flipt",
			"pod": map[string]any{
				"name": "flipt-7d26f049-kdurb",
				"uid":  "bd8299f9-c50f-4b76-af33-9d8e3ef2b850",
			},
			"serviceaccount": map[string]any{
				"name": "flipt",
				"uid":  "4f18914e-f276-44b2-aebd-27db1d8f8def",
			},
		},
	})

	// generate a CA certificate to share between Flipt and the mini OIDC server
	ca := &x509.Certificate{
		SerialNumber: big.NewInt(2019),
		Subject: pkix.Name{
			Organization:  []string{"Flipt, INC."},
			Country:       []string{"US"},
			Province:      []string{""},
			Locality:      []string{"North Carolina"},
			StreetAddress: []string{""},
			PostalCode:    []string{""},
		},
		NotBefore:             time.Now(),
		NotAfter:              time.Now().AddDate(10, 0, 0),
		IsCA:                  true,
		ExtKeyUsage:           []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		KeyUsage:              x509.KeyUsageDigitalSignature | x509.KeyUsageCertSign,
		BasicConstraintsValid: true,
		DNSNames:              []string{"discover.svc"},
	}

	caPrivKey, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		return nil, "", err
	}

	caBytes, err := x509.CreateCertificate(rand.Reader, ca, ca, &caPrivKey.PublicKey, caPrivKey)
	if err != nil {
		return nil, "", err
	}

	var caCert bytes.Buffer
	if err := pem.Encode(&caCert, &pem.Block{
		Type:  "CERTIFICATE",
		Bytes: caBytes,
	}); err != nil {
		return nil, "", err
	}

	var caPrivKeyPEM bytes.Buffer
	pem.Encode(&caPrivKeyPEM, &pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(caPrivKey),
	})

	serviceAccountToken := signJWT(priv, map[string]any{
		"exp": time.Now().Add(24 * time.Hour).Unix(),
		"iss": "https://discover.svc",
		"kubernetes.io": map[string]any{
			"namespace": "integration",
			"pod": map[string]any{
				"name": "integration-test-7d26f049-kdurb",
				"uid":  "bd8299f9-c50f-4b76-af33-9d8e3ef2b850",
			},
			"serviceaccount": map[string]any{
				"name": "myservice",
				"uid":  "4f18914e-f276-44b2-aebd-27db1d8f8def",
			},
		},
	})

	return flipt.
			WithEnvVariable("FLIPT_LOG_LEVEL", "WARN").
			WithEnvVariable("FLIPT_AUTHENTICATION_METHODS_KUBERNETES_DISCOVERY_URL", "https://discover.svc").
			WithServiceBinding("discover.svc", base.
				WithNewFile("/server.crt", dagger.ContainerWithNewFileOpts{Contents: caCert.String()}).
				WithNewFile("/server.key", dagger.ContainerWithNewFileOpts{Contents: caPrivKeyPEM.String()}).
				WithNewFile("/priv.pem", dagger.ContainerWithNewFileOpts{Contents: rsaSigningKey.String()}).
				WithExposedPort(443).
				WithExec([]string{
					"sh",
					"-c",
					"go run ./internal/cmd/discover/... --private-key /priv.pem",
				}).
				AsService()).
			WithNewFile("/var/run/secrets/kubernetes.io/serviceaccount/token", dagger.ContainerWithNewFileOpts{Contents: fliptSAToken}).
			WithNewFile("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt", dagger.ContainerWithNewFileOpts{Contents: caCert.String()}),
		serviceAccountToken, nil
}
