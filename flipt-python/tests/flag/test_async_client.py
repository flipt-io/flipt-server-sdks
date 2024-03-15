import pytest

from flipt.async_client import AsyncFliptClient
from flipt.exceptions import FliptApiError


class TestListFlags:
    async def test_success(self, async_flipt_client: AsyncFliptClient):
        list_response = await async_flipt_client.flag.list_flags(namespace_key="default")
        assert len(list_response.flags) == 7

    @pytest.mark.usefixtures("_mock_list_flags_response_error")
    async def test_list_error(self, async_flipt_client):
        with pytest.raises(FliptApiError):
            await async_flipt_client.flag.list_flags(namespace_key="default")
