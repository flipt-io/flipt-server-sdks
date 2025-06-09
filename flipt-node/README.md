# Flipt Node

[![npm](https://img.shields.io/npm/v/@flipt-io/flipt?label=%40flipt-io%2Fflipt)](https://www.npmjs.com/package/@flipt-io/flipt)

This directory contains the TypeScript source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

```sh
npm i @flipt-io/flipt@{version}
```

## Usage

In your Node code you can import this client and use it as so:

```typescript
import { FliptClient } from "@flipt-io/flipt";

const fliptClient = new FliptClient();

async function evaluate() {
  const variantEvaluationResponse = await fliptClient.evaluation.variant({
    namespaceKey: "default",
    flagKey: "flag1",
    entityId: "entity",
    context: { fizz: "buzz" }
  });

  console.log(variantEvaluationResponse);
}
```

There is a more detailed example in the [examples](./examples) directory.

### Setting HTTP Headers

You can set custom HTTP headers for the client by using the `headers` parameter in the constructor.

```typescript
const fliptClient = new FliptClient({
  headers: { "X-Custom-Header": "Custom-Value" }
});
```

### Flipt V2 Environment Support

Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

```typescript
const fliptClient = new FliptClient({
  headers: { "X-Flipt-Environment": "production" }
});
```

### Metrics

There is support for [Datadog RUM](https://docs.datadoghq.com/real_user_monitoring/) through this client. This allows you to track the values of feature flag evaluation and how it relates to active browser sessions.

You can first install the datadog RUM client like so:

```
npm install --save @datadog/browser-rum
```

To start tracking feature flags on Datadog:

```typescript
import { datadogRum } from "@datadog/browser-rum";
import { FliptClient, FliptMetrics } from "@flipt-io/flipt";

datadogRum.init({
  applicationId: "<APPLICATION_ID>",
  clientToken: "<CLIENT_TOKEN>",
  site: "datadoghq.com",
  service: "<SERVICE_NAME>",
  env: "<ENV_NAME>",
  enableExperimentalFeatures: ["feature_flags"],
  sessionSampleRate: 100,
  sessionReplaySampleRate: 20,
  trackUserInteractions: true,
  trackResources: true,
  trackLongTasks: true,
  defaultPrivacyLevel: "mask-user-input"
});

datadogRum.startSessionReplayRecording();

const metricsClient = new FliptMetrics(
  new FliptClient({
    url: "http://localhost:8080"
  }).evaluation,
  datadogRum
);

const response = await metricsClient.variant({
  namespaceKey: "default",
  flagKey: "hello-this",
  entityId: uuidv4(),
  context: {}
});
```
