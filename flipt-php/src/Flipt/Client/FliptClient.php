<?php

namespace Flipt\Client;

use GuzzleHttp\Client;
use Flipt\Models\BooleanEvaluationResult;
use Flipt\Models\VariantEvaluationResult;
use Flipt\Models\DefaultBooleanEvaluationResult;
use Flipt\Models\DefaultVariantEvaluationResult;
use Psr\Log\LoggerInterface;
use Psr\Log\NullLogger;


final class FliptClient
{
    protected Client $client;
    protected AuthenticationStrategy|null $authentication;
    protected string $namespace;
    protected string $entityId;
    protected array $context;
    protected LoggerInterface $logger;
    protected array $headers = [];

    /**
     * @param array<string, string> $context
     */
    public function __construct(string|Client $host, string $namespace = "default", array $context = [], string $entityId = '', AuthenticationStrategy $authentication = null, array $headers = [])
    {
        $this->authentication = $authentication;
        $this->namespace = $namespace;
        $this->context = $context;
        $this->entityId = $entityId;
        $this->client = (is_string($host)) ? new Client(['base_uri' => $host]) : $host;
        $this->logger = new NullLogger();
        $this->headers = $headers;
    }

    /**
     * Set logger to use
     */
    public function setLogger(LoggerInterface $logger) {
        $this->logger = $logger;
    }

