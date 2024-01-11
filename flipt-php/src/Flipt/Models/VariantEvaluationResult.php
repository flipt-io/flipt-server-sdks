<?php

declare(strict_types=1);

namespace Flipt\Models;

interface VariantEvaluationResult
{
    public function getFlagKey(): string;
    public function getMatch(): bool;
    public function getReason(): string;
    public function getRequestDurationMillis(): float;
    public function getRequestId(): string;
    public function getTimestamp(): string;
    public function getSegmentKeys(): ?array;
    public function getVariantKey(): ?string;
    public function getVariantAttachment(): ?string;
}
