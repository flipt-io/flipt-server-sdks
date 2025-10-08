# Flipt Server SDKs

This repository centralizes the server-side SDKs for [Flipt](https://github.com/flipt-io/flipt).

These server-side SDKs are responsible for evaluating context and returning the results of the evaluation. Each client interacts directly with an upstream Flipt server and can perform any of the three of the following evaluation operations via HTTP:

1. [Variant](https://www.flipt.io/docs/reference/evaluation/variant-evaluation)
2. [Boolean](https://www.flipt.io/docs/reference/evaluation/boolean-evaluation)
3. [Batch](https://www.flipt.io/docs/reference/evaluation/batch-evaluation)

## Language Support

We are constantly growing our list of clients. Currently, we support the following languages:

| Language | Version | Package Registry |
|----------|---------|------------------|
| [Python](./flipt-python) | [![PyPI](https://img.shields.io/pypi/v/flipt?label=flipt)](https://pypi.org/project/flipt/) | PyPI |
| [Node.js](./flipt-node) | [![npm](https://img.shields.io/npm/v/@flipt-io/flipt?label=@flipt-io/flipt)](https://www.npmjs.com/package/@flipt-io/flipt) | npm |
| [Java](./flipt-java) | [![Maven Central](https://img.shields.io/maven-central/v/io.flipt/flipt-java?label=flipt-java)](https://central.sonatype.com/artifact/io.flipt/flipt-java) | Maven Central |
| [Rust](./flipt-rust) | [![Crates.io](https://img.shields.io/crates/v/flipt?label=flipt)](https://crates.io/crates/flipt) | crates.io |
| [PHP](./flipt-php) | [![Packagist](https://img.shields.io/packagist/v/flipt-io/flipt?label=flipt-io/flipt)](https://packagist.org/packages/flipt-io/flipt) | Packagist |
| [C#](./flipt-csharp) | [![NuGet](https://img.shields.io/nuget/v/Flipt?label=Flipt)](https://www.nuget.org/packages/Flipt/) | NuGet |
| [Go](https://github.com/flipt-io/flipt/tree/main/sdk/go) | [![GitHub](https://img.shields.io/github/v/tag/flipt-io/flipt?filter=sdk/go/*&label=flipt-sdk-go)](https://github.com/flipt-io/flipt/tree/main/sdk/go) | GitHub |

> [!NOTE]
> The Go client is maintained in the main Flipt repository.

Want to see a client in a language we don't support? [Open an issue](https://github.com/flipt-io/flipt-server-sdks/issues/new?assignees=&labels=new-language&projects=&template=new_language.yml) and let us know!

## Flipt V2 Support

Almost all clients support Flipt V2 for evaluation. Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

The current clients that do not support Flipt V2 are:

- [Go](https://github.com/flipt-io/flipt/tree/main/sdk/go)

These clients will be updated to support Flipt V2 in the future.

## Installation

See each client's README for installation and usage instructions.

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md)

## License

All code in this repository is licensed under the [MIT License](./LICENSE).
