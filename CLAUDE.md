# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a monorepo for Flipt server-side SDKs. Each SDK is a client that evaluates feature flags by communicating with an upstream Flipt server via HTTP. The SDKs support variant, boolean, and batch evaluations.

## Repository Structure

- `flipt-python/` - Python SDK (uses Poetry)
- `flipt-node/` - TypeScript/Node.js SDK (uses npm)
- `flipt-java/` - Java SDK (uses Gradle)
- `flipt-rust/` - Rust SDK (uses Cargo)
- `flipt-php/` - PHP SDK (uses Composer)
- `flipt-csharp/` - C# SDK (uses .NET)
- `test/` - Dagger-based integration tests in Go
- `test/fixtures/testdata/` - Test fixtures used by integration tests

## SDK Architecture

Each SDK follows a consistent pattern:

1. **FliptClient** - Main client class that initializes evaluation and flag clients
2. **Authentication** - Supports ClientToken and JWT authentication strategies
3. **Evaluation Client** - Handles variant, boolean, and batch evaluations
4. **Flag Client** - Handles getFlag and listFlags operations
5. **Models/DTOs** - Request and response types for each operation

### Common Client Methods

All SDKs implement these core methods:
- Variant evaluation
- Boolean evaluation
- Batch evaluation
- Get flag
- List flags

### Flipt V2 Support

Most SDKs support Flipt V2 environments via the `X-Flipt-Environment` header. The Go SDK (maintained in the main Flipt repo) does not yet support V2.

## Development Commands

### Python (flipt-python)
```bash
cd flipt-python
poetry install
make lint        # Run ruff and black checks
make format      # Auto-format with ruff and black
make test        # Run tests with coverage
make testcov     # Generate HTML coverage report
```

### Node.js (flipt-node)
```bash
cd flipt-node
npm install
npm run lint     # Check formatting with prettier
npm run fmt      # Auto-format with prettier
npm run build    # Compile TypeScript
npm test         # Run Jest tests
```

### Java (flipt-java)
```bash
cd flipt-java
./gradlew spotlessCheck   # Check code formatting
./gradlew spotlessApply   # Auto-format code
./gradlew test            # Run tests
./gradlew build           # Build the project
```

### Rust (flipt-rust)
```bash
cd flipt-rust
cargo fmt --all -- --check              # Check formatting
cargo fmt --all                         # Auto-format
cargo clippy --all -- -D warnings       # Run linter
cargo test                              # Run unit tests
cargo test --features flipt_integration --test integration  # Run integration tests
```

### PHP (flipt-php)
```bash
cd flipt-php
composer install
composer test    # Run PHPUnit tests
```

### C# (flipt-csharp)
```bash
cd flipt-csharp
dotnet test      # Run tests
dotnet build     # Build the project
```

## Testing

### Integration Tests

Integration tests use Dagger to run each SDK against a containerized Flipt server. Tests are defined in `test/main.go`.

Run all integration tests:
```bash
dagger run go run ./test
```

Run tests for specific SDK(s):
```bash
dagger run go run ./test --sdks=python
dagger run go run ./test --sdks=python,node,rust
```

Requirements: Docker, Go 1.21.4+, and Dagger 0.9.5+ must be installed.

### Test Configuration

Integration tests use these environment variables:
- `FLIPT_URL` - Set to `http://flipt:8080` (service binding in container)
- `FLIPT_AUTH_TOKEN` - Set to `secret` (bootstrap token)

Test fixtures are located in `test/fixtures/testdata/` and mounted into the Flipt container.

## Release Process

- Uses Release Please for automated releases
- Each SDK is independently versioned (see `release-please-config.json` and `.release-please-manifest.json`)
- Commits must follow Conventional Commits format
- All commits must be signed off (use `git commit -s`)

## Adding a New Language SDK

1. Create a new `flipt-{language}` directory
2. Follow the SDK architecture pattern (FliptClient, Evaluation, Flag clients)
3. Implement the 5 core methods (variant, boolean, batch, getFlag, listFlags)
4. Add tests in the SDK's test directory
5. Update `test/main.go` to add integration test function
6. Update root `README.md` to list the new SDK
7. Add Dependabot configuration in `.github/dependabot.yml`
8. Update `release-please-config.json` with new package entry

## CI/CD

- `.github/workflows/lint-sdks.yml` - Runs language-specific linters for each SDK
- `.github/workflows/test-sdks.yml` - Runs Dagger integration tests
- `.github/workflows/package-*.yml` - Publishes SDKs to package registries
- `.github/workflows/release-please.yml` - Manages releases via Release Please

## Important Notes

- The Go SDK is maintained separately in the main Flipt repository
- All SDKs default to `http://localhost:8080` for the Flipt server URL
- Authentication is optional but recommended via ClientToken or JWT strategies
- Custom headers can be set on all clients for additional use cases
