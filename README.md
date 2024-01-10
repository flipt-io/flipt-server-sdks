# Flipt Server SDKs

This repository centralizes the server-side SDKs for [Flipt](https://github.com/flipt-io/flipt).

These server-side SDKs are responsible for evaluating context and returning the results of the evaluation. Each client interacts directly with an upstream Flipt server and can perform any of the three of the following evaluation operations via HTTP:

1. [Variant](https://www.flipt.io/docs/reference/evaluation/variant-evaluation)
2. [Boolean](https://www.flipt.io/docs/reference/evaluation/boolean-evaluation)
3. [Batch](https://www.flipt.io/docs/reference/evaluation/batch-evaluation)

## Language Support

We are constantly growing our list of clients. Currently, we support the following languages:

1. [Python](./flipt-python)
2. [NodeJS](./flipt-node)
3. [Java](./flipt-java)
4. [Rust](./flipt-rust)

Want to see a client in a language we don't support? [Open an issue](https://github.com/flipt-io/flipt-server-sdks/issues/new?assignees=&labels=new-language&projects=&template=new_language.yml) and let us know!

## Installation

See each client's README for installation and usage instructions.

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md)

## License

All code in this repository is licensed under the [MIT License](./LICENSE).
