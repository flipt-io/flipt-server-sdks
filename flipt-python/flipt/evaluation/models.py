import enum
from pydantic import BaseModel, Field
from typing import List, Optional


class EvaluationRequest(BaseModel):
    namespace_key: str = Field(default="default")
    flag_key: str
    entity_id: str
    context: dict
    reference: Optional[str] = None


class BatchEvaluationRequest(BaseModel):
    request_id: Optional[str] = None
    requests: List[EvaluationRequest]
    reference: Optional[str] = None


class VariantEvaluationResponse(BaseModel):
    match: bool
    segment_keys: List[str] = Field(..., alias="segmentKeys")
    reason: str
    flag_key: str = Field(..., alias="flagKey")
    variant_key: str = Field(..., alias="variantKey")
    variant_attachment: str = Field(..., alias="variantAttachment")
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
    timestamp: str


class BooleanEvaluationResponse(BaseModel):
    enabled: bool
    flag_key: str = Field(..., alias="flagKey")
    reason: str
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
    timestamp: str


class ErrorEvaluationResponse(BaseModel):
    flag_key: str = Field(..., alias="flagKey")
    namespace_key: str = Field(..., alias="namespaceKey")
    reason: str


class EvaluationResponse(BaseModel):
    type: str
    boolean_response: Optional[BooleanEvaluationResponse] = Field(
        default=None, alias="booleanResponse"
    )
    variant_response: Optional[VariantEvaluationResponse] = Field(
        default=None, alias="variantResponse"
    )
    error_response: Optional[ErrorEvaluationResponse] = Field(
        default=None, alias="errorResponse"
    )


class BatchEvaluationResponse(BaseModel):
    request_id: str = Field(..., alias="requestId")
    responses: List[EvaluationResponse]
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
