# Flipt Server SDK Issue Health

The `flipt-server-sdks` repository contains Flipt server-side SDKs. Each SDK
talks to an upstream Flipt server over HTTP and supports variant, boolean,
batch, get-flag, and list-flags operations.

Analyze newly opened issues for actionability and routing, not code review.

## Repository-specific issue types

Treat requests for a new server-side SDK language as `feature` issues when the
issue asks for a language that is not currently supported in this repository. If
`targetRepoLabels` contains `new-language`, suggest that label for these issues.

Current SDK directories are:

- `flipt-python/`
- `flipt-node/`
- `flipt-java/`
- `flipt-rust/`
- `flipt-php/`
- `flipt-csharp/`

## Actionability checks

For bug reports, prefer issues that include:

- SDK language and package version.
- Flipt server version and whether Flipt V1 or V2 environments are used.
- The evaluation or flag operation involved.
- Minimal flag/rule data or request/response details needed to reproduce.
- Authentication mode, custom headers, and relevant environment header usage when
  those are part of the behavior.

For feature requests, identify the affected SDKs and whether the requested
behavior should be consistent across all server-side SDKs.
