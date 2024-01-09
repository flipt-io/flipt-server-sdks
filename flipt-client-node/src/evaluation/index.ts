import {
  BatchEvaluationRequest,
  BatchEvaluationResponse,
  BooleanEvaluationResponse,
  EvaluationRequest,
  VariantEvaluationResponse
} from "./models";

export class Evaluation {
  private url: string;
  private headers: object;
  private timeout: number;

  public constructor(
    url: string,
    clientToken: string,
    jwtToken: string,
    timeout: number
  ) {
    this.url = url;
    this.headers = {};
    if (!!clientToken) {
      this.headers["Authorization"] = `Bearer ${clientToken}`;
    }
    if (!!jwtToken) {
      this.headers["Authorization"] = `JWT ${jwtToken}`;
    }
    this.timeout = timeout;
  }

  public async variant(
    request: EvaluationRequest
  ): Promise<VariantEvaluationResponse> {
    const response = await fetch(`${this.url}/evaluate/v1/variant`, {
      method: "POST",
      headers: {
        ...this.headers
      },
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
    const response = await fetch(`${this.url}/evaluate/v1/boolean`, {
      method: "POST",
      headers: {
        ...this.headers
      },
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
    const response = await fetch(`${this.url}/evaluate/v1/batch`, {
      method: "POST",
      headers: {
        ...this.headers
      },
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
