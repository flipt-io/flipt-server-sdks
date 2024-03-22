from http import HTTPStatus

import pytest


@pytest.fixture(params=[{}, {"message": "some error"}])
def _mock_variant_response_error(httpx_mock, flipt_url, request):
    httpx_mock.add_response(
        method="POST",
        url=f"{flipt_url}/evaluate/v1/variant",
        status_code=HTTPStatus.INTERNAL_SERVER_ERROR,
        json=request.param,
    )


@pytest.fixture(params=[{}, {"message": "some error"}])
def _mock_boolean_response_error(httpx_mock, flipt_url, request):
    httpx_mock.add_response(
        method="POST",
        url=f"{flipt_url}/evaluate/v1/boolean",
        status_code=HTTPStatus.INTERNAL_SERVER_ERROR,
        json=request.param,
    )


@pytest.fixture(params=[{}, {"message": "some error"}])
def _mock_batch_response_error(httpx_mock, flipt_url, request):
    httpx_mock.add_response(
        method="POST",
        url=f"{flipt_url}/evaluate/v1/batch",
        status_code=HTTPStatus.INTERNAL_SERVER_ERROR,
        json=request.param,
    )
