import { Evaluation } from "./evaluation";
import {
  BooleanEvaluationResponse,
  EvaluationRequest,
  VariantEvaluationResponse
} from "./evaluation/models";

interface FliptClientOptions {
  url?: string;
  authenticationStrategy?: AuthenticationStrategy;
  timeout?: number;
  headers?: Record<string, string>;
}

export interface AuthenticationStrategy {
  authenticate(): Map<string, string>;
}

export class ClientTokenAuthentication implements AuthenticationStrategy {
  private clientToken: string;

  public constructor(clientToken: string) {
    this.clientToken = clientToken;
  }

  public authenticate(): Map<string, string> {
    return new Map([["Authorization", `Bearer ${this.clientToken}`]]);
  }
}

export class JWTAuthentication implements AuthenticationStrategy {
  private jwtToken: string;

  public constructor(jwtToken: string) {
    this.jwtToken = jwtToken;
  }

  public authenticate(): Map<string, string> {
    return new Map([["Authorization", `JWT ${this.jwtToken}`]]);
  }
}

const defaultURL = "http://localhost:8080";

const defaultFliptClientOptions: FliptClientOptions = {
  url: defaultURL
};

export class FliptClient {
  public evaluation: Evaluation;

  public constructor(options?: FliptClientOptions) {
    const clientOptions = {
      ...defaultFliptClientOptions
    };

    if (options?.url !== undefined) {
      clientOptions.url = options.url;
    }

    if (options?.timeout !== undefined) {
      clientOptions.timeout = options.timeout;
    }

    if (options?.authenticationStrategy !== undefined) {
      clientOptions.authenticationStrategy = options.authenticationStrategy;
    }

    if (options?.headers !== undefined) {
      clientOptions.headers = options.headers;
    }

    this.evaluation = new Evaluation(
      clientOptions.url || defaultURL,
      clientOptions.timeout,
      clientOptions.authenticationStrategy,
      clientOptions.headers
    );
  }
}

export class FliptMetrics {
  evaluationClient: Evaluation;
  datadogRum: any;

  constructor(evaluationClient: Evaluation, datadogRum: any) {
    this.evaluationClient = evaluationClient;
    this.datadogRum = datadogRum;
  }

  public async boolean(
    request: EvaluationRequest
  ): Promise<BooleanEvaluationResponse> {
    const response = await this.evaluationClient.boolean(request);

    this.datadogRum.addFeatureFlagEvaluation(
      `${request.namespaceKey}/${request.flagKey}`,
      response.enabled
    );
    return response;
  }

  public async variant(
    request: EvaluationRequest
  ): Promise<VariantEvaluationResponse> {
    const response = await this.evaluationClient.variant(request);

    this.datadogRum.addFeatureFlagEvaluation(
      `${request.namespaceKey}/${request.flagKey}`,
      response.variantKey
    );
    return response;
  }
}
