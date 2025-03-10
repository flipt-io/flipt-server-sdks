from http import HTTPStatus

import httpx

from flipt.authentication import AuthenticationStrategy
from flipt.exceptions import FliptApiError
from flipt.models import CommonParameters, ListParameters

from .models import Flag, ListFlagsResponse


class AsyncFlag:
    def __init__(
        self,
        url: str,
        headers: dict[str, str] | None = None,
        authentication: AuthenticationStrategy | None = None,
        httpx_client: httpx.AsyncClient | None = None,
    ):
        self.url = url

        if headers is None:
            headers = {}
        self.headers = headers

        self._client = httpx_client or httpx.AsyncClient()

        if authentication:
            authentication.authenticate(self.headers)

    async def close(self) -> None:
        await self._client.aclose()

    def _raise_on_error(self, response: httpx.Response) -> None:
        if response.status_code != 200:
            body = response.json()
            message = body.get("message", HTTPStatus(response.status_code).description)
            raise FliptApiError(message, response.status_code)

    async def list_flags(self, namespace_key: str, params: ListParameters | None = None) -> ListFlagsResponse:
        if namespace_key is None:
            namespace_key = "default"

        response = await self._client.get(
            f"{self.url}/api/v1/namespaces/{namespace_key}/flags",
            params=params.model_dump_json(exclude_none=True, by_alias=True) if params else {},
            headers=self.headers,
        )
        self._raise_on_error(response)
        return ListFlagsResponse.model_validate_json(response.text)

    async def get_flag(self, namespace_key: str, flag_key: str, params: CommonParameters | None = None) -> Flag:
        if namespace_key is None:
            namespace_key = "default"

        response = await self._client.get(
            f"{self.url}/api/v1/namespaces/{namespace_key}/flags/{flag_key}",
            params=params.model_dump_json(exclude_none=True, by_alias=True) if params else {},
            headers=self.headers,
        )
        self._raise_on_error(response)
        return Flag.model_validate_json(response.text)
