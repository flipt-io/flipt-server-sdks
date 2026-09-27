# Flipt Python

[![pypi](https://img.shields.io/pypi/v/flipt.svg)](https://pypi.org/project/flipt)

This directory contains the Python source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

```sh
pip install flipt=={version}
```

## Usage

In your Python code you can import this client and use it as so:

```python
from flipt import FliptClient
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest

flipt_client = FliptClient()

variant_flag = flipt_client.evaluation.variant(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flagll",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)

print(variant_flag)
```

There is a more detailed example in the [examples](./examples) directory.

### Setting HTTP Headers

You can set custom HTTP headers for the client by using the `headers` parameter in the constructor.

```python
flipt_client = FliptClient(headers={"X-Custom-Header": "Custom-Value"})
```

### Flipt V2 Environment Support

Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

```python
flipt_client = FliptClient(headers={"X-Flipt-Environment": "production"})
```

## Upgrading to 2.0

Version 2.0 replaces the `httpx` dependency with [`httpx2`](https://github.com/pydantic/httpx2), Pydantic's maintained fork with the same API. The Flipt API surface does not change. These details do:

- Transport errors such as connection refused, timeouts, and Transport Layer Security (TLS) failures are now `httpx2` exceptions, for example `httpx2.ConnectError` and `httpx2.TimeoutException`. Update `except` clauses that name `httpx` exceptions to the `httpx2` equivalents. `flipt.exceptions.FliptApiError` does not change.
- The `httpx_client` argument of `Evaluation`, `AsyncEvaluation`, `SyncFlag`, and `AsyncFlag` must be an `httpx2.Client` or `httpx2.AsyncClient`.
- `flipt` no longer installs `httpx`. If your project imports `httpx` directly, declare it yourself. `httpx` and `httpx2` install side by side.
- The client verifies TLS certificates against the trust store of your operating system instead of the `certifi` bundle. Private certificate authorities installed in that store work without extra configuration. `SSL_CERT_FILE`, `SSL_CERT_DIR`, and `verify=` on a custom client still work. In containers without operating system certificates, such as distroless images, set `SSL_CERT_FILE` to a certificate bundle.

## For developers

After adding new code, please don't forget to add unit tests for new features.
To format the code, check it with linters and run tests, use the `make check` command.

Please keep the Python [PEP8](https://peps.python.org/pep-0008/) in mind while adding new code.
