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

fliptClient = FliptClient()

v = fliptClient.evaluation.variant(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flagll",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)

print(v)
```

There is a more detailed example in the [examples](./examples) directory.
