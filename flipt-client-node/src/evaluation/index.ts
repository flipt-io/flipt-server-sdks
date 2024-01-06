import {
  BatchEvaluationRequest,
  BatchEvaluationResponse,
  BooleanEvaluationResponse,
  EvaluationRequest,
  VariantEvaluationResponse
} from "./models";

export class Evaluation {
  private url: string;
  private token: string;
  private timeout: number;

  public constructor(url: string, token: string, timeout: number) {
    this.url = url;
    this.token = token;
    this.timeout = timeout;
  }

  public async variant(
    request: EvaluationRequest
  ): Promise<VariantEvaluationResponse> {
    const headers = {};
    if (this.token !== "") {
      headers["Authorization"] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${this.url}/evaluate/v1/variant`, {
      method: "POST",
      headers,
      body: JSON.stringify(request),
      signal: AbortSignal.timeout(this.timeout * 1000)
    });

    if (response.status !== 200) {
      const body = await response.json();
      throw new Error(body["message"] || "internal error");
    }

    const data: VariantEvaluationResponse =
      (await response.json()) as VariantEvaluationResponse;

    return data;
  }

  public async boolean(
    request: EvaluationRequest
  ): Promise<BooleanEvaluationResponse> {
    const headers = {};
    if (this.token !== "") {
      headers["Authorization"] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${this.url}/evaluate/v1/boolean`, {
      method: "POST",
      headers,
      body: JSON.stringify(request),
      signal: AbortSignal.timeout(this.timeout * 1000)
    });

    if (response.status !== 200) {
      const body = await response.json();
      throw new Error(body["message"] || "internal error");
    }

    const data: BooleanEvaluationResponse =
      (await response.json()) as BooleanEvaluationResponse;

    return data;
  }

  public async batch(
    request: BatchEvaluationRequest
  ): Promise<BatchEvaluationResponse> {
    const headers = {};
    if (this.token !== "") {
      headers["Authorization"] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${this.url}/evaluate/v1/batch`, {
      method: "POST",
      headers,
      body: JSON.stringify(request),
      signal: AbortSignal.timeout(this.timeout * 1000)
    });

    if (response.status !== 200) {
      const body = await response.json();
      throw new Error(body["message"] || "internal error");
    }

    const data: BatchEvaluationResponse =
      (await response.json()) as BatchEvaluationResponse;

    return data;
  }
}
