import axios from 'axios';
import {
  BatchEvaluationRequest,
  BatchEvaluationResponse,
  BooleanEvaluationResponse,
  EvaluationRequest,
  VariantEvaluationResponse
} from './models';

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
    if (this.token !== '') {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const { data } = await axios.post<VariantEvaluationResponse>(
      `${this.url}/evaluate/v1/variant`,
      request,
      {
        headers,
        timeout: this.timeout
      }
    );

    return data;
  }

  public async boolean(
    request: EvaluationRequest
  ): Promise<BooleanEvaluationResponse> {
    const headers = {};
    if (this.token !== '') {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const { data } = await axios.post<BooleanEvaluationResponse>(
      `${this.url}/evaluate/v1/boolean`,
      request,
      {
        headers,
        timeout: this.timeout
      }
    );

    return data;
  }

  public async batch(
    request: BatchEvaluationRequest
  ): Promise<BatchEvaluationResponse> {
    const headers = {};
    if (this.token !== '') {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const { data } = await axios.post<BatchEvaluationResponse>(
      `${this.url}/evaluate/v1/batch`,
      request,
      {
        headers,
        timeout: this.timeout
      }
    );

    return data;
  }
}
