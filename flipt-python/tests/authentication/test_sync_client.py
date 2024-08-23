import pytest

from flipt.evaluation import EvaluationRequest


def test_variant(sync_k8s_flipt_client):
    variant = sync_k8s_flipt_client.evaluation.variant(
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
