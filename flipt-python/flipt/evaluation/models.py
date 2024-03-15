from enum import StrEnum

from pydantic import Field

from flipt.models import CamelAliasModel


class EvaluationResponseType(StrEnum):
    VARIANT_EVALUATION_RESPONSE_TYPE = "VARIANT_EVALUATION_RESPONSE_TYPE"
    BOOLEAN_EVALUATION_RESPONSE_TYPE = "BOOLEAN_EVALUATION_RESPONSE_TYPE"
    ERROR_EVALUATION_RESPONSE_TYPE = "ERROR_EVALUATION_RESPONSE_TYPE"


class EvaluationReason(StrEnum):
    UNKNOWN_EVALUATION_REASON = "UNKNOWN_EVALUATION_REASON"
    FLAG_DISABLED_EVALUATION_REASON = "FLAG_DISABLED_EVALUATION_REASON"
    MATCH_EVALUATION_REASON = "MATCH_EVALUATION_REASON"
    DEFAULT_EVALUATION_REASON = "DEFAULT_EVALUATION_REASON"


class ErrorEvaluationReason(StrEnum):
    UNKNOWN_ERROR_EVALUATION_REASON = "UNKNOWN_ERROR_EVALUATION_REASON"
    NOT_FOUND_ERROR_EVALUATION_REASON = "NOT_FOUND_ERROR_EVALUATION_REASON"


class EvaluationRequest(CamelAliasModel):
    namespace_key: str = Field(default="default")
    flag_key: str
    entity_id: str
    context: dict
    reference: str | None = None


class BatchEvaluationRequest(CamelAliasModel):
    request_id: str | None = None
    requests: list[EvaluationRequest]
    reference: str | None = None


class VariantEvaluationResponse(CamelAliasModel):
    match: bool
    segment_keys: list[str]
    reason: EvaluationReason
    flag_key: str
    variant_key: str
    variant_attachment: str
    request_duration_millis: float
    timestamp: str


class BooleanEvaluationResponse(CamelAliasModel):
    enabled: bool
    flag_key: str
    reason: EvaluationReason
    request_duration_millis: float
    timestamp: str


class ErrorEvaluationResponse(CamelAliasModel):
    flag_key: str
    namespace_key: str
    reason: ErrorEvaluationReason


class EvaluationResponse(CamelAliasModel):
    type: EvaluationResponseType
    boolean_response: BooleanEvaluationResponse | None = None
    variant_response: VariantEvaluationResponse | None = None
    error_response: ErrorEvaluationResponse | None = None


class BatchEvaluationResponse(CamelAliasModel):
    request_id: str
    responses: list[EvaluationResponse]
    request_duration_millis: float
