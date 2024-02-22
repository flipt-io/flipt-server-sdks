import asyncio

from flipt import AsyncFliptClient
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest


async def main():
    flipt_client = AsyncFliptClient()

    variant_flag = await flipt_client.evaluation.variant(
        EvaluationRequest(
            namespace_key="default",
            flag_key="flag1",
            entity_id="entity",
            context={"fizz": "buzz"},
        )
    )
    boolean_flag = await flipt_client.evaluation.boolean(
        EvaluationRequest(
            namespace_key="default",
            flag_key="flag_boolean",
            entity_id="entity",
            context={"fizz": "buzz"},
        )
    )
    batch = await flipt_client.evaluation.batch(
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

    print(variant_flag)
    print(boolean_flag)
    print(batch)


loop = asyncio.new_event_loop()
loop.run_until_complete(main())
