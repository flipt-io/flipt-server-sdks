import requests
import json
from .models import (
    BatchEvaluationRequest,
    BatchEvaluationResponse,
    BooleanEvaluationResponse,
    EvaluationRequest,
    VariantEvaluationResponse,
)


class Evaluation:
    def __init__(self, url: str, token: str, timeout: int):
        self.url = url
        self.token = token
        self.timeout = timeout

    def variant(self, request: EvaluationRequest):
        headers = {}
        if self.token != "":
            headers["Authorization"] = f"Bearer {self.token}"

        response = requests.post(
            f"{self.url}/evaluate/v1/variant",
            headers=headers,
            json=request.dict(),
            timeout=self.timeout,
        )

        variant_response = json.dumps(response.json()).encode("utf-8")
        return VariantEvaluationResponse.model_validate_json(variant_response)

    def boolean(self, request: EvaluationRequest):
        headers = {}
        if self.token != "":
            headers["Authorization"] = f"Bearer {self.token}"

        response = requests.post(
            f"{self.url}/evaluate/v1/boolean",
            headers=headers,
            json=request.dict(),
            timeout=self.timeout,
        )

        boolean_response = json.dumps(response.json()).encode("utf-8")
        return BooleanEvaluationResponse.model_validate_json(boolean_response)

    def batch(self, request: BatchEvaluationRequest):
        headers = {}
        if self.token != "":
            headers["Authorization"] = f"Bearer {self.token}"

        response = requests.post(
            f"{self.url}/evaluate/v1/batch",
            headers=headers,
            json=request.dict(),
            timeout=self.timeout,
        )

        batch_response = json.dumps(response.json()).encode("utf-8")
        return BatchEvaluationResponse.model_validate_json(batch_response)
