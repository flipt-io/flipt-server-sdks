import pytest

from flipt.exceptions import FliptApiError


class TestListFlags:
    def test_list_flags_success(self, sync_flipt_client):
        list_response = sync_flipt_client.flag.list_flags(namespace_key="default")
        assert len(list_response.flags) == 2

    @pytest.mark.usefixtures("_mock_list_flags_response_error")
    def test_list_flags_error(self, sync_flipt_client):
        with pytest.raises(FliptApiError):
            sync_flipt_client.flag.list_flags(namespace_key="default")

    @pytest.mark.parametrize("flag_key", ["flag1", "flag_boolean"])
    def test_get_flag_success(self, sync_flipt_client, flag_key):
        flag = sync_flipt_client.flag.get_flag("default", flag_key)
        assert flag.key == flag_key

    def test_get_flag_error(self, sync_flipt_client):
        with pytest.raises(FliptApiError):
            sync_flipt_client.flag.get_flag("default", "notfound")
