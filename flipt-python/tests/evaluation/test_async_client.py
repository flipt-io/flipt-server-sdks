import pytest

from flipt.evaluation import (
    BooleanEvaluationResponse,
    EvaluationRequest,
    VariantEvaluationResponse,
)
from flipt.exceptions import FliptApiError


async def test_variant(async_flipt_client, corpus):
    variant_tests = corpus["VARIANT"]
    for test_scenario in variant_tests:
        if ("request" not in test_scenario) or ("expectation" not in test_scenario):
            raise Exception("malformed test")
        eval_request = EvaluationRequest.model_validate(test_scenario["request"])
        variant = await async_flipt_client.evaluation.variant(eval_request)
        expectation = VariantEvaluationResponse.model_validate(test_scenario["expectation"])
        assert expectation.flag_key == variant.flag_key
        assert expectation.match == variant.match
        assert expectation.variant_key == variant.variant_key
        assert expectation.reason == variant.reason

        assert len(expectation.segment_keys) == len(variant.segment_keys)


@pytest.mark.usefixtures('_mock_variant_response_error')
async def test_evaluate_variant_error(async_flipt_client):
    with pytest.raises(FliptApiError):
        await async_flipt_client.evaluation.variant(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag1",
                entity_id="entity",
                context={"fizz": "buzz"},
            )
        )


async def test_boolean(async_flipt_client, corpus):
    boolean_tests = corpus["BOOLEAN"]
    for test_scenario in boolean_tests:
        if ("request" not in test_scenario) or ("expectation" not in test_scenario):
            raise Exception("malformed test")
        eval_request = EvaluationRequest.model_validate(test_scenario["request"])
        boolean = await async_flipt_client.evaluation.boolean(eval_request)
        expectation = BooleanEvaluationResponse.model_validate(test_scenario["expectation"])
        assert expectation.flag_key == boolean.flag_key
        assert expectation.enabled == boolean.enabled
        assert expectation.reason == boolean.reason


@pytest.mark.usefixtures('_mock_boolean_response_error')
async def test_evaluate_boolean_error(async_flipt_client):
    with pytest.raises(FliptApiError):
        await async_flipt_client.evaluation.boolean(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag_boolean",
                entity_id="entity",
                context={"fizz": "buzz"},
            )
        )
