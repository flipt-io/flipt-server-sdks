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
    protected string $apiToken;
    protected string $namespace;
    protected string $entityId;
    protected array $context;


    public function __construct(string|Client $host, string $apiToken, string $namespace, array $context = [], string $entityId = '')
    {
        $this->apiToken = $apiToken;
        $this->namespace = $namespace;
        $this->context = $context;
        $this->entityId = $entityId;

        $this->client = (is_string($host)) ? new Client(['base_uri' => $host]) : $host;
    }


    /**
     * Returns the boolean evaluation result
     */
    public function boolean(string $name, $context = [], $entityId = NULL): BooleanEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/boolean', $this->mergeRequestParams($name, $context, $entityId));
        return new DefaultBooleanEvaluationResult($response['enabled'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp']);
    }



    /**
     * Returns the variant evaluation result
     */
    public function variant(string $name, $context = [], $entityId = NULL): VariantEvaluationResult
    {
        $response = $this->apiRequest('/evaluate/v1/variant', $this->mergeRequestParams($name, $context, $entityId));
        return new DefaultVariantEvaluationResult($response['match'], $response['reason'], $response['requestDurationMillis'], $response['requestId'], $response['timestamp'], $response['segmentKeys'], $response['variantKey'], $response['variantAttachment']);
    }


    /**
     * Batch return evaluation requests
     */
    public function batch(array $names, $context = [], $entityId = NULL): array
    {

        $response = $this->apiRequest('/evaluate/v1/batch', [
            'requests' => array_map(function ($name) use ($context, $entityId) {
                return $this->mergeRequestParams($name, $context, $entityId);
            }, $names)
        ]);


        // map all responses to corresponding results
        return array_map(function ($resp) {

            if ($resp['type'] == 'VARIANT_EVALUATION_RESPONSE_TYPE') {
                // get the variant response
                $vr = $resp['variantResponse'];
                return new DefaultVariantEvaluationResult($vr['match'], $vr['reason'], $vr['requestDurationMillis'], $vr['requestId'], $vr['timestamp'], $vr['segmentKeys'], $vr['variantKey'], $vr['variantAttachment']);
            }

            if ($resp['type'] == 'BOOLEAN_EVALUATION_RESPONSE_TYPE') {
                // get the boolean response
                $vr = $resp['booleanResponse'];
                return new DefaultBooleanEvaluationResult($vr['enabled'], $vr['reason'], $vr['requestDurationMillis'], $vr['requestId'], $vr['timestamp']);
            }

            return null;
        }, $response['responses']);
    }


    protected function mergeRequestParams(string $name, $context = [], $entityId = NULL)
    {
        return [
            'context' => array_merge($this->context, $context),
            'entityId' => isset($entityId) ? $entityId : $this->entityId,
            'flagKey' => $name,
            'namespaceKey' => $this->namespace,
        ];
    }



    /**
     * Helper function to perform a guzzle request with the correct headers and body
     */
    protected function apiRequest(string $path, array $body = [], string $method = 'POST')
    {

        // execute request
        $response = $this->client->request($method, $path, [
            'headers' => [
                'Authorization' => 'Bearer ' . $this->apiToken,
                'Accept' => 'application/json'
            ],
            'body' => json_encode($body, JSON_FORCE_OBJECT),
        ]);

        return json_decode($response->getBody(), true);
    }


    /**
     * Create a new client with a different namespace
     */
    public function withNamespace(string $namespace)
    {
        return new FliptClient($this->client, $this->apiToken, $namespace, $this->context, $this->entityId);
    }

    /**
     * Create a new client with a different context
     */
    public function withContext(array $context)
    {
        return new FliptClient($this->client, $this->apiToken, $this->namespace, $context, $this->entityId);
    }
}

interface AuthenticationStrategy
{
    public function authenticate(array $headers);
}
