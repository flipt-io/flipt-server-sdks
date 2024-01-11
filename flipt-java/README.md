# Flipt Java

This directory contains the Java source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.
This directory contains the Java source code for the Java server side SDK.

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

In the [examples](./examples) directory, there is an example Java program which imports in the flipt client, and uses it appropriately, please refer to that for how to use the client.
