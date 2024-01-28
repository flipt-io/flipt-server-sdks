# Flipt PHP

[![Packagist Version](https://img.shields.io/packagist/v/flipt-io/flipt)](https://packagist.org/packages/flipt-io/flipt)

This directory contains the PHP source code for the Flipt [server-side](https://www.flipt.io/docs/integration/server/rest) client.

> [!NOTE]
> If you are on the <https://github.com/flipt-io/flipt-php> repository, this is a mirror of the source code. Please file issues and pull requests against the [flipt-io/flipt-server-sdks](https://github.com/flipt-io/flipt-server-sdks) repository.

## Requirements

- PHP 8.0 or higher
- [Composer](https://getcomposer.org/)

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Installation

```Bash
composer install flipt-io/flipt
```

## Usage

In your PHP code you can import this client and use it as so:

```php
<?php
use Flipt\Client\FliptClient;

$fliptClient = new FliptClient();

$result = $fliptClient->variant('flag1', ['fizz' => 'buzz'], 'entity');
```

There is a more detailed example in the [tests](./tests) directory.

## Thanks :tada:

Thanks to [legoheld](https://github.com/legoheld) for the initial implementation of this client.
