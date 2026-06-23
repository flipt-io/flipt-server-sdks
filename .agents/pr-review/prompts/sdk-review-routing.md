# Flipt Server SDK PR Review Routing

This repository contains Flipt server-side SDKs that communicate with an upstream
Flipt server over HTTP. Produce **one combined PR review** for the pull request.
Do not post separate reviews per language or package.

## Changed-path routing

Apply the local server SDK reviewer persona when these paths are touched:

- `.agents/pr-review/personas/server-sdk-reviewer.md` for `flipt-python/`,
  `flipt-node/`, `flipt-java/`, `flipt-rust/`, `flipt-php/`,
  `flipt-csharp/`, and `test/`.

If a PR touches multiple SDKs, synthesize the relevant findings into one review.
If a PR touches docs, release tooling, GitHub Actions, root configuration,
dependency automation, `release-please-config.json`, `.release-please-manifest.json`,
or other repository metadata, review those changes directly using `AGENTS.md` plus
the central `flipt-io/agents` code-review skill guidance.

## Review priorities

Prioritize findings that affect:

1. Evaluation correctness across variant, boolean, and batch operations.
2. Flag API correctness for get-flag and list-flags operations.
3. Public SDK API compatibility, option names, defaults, response shapes, and
   documented behavior.
4. Authentication and request metadata: client tokens, JWTs, custom headers, and
   the `X-Flipt-Environment` header used for Flipt V2 environments.
5. HTTP behavior: base URL handling, timeouts, retries or cancellation when
   present, request serialization, response parsing, and error mapping.
6. Packaging and release impact: version files, package metadata, generated
   artifacts, publish workflows, and Release Please configuration.
7. Tests that prove behavior users observe, including Dagger integration coverage
   when behavior should be consistent across SDKs.

Do not spend review budget on mechanical formatting or lint findings that this
repo's CI already checks. Flag style only when it hides a real correctness, API,
or maintenance problem.
