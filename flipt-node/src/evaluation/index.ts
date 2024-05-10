import { AuthenticationStrategy } from "..";
import {
  BatchEvaluationRequest,
  BatchEvaluationResponse,
  BooleanEvaluationResponse,
  EvaluationRequest,
  VariantEvaluationResponse
} from "./models";

export class Evaluation {
  private url: string;
  private headers: Record<string, string>;
  private timeout?: number;

  public constructor(
    url: string,
    timeout?: number,
    authenticationStrategy?: AuthenticationStrategy,
    headers?: Record<string, string>
  ) {
    this.url = url;
    this.headers = headers || {};
    if (!!authenticationStrategy) {
      this.headers = {
        ...this.headers,
        ...Object.fromEntries(authenticationStrategy.authenticate())
      };
    }
    this.timeout = timeout;
  }

  public async variant(
    request: EvaluationRequest
  ): Promise<VariantEvaluationResponse> {
    const args: RequestInit = {
      method: "POST",
      headers: {
        ...this.headers
      },
      body: JSON.stringify(request)
    };

    if (this.timeout !== undefined && this.timeout > 0) {
      args.signal = AbortSignal.timeout(this.timeout * 1000);
    }

    const response = await fetch(`${this.url}/evaluate/v1/variant`, args);

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
    const args: RequestInit = {
      method: "POST",
      headers: {
        ...this.headers
      },
      body: JSON.stringify(request)
    };

    if (this.timeout !== undefined && this.timeout > 0) {
      args.signal = AbortSignal.timeout(this.timeout * 1000);
    }

    const response = await fetch(`${this.url}/evaluate/v1/boolean`, args);

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
    const args: RequestInit = {
      method: "POST",
      headers: {
        ...this.headers
      },
      body: JSON.stringify(request)
    };

    if (this.timeout !== undefined && this.timeout > 0) {
      args.signal = AbortSignal.timeout(this.timeout * 1000);
    }

    const response = await fetch(`${this.url}/evaluate/v1/batch`, args);

    if (response.status !== 200) {
      const body = await response.json();
      throw new Error(body["message"] || "internal error");
    }

    const data: BatchEvaluationResponse =
      (await response.json()) as BatchEvaluationResponse;

    return data;
  }
}
