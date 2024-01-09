import { Evaluation } from "./evaluation";

interface FliptApiClientOptions {
  url?: string;
  clientToken?: string;
  jwtToken?: string;
  timeout?: number;
}

const defaultFliptClientOptions: FliptApiClientOptions = {
  url: "http://localhost:8080",
  clientToken: "",
  jwtToken: "",
  timeout: 60
};

export class FliptApiClient {
  public evaluation: Evaluation;

  public constructor(options?: FliptApiClientOptions) {
    const clientOptions = {
      ...defaultFliptClientOptions
    };

    if (options?.clientToken !== undefined && options?.jwtToken != undefined) {
      throw new Error("can not define both client token and jwt token");
    }

    if (options?.url !== undefined) {
      clientOptions.url = options.url;
    }
    if (options?.clientToken !== undefined) {
      clientOptions.clientToken = options.clientToken;
    }
    if (options?.jwtToken !== undefined) {
      clientOptions.jwtToken = options.jwtToken;
    }
    if (options?.timeout !== undefined) {
      clientOptions.timeout = options.timeout;
    }

    this.evaluation = new Evaluation(
      clientOptions.url,
      clientOptions.clientToken,
      clientOptions.jwtToken,
      clientOptions.timeout
    );
  }
}