    /**
     * Returns the boolean evaluation result
     *
     * @param array<string, string> $context
     *
     * @throws \JsonException if request or response includes invalid json data
     * @throws \Psr\Http\Client\ClientExceptionInterface if network or request error occurs
     */
    public function boolean(string $name, ?array $context = [], ?string $entityId = null, ?string $reference = ""): BooleanEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/boolean', $this->mergeRequestParams($name, $context, $entityId, $reference));
        return new DefaultBooleanEvaluationResult($response['flagKey'], $response['enabled'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp']);
    }


    /**
     * Returns the bool result or default
     *
     * @param string $name - the flag key
     * @param bool $fallback - default value in case of error
     * @param array<string, string> $context
     *
     * @return bool
     */
    public function booleanValue(string $name, bool $fallback, ?array $context = [], ?string $entityId = null, ?string $reference = ""): bool
    {
        try {
            return $this->boolean($name, $context, $entityId, $reference)->getEnabled();
        } catch (\JsonException | \Psr\Http\Client\ClientExceptionInterface $e) {
            $this->logger->error($e->getMessage());
        }
        return $fallback;
    }

    /**
     * Returns the variant evaluation result
     *
     * @param array<string,string> $context
     *
     * @throws \JsonException if request or response includes invalid json data
     * @throws \Psr\Http\Client\ClientExceptionInterface if network or request error occurs
     */
    public function variant(string $name, ?array $context = [], ?string $entityId = null, ?string $reference = ""): VariantEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/variant', $this->mergeRequestParams($name, $context, $entityId, $reference));
        return new DefaultVariantEvaluationResult($response['flagKey'], $response['match'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp'], $response['segmentKeys'], $response['variantKey'], $response['variantAttachment']);
    }


    /**
     * Returns the variant evaluation variantKey or default
     *
     * @param string $name - the flag key
     * @param string $fallback - default value in case of error
     * @param array<string,string> $context
     *
     * @return string
     */
    public function variantValue(string $name, string $fallback, ?array $context = [], ?string $entityId = null, ?string $reference = ""): string
    {
        try {
            return $this->variant($name, $context, $entityId, $reference)->getVariantKey();
        } catch (\JsonException | \Psr\Http\Client\ClientExceptionInterface $e) {
            $this->logger->error($e->getMessage());
        }
        return $fallback;
    }

    /**
     * Batch return evaluation requests
     *
     * @param array<string> $names
     * @param array<string,string> $context
     *
     * @return array<mixed>
     *
     * @throws \JsonException if request or response includes invalid json data
     * @throws \Psr\Http\Client\ClientExceptionInterface if network or request error occurs
     */
    public function batch(array $names, $context = [], ?string $entityId = null, ?string $reference = ""): array
    {

        $response = $this->apiRequest('/evaluate/v1/batch', [
            'requests' => array_map(function ($name) use ($context, $entityId) {
                return $this->mergeRequestParams($name, $context, $entityId);
            }, $names),
            'reference' => $reference
        ]);


        // map all responses to corresponding results
        return array_map(function ($resp) {

            if ($resp['type'] == 'VARIANT_EVALUATION_RESPONSE_TYPE') {
                // get the variant response
                $vr = $resp['variantResponse'];
                return new DefaultVariantEvaluationResult($vr['flagKey'], $vr['match'], $vr['reason'], $vr['requestDurationMillis'], $vr['requestId'], $vr['timestamp'], $vr['segmentKeys'], $vr['variantKey'], $vr['variantAttachment']);
            }

            if ($resp['type'] == 'BOOLEAN_EVALUATION_RESPONSE_TYPE') {
                // get the boolean response
                $vr = $resp['booleanResponse'];
                return new DefaultBooleanEvaluationResult($vr['flagKey'], $vr['enabled'], $vr['reason'], $vr['requestDurationMillis'], $vr['requestId'], $vr['timestamp']);
            }

            return null;
        }, $response['responses']);
    }

    /**
     * @param array<string,string> $context
     *
     * @return array<string,mixed>
     */
    protected function mergeRequestParams(string $name, $context = [], ?string $entityId = null, ?string $reference = "")
    {
        return [
            'context' => array_merge($this->context, $context),
            'entityId' => isset($entityId) ? $entityId : $this->entityId,
            'flagKey' => $name,
            'namespaceKey' => $this->namespace,
            'reference' => $reference,
        ];
    }



    /**
     * Helper function to perform a guzzle request with the correct headers and body
     *
     * @param array<string,mixed> $body
     *
     * @return array<string,mixed>
     *
     * @throws \JsonException if request or response includes invalid json data
     * @throws \Psr\Http\Client\ClientExceptionInterface if network or request error occurs
     */
    protected function apiRequest(string $path, array $body = [], string $method = 'POST')
    {
        // merge authentication headers
        $headers = array_merge(
            ['Accept' => 'application/json'],
            $this->headers
        );

        if ($this->authentication) {
            $headers = $this->authentication->authenticate($headers);
        }

        // execute request
        $response = $this->client->request($method, $path, [
            'headers' => $headers,
            'body' => json_encode($body, JSON_FORCE_OBJECT | JSON_THROW_ON_ERROR),
        ]);

        return json_decode($response->getBody(), true, 512, JSON_THROW_ON_ERROR);
    }


    /**
     * Create a new client with a different namespace
     *
     * @return FliptClient
     */
    public function withNamespace(string $namespace)
    {
        return new FliptClient($this->client, $namespace, $this->context, $this->entityId, $this->authentication, $this->headers);
    }

    /**
     * Create a new client with a different headers
     *
     * @param array<string, string> $headers
     *
     * @return FliptClient
     */
    public function withHeaders(array $headers)
    {
        return new FliptClient($this->client, $this->namespace, $this->context, $this->entityId, $this->authentication, $headers);
    }

    /**
     * Create a new client with a different context
     *
     * @param array<string,string> $context
     *
     * @return FliptClient
     */
    public function withContext(array $context)
    {
        return new FliptClient($this->client, $this->namespace, $context, $this->entityId, $this->authentication, $this->headers);
    }

    /**
     * Create a new client with a different authentication strategy
     *
     * @return FliptClient
     */
    public function withAuthentication(AuthenticationStrategy $authentication)
    {
        return new FliptClient($this->client, $this->namespace, $this->context, $this->entityId, $authentication, $this->headers);
    }
}

interface AuthenticationStrategy
{
    /**
     * @param array<string, mixed> $headers
     *
     * @return array<string, mixed>
     */
    public function authenticate(array $headers);
}

/**
 * Authenticate with a client token
 *
 * @see https://www.flipt.io/docs/authentication/methods#static-token
 */
class ClientTokenAuthentication implements AuthenticationStrategy
{
    protected string $token;

    public function __construct(string $token)
    {
        $this->token = $token;
    }

    /**
     * @inheritDoc
     */
    public function authenticate(array $headers)
    {
        $headers['Authorization'] = 'Bearer ' . $this->token;
        return $headers;
    }
}

/**
 * Authenticate with a JWT token
 *
 * @see https://www.flipt.io/docs/authentication/methods#json-web-tokens
 */
class JWTAuthentication implements AuthenticationStrategy
{
    protected string $token;

    public function __construct(string $token)
    {
        $this->token = $token;
    }

    /**
     * @inheritDoc
     */
    public function authenticate(array $headers)
    {
        $headers['Authorization'] = 'JWT ' . $this->token;
        return $headers;
    }
}
