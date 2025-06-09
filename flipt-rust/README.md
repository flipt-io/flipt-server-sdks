# Flipt Rust

[![crates.io](https://img.shields.io/crates/v/flipt.svg)](https://crates.io/crates/flipt)

This directory contains the Rust source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

```sh
cargo add flipt
```

## Usage

In your Rust code you can import this client and use it as so:

```rust
use std::collections::HashMap;

use flipt::api::FliptClient;
use flipt::evaluation::models::EvaluationRequest;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = FliptClient::default();

    let mut context: HashMap<String, String> = HashMap::new();

    context.insert("fizz".into(), "buzz".into());

    let variant_result = client
        .evaluation
        .variant(&EvaluationRequest {
            namespace_key: "default".into(),
            flag_key: "flag1".into(),
            entity_id: "entity".into(),
            context: context.clone(),
            reference: None,
        })
        .await
        .unwrap();

    print!("{:?}", variant_result);
```

There is a more detailed example in the [examples](./examples) directory.

## Setting HTTP Headers

You can set custom HTTP headers for the client by using the `with_headers` method in the `ConfigBuilder`.

```rust
let client = FliptClient::new(ConfigBuilder::default().with_headers(HeaderMap::new()).build());
```

### Flipt V2 Environment Support

Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

```rust
let client = FliptClient::new(ConfigBuilder::default().with_headers(HeaderMap::from_iter([("X-Flipt-Environment".into(), "production".into())])).build());
```
