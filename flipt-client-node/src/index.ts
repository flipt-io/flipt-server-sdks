import { Evaluation } from "./evaluation";

interface FliptApiClientOptions {
  url?: string;
  token?: string;
  timeout?: number;
}

const defaultFliptClientOptions: FliptApiClientOptions = {
  url: "http://localhost:8080",
  token: "",
  timeout: 60
};

export class FliptClient {
  public evaluation: Evaluation;

  public constructor(options?: FliptApiClientOptions) {
    const clientOptions = {
      ...defaultFliptClientOptions
    };

    if (options?.url !== undefined) {
      clientOptions.url = options.url;
    }
    if (options?.token !== undefined) {
      clientOptions.token = options.token;
    }
    if (options?.timeout !== undefined) {
      clientOptions.timeout = options.timeout;
    }

    this.evaluation = new Evaluation(
      clientOptions.url,
      clientOptions.token,
      clientOptions.timeout
    );
  }
}
