# Flipt Server SDKs

This repository is a monorepo which contains the source code for SDKs in various languages. Each client interacts directly with an upstream server and can perform one of three of the following operations via HTTP:

1. [Variant](https://github.com/flipt-io/flipt/blob/main/internal/server/evaluation/evaluation.go#L24)
2. [Boolean](https://github.com/flipt-io/flipt/blob/main/internal/server/evaluation/evaluation.go#L90)
3. [Batch](https://github.com/flipt-io/flipt/blob/main/internal/server/evaluation/evaluation.go#L248)

## Language Support

This list is highly subject to change as our list of clients will grow over time. Currently, we support the following languages:

1. [Python](./flipt-client-python)
2. [NodeJS](./flipt-client-node)
3. [Java](./flipt-client-java)
4. [Rust](./flipt-client-rust)

## Installation

Please refer to each individual language client's README to see how you can install and use it.
