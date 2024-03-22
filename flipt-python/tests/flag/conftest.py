from http import HTTPStatus

import pytest


@pytest.fixture(params=[{}, {"message": "some error"}])
def _mock_list_flags_response_error(httpx_mock, flipt_url, request):
    httpx_mock.add_response(
        method="GET",
        url=f"{flipt_url}/api/v1/namespaces/default/flags",
        status_code=HTTPStatus.INTERNAL_SERVER_ERROR,
        json=request.param,
    )
