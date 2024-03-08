import pytest

from flipt.exceptions import FliptApiError


class TestListFlags:
    def test_success(self, sync_flipt_client):
        list_response = sync_flipt_client.flag.list_flags(namespace_key="default")
        assert len(list_response.flags) == 2

    @pytest.mark.usefixtures("_mock_list_flags_response_error")
    def test_list_error(self, sync_flipt_client):
        with pytest.raises(FliptApiError):
            sync_flipt_client.flag.list_flags(namespace_key="default")
