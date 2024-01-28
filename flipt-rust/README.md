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
