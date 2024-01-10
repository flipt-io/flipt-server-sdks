import httpx
import typing
import json
from .models import (
    BatchEvaluationRequest,
    BatchEvaluationResponse,
    BooleanEvaluationResponse,
    EvaluationRequest,
    VariantEvaluationResponse,
)


class Evaluation:
    def __init__(
        self,
        url: str,
        client_token: typing.Optional[str],
        jwt_token: typing.Optional[str],
        timeout: int,
    ):
        self.url = url
        self.headers = {}
        if client_token != None:
            self.headers["Authorization"] = f"Bearer {client_token}"
        if jwt_token != None:
            self.headers["Authorization"] = f"JWT {jwt_token}"
        self.timeout = timeout

    def variant(self, request: EvaluationRequest) -> VariantEvaluationResponse:
        response = httpx.post(
            f"{self.url}/evaluate/v1/variant",
            headers=self.headers,
            json=request.model_dump(),
            timeout=self.timeout,
        )

        if response.status_code != 200:
            body = response.json()
            message = "internal error"

            if "message" in body:
                message = body["message"]

            raise Exception(message)

        variant_response = json.dumps(response.json()).encode("utf-8")
        return VariantEvaluationResponse.model_validate_json(variant_response)

    def boolean(self, request: EvaluationRequest) -> BooleanEvaluationResponse:
        response = httpx.post(
            f"{self.url}/evaluate/v1/boolean",
            headers=self.headers,
            json=request.model_dump(),
            timeout=self.timeout,
        )

        if response.status_code != 200:
            body = response.json()
            message = "internal error"

            if "message" in body:
                message = body["message"]

            raise Exception(message)

        boolean_response = json.dumps(response.json()).encode("utf-8")
        return BooleanEvaluationResponse.model_validate_json(boolean_response)

    def batch(self, request: BatchEvaluationRequest) -> BatchEvaluationResponse:
        response = httpx.post(
            f"{self.url}/evaluate/v1/batch",
            headers=self.headers,
            json=request.model_dump(),
            timeout=self.timeout,
        )

        if response.status_code != 200:
            body = response.json()
            message = "internal error"

            if "message" in body:
                message = body["message"]

            raise Exception(message)

        batch_response = json.dumps(response.json()).encode("utf-8")
        return BatchEvaluationResponse.model_validate_json(batch_response)
