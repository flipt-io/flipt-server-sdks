<?php

use PHPUnit\Framework\TestCase;
use Flipt\Client\FliptClient;
use Flipt\Models\ResponseReasons;

final class FliptClientTest extends TestCase
{

    protected array $history;
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
    }


    public function testVariant(): void
    {
        $result = $this->apiClient->variant('flag1', ['fizz' => 'buzz'], 'entity');


        $this->assertTrue($result->getMatch());
        $this->assertEquals($result->getReason(), ResponseReasons::MATCH_EVALUATION_REASON);
        $this->assertEquals('flag1', $result->getFlagKey());
        $this->assertEquals($result->getSegmentKeys(), ['segment1']);
        $this->assertEquals($result->getVariantKey(), 'variant1');
    }
}
