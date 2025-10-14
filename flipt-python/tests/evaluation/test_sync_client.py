import pytest

from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest
from flipt.exceptions import FliptApiError


def test_variant(sync_flipt_client):
    variant = sync_flipt_client.evaluation.variant(
        EvaluationRequest(
            namespace_key="default",
            flag_key="flag1",
            entity_id="entity",
            context={"fizz": "buzz"},
        ),
    )

    assert variant.match
    assert variant.flag_key == "flag1"
    assert variant.variant_key == "variant1"
    assert variant.reason == "MATCH_EVALUATION_REASON"
    assert "segment1" in variant.segment_keys


@pytest.mark.usefixtures("_mock_variant_response_error")
def test_evaluate_variant_error(sync_flipt_client):
    with pytest.raises(FliptApiError):
        sync_flipt_client.evaluation.variant(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag1",
                entity_id="entity",
                context={"fizz": "buzz"},
            ),
        )


def test_boolean(sync_flipt_client):
    boolean = sync_flipt_client.evaluation.boolean(
        EvaluationRequest(
            namespace_key="default",
            flag_key="flag_boolean",
            entity_id="entity",
            context={"fizz": "buzz"},
        ),
    )

    assert boolean.enabled
    assert boolean.flag_key == "flag_boolean"
    assert boolean.reason == "MATCH_EVALUATION_REASON"
    assert "segment1" in boolean.segment_keys


@pytest.mark.usefixtures("_mock_boolean_response_error")
def test_evaluate_boolean_error(sync_flipt_client):
    with pytest.raises(FliptApiError):
        sync_flipt_client.evaluation.boolean(
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag_boolean",
                entity_id="entity",
                context={"fizz": "buzz"},
            ),
        )


def test_batch(sync_flipt_client):
    batch = sync_flipt_client.evaluation.batch(
        BatchEvaluationRequest(
            requests=[
                EvaluationRequest(
                    namespace_key="default",
                    flag_key="flag1",
                    entity_id="entity",
                    context={"fizz": "buzz"},
                ),
                EvaluationRequest(
                    namespace_key="default",
                    flag_key="flag_boolean",
                    entity_id="entity",
                    context={"fizz": "buzz"},
                ),
                EvaluationRequest(
                    namespace_key="default",
                    flag_key="notfound",
                    entity_id="entity",
                    context={"fizz": "buzz"},
                ),
            ],
        ),
    )

    assert len(batch.responses) == 3

    # Variant
    assert batch.responses[0].type == "VARIANT_EVALUATION_RESPONSE_TYPE"

    variant = batch.responses[0].variant_response
    assert variant.match
    assert variant.flag_key == "flag1"
    assert variant.variant_key == "variant1"
    assert variant.reason == "MATCH_EVALUATION_REASON"
    assert "segment1" in variant.segment_keys

    # Boolean
    assert batch.responses[1].type == "BOOLEAN_EVALUATION_RESPONSE_TYPE"

    boolean = batch.responses[1].boolean_response
    assert boolean.enabled
    assert boolean.flag_key == "flag_boolean"
    assert boolean.reason == "MATCH_EVALUATION_REASON"
    assert "segment1" in boolean.segment_keys

    # Error
    assert batch.responses[2].type == "ERROR_EVALUATION_RESPONSE_TYPE"

    error = batch.responses[2].error_response
    assert error.flag_key == "notfound"
    assert error.namespace_key == "default"
    assert error.reason == "NOT_FOUND_ERROR_EVALUATION_REASON"


@pytest.mark.usefixtures("_mock_batch_response_error")
def test_evaluate_batch_error(sync_flipt_client):
    with pytest.raises(FliptApiError):
        sync_flipt_client.evaluation.batch(
            BatchEvaluationRequest(
                requests=[
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="flag1",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="flag_boolean",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                    EvaluationRequest(
                        namespace_key="default",
                        flag_key="notfound",
                        entity_id="entity",
                        context={"fizz": "buzz"},
                    ),
                ],
            ),
        )
