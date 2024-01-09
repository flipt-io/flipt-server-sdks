from flipt import FliptApi
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest

fliptClient = FliptApi()

v = fliptClient.evaluation.variant(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flagll",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)
b = fliptClient.evaluation.boolean(
    EvaluationRequest(
        namespace_key="default",
        flag_key="flag_boolean",
        entity_id="entity",
        context={"fizz": "buzz"},
    )
)
ba = fliptClient.evaluation.batch(
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
        ]
    )
)

print(v)
print(b)
print(ba)
