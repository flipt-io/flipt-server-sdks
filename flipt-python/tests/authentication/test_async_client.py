import pytest

from flipt.async_client import AsyncFliptClient

async def test_variant(async_k8s_flipt_client):
    variant = await async_flipt_client.evaluation.variant(
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
