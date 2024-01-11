import enum
from pydantic import BaseModel, Field
from typing import List, Optional


class EvaluationResponseType(str, enum.Enum):
    VARIANT_EVALUATION_RESPONSE_TYPE = "VARIANT_EVALUATION_RESPONSE_TYPE"
    BOOLEAN_EVALUATION_RESPONSE_TYPE = "BOOLEAN_EVALUATION_RESPONSE_TYPE"
    ERROR_EVALUATION_RESPONSE_TYPE = "ERROR_EVALUATION_RESPONSE_TYPE"


class EvaluationReason(str, enum.Enum):
    UNKNOWN_EVALUATION_REASON = "UNKNOWN_EVALUATION_REASON"
    FLAG_DISABLED_EVALUATION_REASON = "FLAG_DISABLED_EVALUATION_REASON"
    MATCH_EVALUATION_REASON = "MATCH_EVALUATION_REASON"
    DEFAULT_EVALUATION_REASON = "DEFAULT_EVALUATION_REASON"


class ErrorEvaluationReason(str, enum.Enum):
    UNKNOWN_ERROR_EVALUATION_REASON = "UNKNOWN_ERROR_EVALUATION_REASON"
    NOT_FOUND_ERROR_EVALUATION_REASON = "NOT_FOUND_ERROR_EVALUATION_REASON"


class EvaluationRequest(BaseModel):
    namespace_key: str = Field(default="default")
    flag_key: str
    entity_id: str
    context: dict


class BatchEvaluationRequest(BaseModel):
    request_id: Optional[str] = None
    requests: List[EvaluationRequest]


class VariantEvaluationResponse(BaseModel):
    match: bool
    segment_keys: List[str] = Field(..., alias="segmentKeys")
    reason: EvaluationReason
    flag_key: str = Field(..., alias="flagKey")
    variant_key: str = Field(..., alias="variantKey")
    variant_attachment: str = Field(..., alias="variantAttachment")
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
    timestamp: str


class BooleanEvaluationResponse(BaseModel):
    enabled: bool
    flag_key: str = Field(..., alias="flagKey")
    reason: EvaluationReason
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
    timestamp: str


class ErrorEvaluationReponse(BaseModel):
    flag_key: str = Field(..., alias="flagKey")
    namespace_key: str = Field(..., alias="namespaceKey")
    reason: ErrorEvaluationReason


class EvaluationResponse(BaseModel):
    type: EvaluationResponseType
    boolean_response: Optional[BooleanEvaluationResponse] = Field(
        default=None, alias="booleanResponse"
    )
    variant_response: Optional[VariantEvaluationResponse] = Field(
        default=None, alias="variantResponse"
    )
    error_response: Optional[ErrorEvaluationReponse] = Field(
        default=None, alias="errorResponse"
    )


class BatchEvaluationResponse(BaseModel):
    request_id: str = Field(..., alias="requestId")
    responses: List[EvaluationResponse]
    request_duration_millis: float = Field(..., alias="requestDurationMillis")
