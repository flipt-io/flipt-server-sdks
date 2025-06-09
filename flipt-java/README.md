# Flipt Java

[![Maven Central](https://img.shields.io/maven-central/v/io.flipt/flipt-java)](https://central.sonatype.com/artifact/io.flipt/flipt-java)

This directory contains the Java source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

### Gradle

Add the dependency in your `build.gradle`:

```groovy
dependencies {
    implementation 'io.flipt:flipt-java:1.x.x'
}
```

### Maven

Add the dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>io.flipt</groupId>
    <artifactId>flipt-java</artifactId>
    <version>1.x.x</version>
</dependency>
```

## Usage

In your Java code you can import this client and use it as so:

```java
import io.flipt.api.FliptClient;
import io.flipt.api.evaluation.models.*;

public class Main {
  public static void main(String[] args) {
    FliptClient fliptClient = FliptClient.builder().build();
    Map<String, String> context = new HashMap<>();

    context.put("fizz", "buzz");

    EvaluationRequest variantEvaluationRequest =
        EvaluationRequest.builder()
            .namespaceKey("default")
            .flagKey("flag1")
            .entityId("entity")
            .context(context)
            .build();

    EvaluationResponse variantEvaluationResponse = fliptClient.evaluate(variantEvaluationRequest);
```

There is a more detailed example in the [examples](./src/main/java/examples) directory.

### Setting HTTP Headers

You can set custom HTTP headers for the client by using the `headers` method in the builder.

```java
FliptClient fliptClient = FliptClient.builder().headers(Map.of("X-Custom-Header", "Custom-Value")).build();
```

### Flipt V2 Environment Support

Flipt V2 introduces the concept of [environments](https://docs.flipt.io/v2/concepts#environments). This client supports evaluation of flags in a specific environment by using the `X-Flipt-Environment` header.

```java
FliptClient fliptClient = FliptClient.builder().headers(Map.of("X-Flipt-Environment", "production")).build();
```
