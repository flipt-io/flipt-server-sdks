---
name: release
description: Use when preparing a release, checking what needs releasing, determining version bumps, auditing unreleased changes across server-side SDKs, or when the user asks "what needs to be released" or "do we need a release".
---

# Release

Audit unreleased changes across Flipt server-side SDKs, determine version bumps,
and identify what Release Please will publish.

## Overview

This monorepo releases each SDK independently with Release Please. Release PRs are
managed by `.github/workflows/release-please.yml`, which reads
`release-please-config.json` and `.release-please-manifest.json`. When a release is
created, the workflow triggers `.github/workflows/package-sdks.yml` for the
released SDK, which dispatches the language-specific package workflow.

This repository has no separate native engine publishing stage. Each SDK talks to
an upstream Flipt server over HTTP and can be evaluated independently unless a
change intentionally touches shared integration tests or repository-wide config.

## SDK reference table

| SDK | Directory | Release Please component | Release type | Package workflow |
|-----|-----------|--------------------------|--------------|------------------|
| Python | `flipt-python/` | `flipt-python` | `python` | `package-python-sdk.yml` |
| Node.js | `flipt-node/` | `flipt-node` | `node` | `package-node-sdk.yml` |
| Java | `flipt-java/` | `flipt-java` | `java` | `package-java-sdk.yml` |
| Rust | `flipt-rust/` | `flipt-rust` | `rust` | `package-rust-sdk.yml` |
| PHP | `flipt-php/` | `flipt-php` | `php` | `package-php-sdk.yml` |
| C# | `flipt-csharp/` | `flipt-csharp` | `simple` | `package-csharp-sdk.yml` |

Current versions live in `.release-please-manifest.json`. Release metadata and
extra files live in `release-please-config.json`.

## Step 1: Inspect Release Please state

```bash
cat .release-please-manifest.json
cat release-please-config.json
```

Confirm the SDK's component name, current version, release type, and any
`extra-files` that must be updated by Release Please.

## Step 2: Audit unreleased commits per SDK

Find recent tags and commits for each SDK component:

```bash
git tag --sort=-creatordate | grep "flipt-python-v" | head -5
git log --oneline <last-python-tag>..HEAD --no-merges -- flipt-python/

git tag --sort=-creatordate | grep "flipt-node-v" | head -5
git log --oneline <last-node-tag>..HEAD --no-merges -- flipt-node/

git tag --sort=-creatordate | grep "flipt-java-v" | head -5
git log --oneline <last-java-tag>..HEAD --no-merges -- flipt-java/

git tag --sort=-creatordate | grep "flipt-rust-v" | head -5
git log --oneline <last-rust-tag>..HEAD --no-merges -- flipt-rust/

git tag --sort=-creatordate | grep "flipt-php-v" | head -5
git log --oneline <last-php-tag>..HEAD --no-merges -- flipt-php/

git tag --sort=-creatordate | grep "flipt-csharp-v" | head -5
git log --oneline <last-csharp-tag>..HEAD --no-merges -- flipt-csharp/
```

Also check shared files that may require coordinated release notes or tests:

```bash
git log --oneline --no-merges -- test/ test/fixtures/ README.md release-please-config.json .github/workflows/
```

## Step 3: Determine version bumps

Use conventional commits and semver:

- `fix:` -> patch bump
- `feat:` -> minor bump
- breaking change marker (`!` or `BREAKING CHANGE`) -> major bump
- docs/chore/test-only changes usually do not require a package release unless
  they affect published metadata or release artifacts

When a change affects multiple SDK directories, evaluate each SDK separately.
Release Please will open or update release PRs only for components with qualifying
commits according to `release-please-config.json`.

## Step 4: Categorize release priority

Present a summary table:

```markdown
## SDK Release Status
| SDK | Current Version | Last Tag | Unreleased Commits | Key Changes | Bump Needed | Priority |
|-----|-----------------|----------|--------------------|-------------|-------------|----------|
```

Use these priority labels:

- **Must release**: user-facing fix/feature in the SDK directory.
- **Should release**: package metadata, compatibility, or shared behavior change
  that should be published soon.
- **No release needed**: no qualifying SDK changes since the last tag.
- **Investigate**: commits are ambiguous and need maintainer judgment.

## Step 5: Verify before release or merge

Run the smallest relevant validation for the SDKs being released:

```bash
# Python
cd flipt-python && make test

# Node.js
cd flipt-node && npm test && npm run build

# Java
cd flipt-java && ./gradlew test

# Rust
cd flipt-rust && cargo test

# PHP
cd flipt-php && composer test

# C#
cd flipt-csharp && dotnet test
```

For behavior expected to be consistent across SDKs, run Dagger integration tests:

```bash
dagger run go run ./test --sdks=python,node,rust
# or all SDKs
dagger run go run ./test
```

## Step 6: Release flow

1. Merge qualifying conventional commits to `main`.
2. Let `.github/workflows/release-please.yml` open or update Release Please PRs.
3. Review each release PR for the expected version bump and changelog entries.
4. Merge the Release Please PR.
5. Confirm the package workflow for the SDK ran successfully:
   - `package-python-sdk.yml`
   - `package-node-sdk.yml`
   - `package-java-sdk.yml`
   - `package-rust-sdk.yml`
   - `package-php-sdk.yml`
   - `package-csharp-sdk.yml`

Do not manually create release tags unless maintainers explicitly decide to bypass
Release Please.
