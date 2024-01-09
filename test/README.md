# Integration Tests

The different languages clients should all have an integration test suite that is dependent on an upstream Flipt server protected by auth.

In the `test/` directory we will use [Dagger](https://dagger.io/) to orchestrate setting up the dependencies for running the test suites for the different languages.

## Requirements

Make sure you have `dagger` installed. This module is pinned to `v0.9.5` currently.

Here are the [Dagger Installation Instructions](https://docs.dagger.io/quickstart/729236/cli).

## Running Tests

From the root of this repository you can run:

```bash
dagger run go run ./test
```

This will run integration tests for every language that is supported. If you wish to filter specific languages to test, there is a flag `-languages` which accepts a comma-separated list of values.

e.g.

```bash
dagger run go run ./test -languages=python,rust,node
```
