import httpx2
import pytest

from flipt.evaluation import AsyncEvaluation, Evaluation


@pytest.fixture
def failing_evaluation(mock_flipt_url, error_transport):
    client = Evaluation(mock_flipt_url, httpx_client=httpx2.Client(transport=error_transport))
    yield client
    client.close()


@pytest.fixture
async def failing_async_evaluation(mock_flipt_url, error_transport):
    client = AsyncEvaluation(mock_flipt_url, httpx_client=httpx2.AsyncClient(transport=error_transport))
    yield client
    await client.close()
