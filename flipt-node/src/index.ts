import { Evaluation } from "./evaluation";

interface FliptClientOptions {
  url?: string;
  authenticationStrategy?: AuthenticationStrategy;
  timeout?: number;
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

const defaultFliptClientOptions: FliptClientOptions = {
  url: "http://localhost:8080",
  timeout: 60
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

    this.evaluation = new Evaluation(
      clientOptions.url,
      clientOptions.timeout,
      clientOptions.authenticationStrategy
    );
  }
}
