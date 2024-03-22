import pytest

from flipt.async_client import AsyncFliptClient
from flipt.exceptions import FliptApiError


class TestListFlags:
    async def test_list_flags_success(self, async_flipt_client: AsyncFliptClient):
        list_response = await async_flipt_client.flag.list_flags(namespace_key="default")
        assert len(list_response.flags) == 2

    @pytest.mark.usefixtures("_mock_list_flags_response_error")
    async def test_list_flags_error(self, async_flipt_client):
        with pytest.raises(FliptApiError):
            await async_flipt_client.flag.list_flags(namespace_key="default")

    @pytest.mark.parametrize("flag_key", ["flag1", "flag_boolean"])
    async def test_get_flag_success(self, async_flipt_client, flag_key):
        flag = await async_flipt_client.flag.get_flag("default", flag_key)
        assert flag.key == flag_key

    async def test_get_flag_error(self, async_flipt_client):
        with pytest.raises(FliptApiError):
            await async_flipt_client.flag.get_flag("default", "notfound")
