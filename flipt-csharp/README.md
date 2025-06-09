# Flipt C\#

[![Nuget](https://img.shields.io/nuget/v/flipt)](https://www.nuget.org/packages/Flipt/)

This directory contains the C# source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

```console
dotnet add package Flipt --version 0.x.x
```

## Usage

In your C# code you can import this client and use it as so:

```csharp
using Flipt.Authentication;
using Flipt.DTOs;
using Flipt.Utilities;

namespace Flipt.Example;

public class Program
{
    public static async Task Main()
    {
        var fliptClient = FliptClient.Builder()
            .WithUrl("http://localhost:8080")
            .WithAuthentication(new ClientTokenAuthenticationStrategy("Client-Token"))
            .WithTimeout(30)
            .Build();

        Dictionary<string, string> context = new() { { "fizz", "buzz" } };

        var evaluation = fliptClient.Evaluation;

        var variantEvaluation = new EvaluationRequest("default", "flag1", "entity", context);
        var variantEvaluationResponse = await evaluation.EvaluateVariantAsync(variantEvaluation);

        var boolEvaluation = new EvaluationRequest("default", "bool_flag", "entity", context);
        var boolEvaluationResponse = await evaluation.EvaluateBooleanAsync(boolEvaluation);

        var list = new List<EvaluationRequest>
        {
            variantEvaluation,
            boolEvaluation
        };
        var batchEvaluationRequest = new BatchEvaluationRequest(list);
        var batchEvaluationResponse = await evaluation.EvaluateBatchAsync(batchEvaluationRequest);
    }
}
```

### Setting HTTP Headers

You can set custom HTTP headers for the client by using the `WithHeaders` method.

````csharp
var fliptClient = FliptClient.Builder()
    .WithUrl("http://localhost:8080")
    .WithAuthentication(new ClientTokenAuthenticationStrategy("Client-Token"))
    .WithHeaders(new Dictionary<string, string> { { "X-Custom-Header", "Custom-Value" } })
    .Build();

### Flipt V2 Environment Support

Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

```csharp
var fliptClient = FliptClient.Builder()
    .WithUrl("http://localhost:8080")
    .WithAuthentication(new ClientTokenAuthenticationStrategy("Client-Token"))
    .WithHeaders(new Dictionary<string, string> { { "X-Flipt-Environment", "production" } })
    .Build();
````
