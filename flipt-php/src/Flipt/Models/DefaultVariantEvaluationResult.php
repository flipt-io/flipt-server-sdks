<?php

declare(strict_types=1);

namespace Flipt\Models;

use Flipt\Models\VariantEvaluationResult;

final class DefaultVariantEvaluationResult implements VariantEvaluationResult
{
    public bool $match;
    public string $reason;
    public float $requestDurationMillis;
    public string $requestId;
    public string $timestamp;
    public ?array $segmentKeys;
    public ?string $variantKey;
    public ?string $variantAttachment;

    public function __construct(
        bool $match,
        string $reason,
        float $requestDurationMillis,
        string $requestId,
        string $timestamp,
        ?array $segmentKeys,
        ?string $variantKey,
        ?string $variantAttachment
    ) {
        $this->match = $match;
        $this->reason = $reason;
        $this->requestDurationMillis = $requestDurationMillis;
        $this->requestId = $requestId;
        $this->timestamp = $timestamp;
        $this->segmentKeys = $segmentKeys;
        $this->variantKey = $variantKey;
        $this->variantAttachment = $variantAttachment;
    }

    public function getMatch(): bool
    {
        return $this->match;
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

    public function getSegmentKeys(): ?array
    {
        return $this->segmentKeys;
    }

    public function getVariantKey(): ?string
    {
        return $this->variantKey;
    }

    public function getVariantAttachment(): ?string
    {
        return $this->variantAttachment;
    }
}
