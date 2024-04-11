import asyncio
import base64
import os

from flipt import AsyncFliptClient
from flipt.evaluation import BatchEvaluationRequest, EvaluationRequest


async def main():
    # Set up the headers with the basic auth credentials
    # for example if Flipt is behind a reverse proxy

    headers = {}
    username = os.getenv("FLIPT_USERNAME") or "admin"
    password = os.getenv("FLIPT_PASSWORD") or "admin"

    b64_creds = base64.b64encode(f"{username}:{password}".encode("utf-8")).decode("utf-8")
    headers["Authorization"] = f"Basic {b64_creds}')"

    flipt_client = AsyncFliptClient(headers=headers)

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
