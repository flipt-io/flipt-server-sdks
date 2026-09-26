import os
from http import HTTPStatus

import httpx2
import pytest

from flipt import AsyncFliptClient, FliptClient
from flipt.authentication import ClientTokenAuthentication


@pytest.fixture(scope="session")
def flipt_url() -> str:
    flipt_url = os.environ.get("FLIPT_URL")
    if flipt_url is None:
        raise Exception("FLIPT_URL not set")
    return flipt_url


@pytest.fixture(scope="session")
def flipt_auth_token() -> str:
    auth_token = os.environ.get("FLIPT_AUTH_TOKEN")
    if auth_token is None:
        raise Exception("FLIPT_AUTH_TOKEN not set")

    return auth_token


@pytest.fixture(scope="session")
def sync_flipt_client(flipt_url, flipt_auth_token):
    return FliptClient(url=flipt_url, authentication=ClientTokenAuthentication(flipt_auth_token))


@pytest.fixture
def async_flipt_client(flipt_url, flipt_auth_token):
    return AsyncFliptClient(url=flipt_url, authentication=ClientTokenAuthentication(flipt_auth_token))


@pytest.fixture
def mock_flipt_url() -> str:
    return "http://flipt.invalid"


@pytest.fixture(params=[{}, {"message": "some error"}])
def error_transport(request) -> httpx2.MockTransport:
    body = request.param

    def handler(_request: httpx2.Request) -> httpx2.Response:
        return httpx2.Response(HTTPStatus.INTERNAL_SERVER_ERROR, json=body)

    return httpx2.MockTransport(handler)
