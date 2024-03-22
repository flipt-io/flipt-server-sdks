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


## For developers

After adding new code, please don't forget to add unit tests for new features.
To format the code, check it with linters and run tests, use the `make check` command. 

Please keep the Python [PEP8](https://peps.python.org/pep-0008/) in mind while adding new code.
