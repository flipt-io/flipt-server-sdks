from flipt import FliptClient
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest

flipt_client = FliptClient()

variant_flag = flipt_client.evaluation.variant(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flag_variant",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)
boolean_flag = flipt_client.evaluation.boolean(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flag_boolean",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)
batch = flipt_client.evaluation.batch(
    BatchEvaluationRequest(
        requests=[
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag_variant",
                entity_id="entity",
                context={"fizz": "buzz"},
            ),
            EvaluationRequest(
                namespace_key="default",
                flag_key="flag_boolean",
                entity_id="entity",
                context={"fizz": "buzz"},
            ),
        ]
    )
)

print(variant_flag)
print(boolean_flag)
print(batch)
