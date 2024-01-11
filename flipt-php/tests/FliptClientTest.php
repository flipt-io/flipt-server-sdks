<?php

use PHPUnit\Framework\TestCase;
use GuzzleHttp\Client;
use GuzzleHttp\Middleware;
use GuzzleHttp\HandlerStack;
use GuzzleHttp\Handler\MockHandler;
use GuzzleHttp\Psr7\Response;
use Flipt\Client\FliptClient;
use Flipt\Models\ResponseReasons;

final class FliptClientTest extends TestCase
{

    protected MockHandler $mockHandler;
    protected array $history;
    protected FliptClient $apiClient;

    public function setUp(): void
    {

        $this->mockHandler = new MockHandler();
        $handlerStack = HandlerStack::create($this->mockHandler);

        $this->history = [];
        $handlerStack->push(Middleware::history($this->history));

        $httpClient = new Client([
            'handler' => $handlerStack,
        ]);

        $this->apiClient = new FliptClient($httpClient, 'token', 'namespace', ['context' => 'demo'], 'entityId');
    }



    public function testContextMerge(): void
    {

        $this->queueResponse(['enabled' => true, 'reason' => ResponseReasons::UNKNOWN_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z']);

        $client2 = $this->apiClient->withContext(['context1' => 'one', 'context2' => 'two']);

        $client2->boolean('flag', ['user' => 'demo2', 'context1' => 'new']);
        $payload = $this->getLastPayload();

        $this->assertEquals($payload, [
            'flagKey' => 'flag',
            'namespaceKey' => 'namespace',
            'context' => ['user' => 'demo2', 'context1' => 'new', 'context2' => 'two'],
            'entityId' => 'entityId',
        ]);
    }


    public function testEntityId(): void
    {

        $this->queueResponse(['enabled' => true, 'reason' => ResponseReasons::UNKNOWN_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z']);

        $result = $this->apiClient->boolean('flag', [], 'ENTITY');

        $payload = $this->getLastPayload();
        $this->assertEquals($payload, [
            'flagKey' => 'flag',
            'namespaceKey' => 'namespace',
            'context' => ['context' => 'demo'],
            'entityId' => 'ENTITY',
        ]);
    }


    public function testBoolean(): void
    {
        $this->queueResponse(['enabled' => true, 'reason' => ResponseReasons::MATCH_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z']);

        // execute the client function
        $result = $this->apiClient->boolean('flag');

        // get payload on request to validate
        $payload = $this->getLastPayload();

        $this->assertEquals($payload, [
            'flagKey' => 'flag',
            'namespaceKey' => 'namespace',
            'context' => ['context' => 'demo'],
            'entityId' => 'entityId',
        ]);

        $this->assertTrue($result->getEnabled());
        $this->assertEquals($result->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals($result->getRequestDurationMillis(), 0.39315);
        $this->assertEquals($result->getRequestId(), '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789');
        $this->assertEquals($result->getTimestamp(), '2023-10-31T00:57:47.263242143Z');
    }


    public function testVariant(): void
    {

        $this->queueResponse(['match' => true, 'reason' => ResponseReasons::MATCH_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z', 'segmentKeys' => ['foo', 'bar'], 'variantKey' => 'A', 'variantAttachment' => "{'data':'attachment'}"]);

        // execute the client function
        $result = $this->apiClient->variant('flag');

        // get payload on request to validate
        $payload = $this->getLastPayload();

        $this->assertEquals($payload, [
            'flagKey' => 'flag',
            'namespaceKey' => 'namespace',
            'context' => ['context' => 'demo'],
            'entityId' => 'entityId',
        ]);

        $this->assertTrue($result->getMatch());
        $this->assertEquals($result->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals($result->getRequestDurationMillis(), 0.39315);
        $this->assertEquals($result->getRequestId(), '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789');
        $this->assertEquals($result->getTimestamp(), '2023-10-31T00:57:47.263242143Z');
        $this->assertEquals($result->getSegmentKeys(), ['foo', 'bar']);
        $this->assertEquals($result->getVariantKey(), 'A');
        $this->assertEquals($result->getVariantAttachment(), "{'data':'attachment'}");
    }


    public function testBatch(): void
    {

        $this->queueResponse([
            'responses' => [
                [
                    "type" => "BOOLEAN_EVALUATION_RESPONSE_TYPE",
                    'booleanResponse' => [
                        'enabled' => true, 'reason' => ResponseReasons::MATCH_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z'
                    ]
                ],
                [
                    "type" => "VARIANT_EVALUATION_RESPONSE_TYPE",
                    'variantResponse' => [
                        'match' => true, 'reason' => ResponseReasons::MATCH_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z', 'segmentKeys' => ['foo', 'bar'], 'variantKey' => 'A', 'variantAttachment' => "{'data':'attachment'}"
                    ]
                ],
                [
                    "type" => "VARIANT_EVALUATION_RESPONSE_TYPE",
                    'variantResponse' => [
                        'match' => true, 'reason' => ResponseReasons::MATCH_EVALUATION_REASON, 'requestDurationMillis' => 123456, 'requestId' => '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789', 'requestDurationMillis' => 0.39315, 'timestamp' => '2023-10-31T00:57:47.263242143Z', 'segmentKeys' => ['foo', 'bar'], 'variantKey' => 'A', 'variantAttachment' => "{'data':'attachment'}"
                    ]
                ],
            ]
        ]);

        // execute the client function
        $results = $this->apiClient->batch( [ 'flag1', 'flag2', 'flag3' ], [ 'local' => 'context' ], 'entity' );

        // get payload on request to validate
        $payload = $this->getLastPayload();

        $this->assertEquals($payload, [
            'requests' => [
                [ 'flagKey' => 'flag1', 'namespaceKey' => 'namespace', 'context' => ['context' => 'demo', 'local' => 'context' ], 'entityId' => 'entity' ],
                [ 'flagKey' => 'flag2', 'namespaceKey' => 'namespace', 'context' => ['context' => 'demo', 'local' => 'context' ], 'entityId' => 'entity' ],
                [ 'flagKey' => 'flag3', 'namespaceKey' => 'namespace', 'context' => ['context' => 'demo', 'local' => 'context' ], 'entityId' => 'entity' ],
            ]
        ]);

        $this->assertEquals(count( $results ), 3);
        
        // flag1 
        $this->assertTrue($results[0]->getEnabled());
        $this->assertEquals($results[0]->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals($results[0]->getRequestDurationMillis(), 0.39315);
        $this->assertEquals($results[0]->getRequestId(), '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789');
        $this->assertEquals($results[0]->getTimestamp(), '2023-10-31T00:57:47.263242143Z');

        // flag 2
        $this->assertTrue($results[1]->getMatch());
        $this->assertEquals($results[1]->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals($results[1]->getRequestDurationMillis(), 0.39315);
        $this->assertEquals($results[1]->getRequestId(), '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789');
        $this->assertEquals($results[1]->getTimestamp(), '2023-10-31T00:57:47.263242143Z');
        $this->assertEquals($results[1]->getSegmentKeys(), ['foo', 'bar']);
        $this->assertEquals($results[1]->getVariantKey(), 'A');
        $this->assertEquals($results[1]->getVariantAttachment(), "{'data':'attachment'}");

        // flag 3
        $this->assertTrue($results[2]->getMatch());
        $this->assertEquals($results[2]->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals($results[2]->getRequestDurationMillis(), 0.39315);
        $this->assertEquals($results[2]->getRequestId(), '621e48e2-9127-4309-b786-3bfa5885f4bc"23456789');
        $this->assertEquals($results[2]->getTimestamp(), '2023-10-31T00:57:47.263242143Z');
        $this->assertEquals($results[2]->getSegmentKeys(), ['foo', 'bar']);
        $this->assertEquals($results[2]->getVariantKey(), 'A');
        $this->assertEquals($results[2]->getVariantAttachment(), "{'data':'attachment'}");
    }

    protected function getLastPayload()
    {
        return json_decode($this->history[0]['request']->getBody()->getContents(), true);
    }

    protected function queueResponse(array $response)
    {
        $this->mockHandler->append(new Response(200, [], json_encode($response)));
    }
}
