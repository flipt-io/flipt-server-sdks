import httpx2
import pytest

from flipt.flags import AsyncFlag, SyncFlag


@pytest.fixture
def failing_flag(mock_flipt_url, error_transport):
    client = SyncFlag(mock_flipt_url, httpx_client=httpx2.Client(transport=error_transport))
    yield client
    client.close()


@pytest.fixture
async def failing_async_flag(mock_flipt_url, error_transport):
    client = AsyncFlag(mock_flipt_url, httpx_client=httpx2.AsyncClient(transport=error_transport))
    yield client
    await client.close()
