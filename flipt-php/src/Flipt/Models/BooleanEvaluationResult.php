<?php

declare(strict_types=1);

namespace Flipt\Models;

interface BooleanEvaluationResult
{
    public function getEnabled(): bool;
    public function getReason(): string;
    public function getRequestDurationMillis(): float;
    public function getRequestId(): string;
    public function getTimestamp(): string;
}
