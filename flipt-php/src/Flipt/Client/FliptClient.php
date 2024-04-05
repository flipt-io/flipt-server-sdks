<?php

namespace Flipt\Client;

use GuzzleHttp\Client;
use Flipt\Models\BooleanEvaluationResult;
use Flipt\Models\VariantEvaluationResult;
use Flipt\Models\DefaultBooleanEvaluationResult;
use Flipt\Models\DefaultVariantEvaluationResult;


final class FliptClient
{
    protected Client $client;
    protected AuthenticationStrategy|null $authentication;
    protected string $namespace;
    protected string $entityId;
    protected array $context;


    public function __construct(string|Client $host, string $namespace = "default", array $context = [], string $entityId = '', AuthenticationStrategy $authentication = null)
    {
        $this->authentication = $authentication;
        $this->namespace = $namespace;
        $this->context = $context;
        $this->entityId = $entityId;
        $this->client = (is_string($host)) ? new Client(['base_uri' => $host]) : $host;
    }


    /**
     * Returns the boolean evaluation result
     */
    public function boolean(string $name, $context = [], $entityId = NULL, $reference = ""): BooleanEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/boolean', $this->mergeRequestParams($name, $context, $entityId, $reference));
        return new DefaultBooleanEvaluationResult($response['flagKey'], $response['enabled'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp']);
    }



    /**
     * Returns the variant evaluation result
     */
    public function variant(string $name, $context = [], $entityId = NULL, $reference = ""): VariantEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/variant', $this->mergeRequestParams($name, $context, $entityId, $reference));
        return new DefaultVariantEvaluationResult($response['flagKey'], $response['match'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp'], $response['segmentKeys'], $response['variantKey'], $response['variantAttachment']);
    }


    /**
     * Batch return evaluation requests
     */
    public function batch(array $names, $context = [], $entityId = NULL, $reference = ""): array
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


    protected function mergeRequestParams(string $name, $context = [], $entityId = NULL, $reference = "")
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
     */
    protected function apiRequest(string $path, array $body = [], string $method = 'POST')
    {
        // merge authentication headers
        $headers = [
            'Accept' => 'application/json',
        ];

        if ($this->authentication) {
            $headers = $this->authentication->authenticate($headers);
        }

        // execute request
        $response = $this->client->request($method, $path, [
            'headers' => $headers,
            'body' => json_encode($body, JSON_FORCE_OBJECT),
        ]);

        return json_decode($response->getBody(), true);
    }


    /**
     * Create a new client with a different namespace
     */
    public function withNamespace(string $namespace)
    {
        return new FliptClient($this->client, $namespace, $this->context, $this->entityId, $this->authentication);
    }

    /**
     * Create a new client with a different context
     */
    public function withContext(array $context)
    {
        return new FliptClient($this->client, $this->namespace, $context, $this->entityId, $this->authentication);
    }

    /**
     * Create a new client with a different authentication strategy
     */
    public function withAuthentication(AuthenticationStrategy $authentication)
    {
        return new FliptClient($this->client, $this->namespace, $this->context, $this->entityId, $authentication);
    }
}

interface AuthenticationStrategy
{
    public function authenticate(array $headers);
}

/**
 * Authenticate with a client token
 * @see https://www.flipt.io/docs/authentication/methods#static-token
 */
class ClientTokenAuthentication implements AuthenticationStrategy
{
    protected string $token;

    public function __construct(string $token)
    {
        $this->token = $token;
    }

    public function authenticate(array $headers)
    {
        $headers['Authorization'] = 'Bearer ' . $this->token;
        return $headers;
    }
}

/**
 * Authenticate with a JWT token
 * @see https://www.flipt.io/docs/authentication/methods#json-web-tokens
 */
class JWTAuthentication implements AuthenticationStrategy
{
    protected string $token;

    public function __construct(string $token)
    {
        $this->token = $token;
    }

    public function authenticate(array $headers)
    {
        $headers['Authorization'] = 'JWT ' . $this->token;
        return $headers;
    }
}
