<?php

use PHPUnit\Framework\TestCase;
use Flipt\Client\FliptClient;
use Flipt\Models\ResponseReasons;
use GuzzleHttp\Handler\MockHandler;
use GuzzleHttp\HandlerStack;
use GuzzleHttp\Psr7\Response;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\Exception\RequestException;
use GuzzleHttp\Client;
use Psr\Http\Client\ClientExceptionInterface;

final class FliptClientTest extends TestCase
{
    protected FliptClient $apiClient;

    public function setUp(): void
    {
        $fliptUrl = getenv('FLIPT_URL');

        if (!$fliptUrl) {
            $this->fail('FLIPT_URL environment variable not set');
        }

        $authToken = getenv('FLIPT_AUTH_TOKEN');
        if (!$authToken) {
            $this->fail('FLIPT_AUTH_TOKEN environment variable not set');
        }

        $this->apiClient = new FliptClient($fliptUrl, authentication: new Flipt\Client\ClientTokenAuthentication($authToken));
    }


    public function testBoolean(): void
    {
        $result = $this->apiClient->boolean('flag_boolean', ['fizz' => 'buzz'], 'entity');

        $this->assertTrue($result->getEnabled());
        $this->assertEquals($result->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals('flag_boolean', $result->getFlagKey());
        $this->assertEquals($result->getSegmentKeys(), ['segment1']);

        $value = $this->apiClient->booleanValue('flag_boolean', false, ['fizz' => 'buzz'], 'entity');
        $this->assertTrue($value);
    }


    public function testVariant(): void
    {
        $result = $this->apiClient->variant('flag1', ['fizz' => 'buzz'], 'entity');

        $this->assertTrue($result->getMatch());
        $this->assertEquals($result->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals('flag1', $result->getFlagKey());
        $this->assertEquals($result->getSegmentKeys(), ['segment1']);
        $this->assertEquals($result->getVariantKey(), 'variant1');

        $value = $this->apiClient->variantValue('flag1', 'fallback', ['fizz' => 'buzz'], 'entity');
        $this->assertEquals($value, 'variant1');
    }

    public function testCommunicationError(): void
    {
        $mock = new MockHandler([
            new RequestException('Error Communicating with Server', new Request('POST', '/evaluate/v1/boolean'))
        ]);
        $handlerStack = HandlerStack::create($mock);
        $client = new Client(['handler' => $handlerStack]);
        $apiClient = new FliptClient($client);

        $this->expectException(ClientExceptionInterface::class);
        $this->expectExceptionMessage('Error Communicating with Server');
        $apiClient->boolean('flag1', ['fizz' => 'buzz'], 'entity');
    }

    public function testInvalidJsonData(): void
    {
        $mock = new MockHandler([
            new Response(200, [], '{"Hello": "invalid json')
        ]);
        $handlerStack = HandlerStack::create($mock);
        $client = new Client(['handler' => $handlerStack]);
        $apiClient = new FliptClient($client);

        $this->expectException(JsonException::class);
        $this->expectExceptionMessage('Control character error, possibly incorrectly encoded');
        $apiClient->boolean('flag1', ['fizz' => 'buzz'], 'entity');
    }

    public function testFallback(): void
    {
        $mock = new MockHandler([
            new Response(200, [], '{"Hello": "invalid json'),
            new Response(200, [], '{"Hello": "invalid json')
        ]);
        $handlerStack = HandlerStack::create($mock);
        $client = new Client(['handler' => $handlerStack]);
        $apiClient = new FliptClient($client);

        $result = $apiClient->booleanValue('flag-b', true, ['fizz' => 'buzz'], 'entity', '');
        $this->assertTrue($result);

        $result = $apiClient->variantValue('flag-e', 'variant-a', ['fizz' => 'buzz'], 'entity', '');
        $this->assertEquals($result, 'variant-a');
    }
}
