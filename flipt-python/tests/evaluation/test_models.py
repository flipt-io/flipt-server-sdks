from flipt.evaluation.models import (
    BooleanEvaluationResponse,
    EvaluationReason,
    VariantEvaluationResponse,
)


def test_variant_evaluation_response_default_segment_keys():
    response = VariantEvaluationResponse(
        match=True,
        reason=EvaluationReason.MATCH_EVALUATION_REASON,
        flag_key="flag1",
        variant_key="variant1",
        variant_attachment="{}",
        request_duration_millis=0.001,
        timestamp="2024-01-01T00:00:00Z",
    )
    assert response.segment_keys == []


def test_boolean_evaluation_response_default_segment_keys():
    response = BooleanEvaluationResponse(
        enabled=True,
        flag_key="flag_boolean",
        reason=EvaluationReason.MATCH_EVALUATION_REASON,
        request_duration_millis=0.001,
        timestamp="2024-01-01T00:00:00Z",
    )
    assert response.segment_keys == []


def test_variant_evaluation_response_segment_keys_explicit():
    response = VariantEvaluationResponse(
        match=True,
        segment_keys=["segment1", "segment2"],
        reason=EvaluationReason.MATCH_EVALUATION_REASON,
        flag_key="flag1",
        variant_key="variant1",
        variant_attachment="{}",
        request_duration_millis=0.001,
        timestamp="2024-01-01T00:00:00Z",
    )
    assert response.segment_keys == ["segment1", "segment2"]


def test_boolean_evaluation_response_segment_keys_explicit():
    response = BooleanEvaluationResponse(
        enabled=True,
        segment_keys=["segment1"],
        flag_key="flag_boolean",
        reason=EvaluationReason.MATCH_EVALUATION_REASON,
        request_duration_millis=0.001,
        timestamp="2024-01-01T00:00:00Z",
    )
    assert response.segment_keys == ["segment1"]


def test_variant_evaluation_response_from_dict_without_segment_keys():
    data = {
        "match": True,
        "reason": "MATCH_EVALUATION_REASON",
        "flagKey": "flag1",
        "variantKey": "variant1",
        "variantAttachment": "{}",
        "requestDurationMillis": 0.001,
        "timestamp": "2024-01-01T00:00:00Z",
    }
    response = VariantEvaluationResponse.model_validate(data)
    assert response.segment_keys == []


def test_boolean_evaluation_response_from_dict_without_segment_keys():
    data = {
        "enabled": True,
        "flagKey": "flag_boolean",
        "reason": "MATCH_EVALUATION_REASON",
        "requestDurationMillis": 0.001,
        "timestamp": "2024-01-01T00:00:00Z",
    }
    response = BooleanEvaluationResponse.model_validate(data)
    assert response.segment_keys == []
