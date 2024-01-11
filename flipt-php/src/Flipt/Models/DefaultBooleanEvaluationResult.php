<?php

declare(strict_types=1);

namespace Flipt\Models;

use Flipt\Models\BooleanEvaluationResult;

final class DefaultBooleanEvaluationResult implements BooleanEvaluationResult
{
    public string $flagKey;
    public bool $enabled;
    public string $reason;
    public float $requestDurationMillis;
    public string $requestId;
    public string $timestamp;

    public function __construct(
        string $flagKey,
        bool $enabled,
        string $reason,
        float $requestDurationMillis,
        string $requestId,
        string $timestamp
    ) {
        $this->flagKey = $flagKey;
        $this->enabled = $enabled;
        $this->reason = $reason;
        $this->requestDurationMillis = $requestDurationMillis;
        $this->requestId = $requestId;
        $this->timestamp = $timestamp;
    }

    public function getFlagKey(): string
    {
        return $this->flagKey;
    }

    public function getEnabled(): bool
    {
        return $this->enabled;
    }

    public function getReason(): string
    {
        return $this->reason;
    }

    public function getRequestDurationMillis(): float
    {
        return $this->requestDurationMillis;
    }

    public function getRequestId(): string
    {
        return $this->requestId;
    }

    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}
