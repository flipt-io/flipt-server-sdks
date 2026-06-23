# Agent Personas

This directory contains repo-specific reviewer personas for the automated PR
review workflow. They are Markdown prompts consumed by `flipt-io/agents` as local
review overrides.

## Available personas

- `server-sdk-reviewer.md` — server-side HTTP SDK reviewer for Python, Node.js,
  Java, Rust, PHP, C#, and the shared Dagger integration test harness.

Path routing lives in `.agents/pr-review/prompts/sdk-review-routing.md`; keep
this file as an index so SDK directory changes have one source of truth.

## Conventions

- Produce one combined PR review; do not ask for separate reviews per SDK.
- Cite `file:line` for every finding.
- Separate blocking issues from suggestions.
- Defer mechanical formatting and lint-only findings to the existing CI jobs.
