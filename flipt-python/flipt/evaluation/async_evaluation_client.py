from http import HTTPStatus

import httpx

from flipt.authentication import AuthenticationStrategy
from flipt.exceptions import FliptApiError

from .models import (
    BatchEvaluationRequest,
    BatchEvaluationResponse,
    BooleanEvaluationResponse,
    EvaluationRequest,
    VariantEvaluationResponse,
)


class AsyncEvaluation:
    def __init__(
        self,
        url: str,
        authentication: AuthenticationStrategy | None = None,
        httpx_client: httpx.AsyncClient | None = None,
    ):
        self.url = url
        self.headers: dict[str, str] = {}

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

    async def variant(self, request: EvaluationRequest) -> VariantEvaluationResponse:
        response = await self._client.post(
            f"{self.url}/evaluate/v1/variant",
            headers=self.headers,
            json=request.model_dump(),
        )

        self._raise_on_error(response)
        return VariantEvaluationResponse.model_validate_json(response.text)

    async def boolean(self, request: EvaluationRequest) -> BooleanEvaluationResponse:
        response = await self._client.post(
            f"{self.url}/evaluate/v1/boolean",
            headers=self.headers,
            json=request.model_dump(),
        )

        self._raise_on_error(response)
        return BooleanEvaluationResponse.model_validate_json(response.text)

    async def batch(self, request: BatchEvaluationRequest) -> BatchEvaluationResponse:
        response = await self._client.post(
            f"{self.url}/evaluate/v1/batch",
            headers=self.headers,
            json=request.model_dump(),
        )

        self._raise_on_error(response)
        return BatchEvaluationResponse.model_validate_json(response.text)
