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

In the [example](./example) directory, there is an example TypeScript program which imports in the flipt client, and uses it appropriately, please refer to that for how to use the client.

### Metrics

There is support for [Datadog RUM](https://docs.datadoghq.com/real_user_monitoring/) through this client. This allows you to track the values of feature flag evaluation and how it relates to active browser sessions.

To start tracking feature flags on Datadog:

```typescript
import { datadogRum } from '@datadog/browser-rum';
import { FliptClient, FliptMetrics } from '@flipt-io/flipt';

datadogRum.init({
  applicationId: '<APPLICATION_ID>',
  clientToken: '<CLIENT_TOKEN>',
  site: 'datadoghq.com',
  service:'<SERVICE_NAME>',
  env:'<ENV_NAME>',
  enableExperimentalFeatures: ["feature_flags"],
  sessionSampleRate:100,
  sessionReplaySampleRate: 20,
  trackUserInteractions: true,
  trackResources: true,
  trackLongTasks: true,
  defaultPrivacyLevel:'mask-user-input'
});
  
datadogRum.startSessionReplayRecording();

const metricsClient = new FliptMetrics(new FliptClient({
  url: "http://localhost:8080",
}).evaluation, datadogRum);

const response = await metricsClient.variant({
  namespaceKey: "default",
  flagKey: "hello-this",
  entityId: uuidv4(),
  context: {},
});
```
