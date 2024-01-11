# Flipt PHP Library

[![Packagist Version](https://img.shields.io/packagist/v/flipt-io/flipt)](https://packagist.org/packages/flipt-io/flipt)
![beta](https://img.shields.io/badge/status-beta-yellow)

This client is a wrapper around the [Flipt.io](https://www.flipt.io) REST API to easily evaluate flags with a given context on a remote Flipt instance.

## Status

This SDK status is `beta`, and there may be breaking changes between versions without a major version update. Therefore, we recommend pinning your installation of this package wherever necessary.

## Requirements

- PHP 8.0 or higher
- [Composer](https://getcomposer.org/)

## Documentation

API documentation is available at <https://www.flipt.io/docs/reference/overview>.

## Install

### Composer

```Bash
composer install flipt-io/flipt
```

## Usage

Instantiate a client with the corresponding settings.

The API token can be generated under `Settings -> API Tokens` within your Flipt instance. See the [documentation](https://www.flipt.io/docs/authentication/overview) for more information.

```php
$flipt = new \Flipt\Client('https://my-flipt.io', '<apiToken>', '<default namespace>', [ 'default' => 'context' ]);

// test on a boolean flag
$resp = $flipt->boolean('my-boolean');

if ($resp->getEnabled()) {
    // do somthing 
}

// test on variant flag
$resp = $flipt->variant('my-variant');
if ($resp->getVariantKey() == 'demo') {
    // do something
}

// get a variant attachment
$array = $resp->getVariantAttachment();

// the returned value is an array and you can access properties like:
if ($array['key'] == 'demo') {
    // do something
}
```

### Context

You can setup the context in the constructor as shown in the example above.

You can also overwrite context values when accessing a flag as the following example shows:

```php
$flipt = new \Flipt\Client('https://my-flipt.io', 'token', 'namespace', [ 'environment' => 'test', 'user' => '23' ]);

// will send the context [ 'environment' => 'test', 'user' => '23' ] as defined in the client
$test = $flipt->boolean('flag'); 

// will send the context [ 'environment' => 'test', 'user' => '50' ] as it will merge the client context with the current from the call
$test2 = $flipt->boolean('flag', [ 'user' => '50' ]);
```

### Namespaces

See our [documentation](https://www.flipt.io/docs/concepts#namespaces) for more information about namespaces.

If you need to query another namespace you can switch the namespace as follows:

```php
// this client will query against the 'test' namespace
$fliptTest = new \Flipt\Client('https://my-flipt.io', 'token', 'test');

// this will create a new client with all the settings from $fliptTest client except the namespace will changed to 'production'
$fliptProd = $fliptTest->withNamespace('production'),

// this will use namespace 'production'
$fliptProd->boolean('flag')
```

## Testing

### Local

```bash
# install composer dependencies
composer install

# execute phpunit
composer run-script test
```

### Docker

Tests can be run in a Docker container like this:

```bash
# install composer dependencies
docker run -v $PWD:/app -w /app composer install

# execute phpunit
docker run -v $PWD:/app -w /app --entrypoint vendor/bin/phpunit php:8-cli
```

## Thanks :tada:

Thanks to [legoheld](https://github.com/legoheld) for the initial implementation of this client.
