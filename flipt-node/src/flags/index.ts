import { AuthenticationStrategy } from "..";
import { Flag, ListFlagsRequest, ListFlagsResponse } from "./models";

export class Flags {
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

  public async listFlags(
    namespaceKey: string,
    request?: ListFlagsRequest
  ): Promise<ListFlagsResponse> {
    const namespace = namespaceKey ?? "default";
    const url = new URL(`${this.url}/api/v1/namespaces/${namespace}/flags`);

    if (request?.reference) {
      url.searchParams.append("reference", request.reference);
    }
    if (request?.limit !== undefined) {
      url.searchParams.append("limit", request.limit.toString());
    }
    if (request?.offset !== undefined) {
      url.searchParams.append("offset", request.offset.toString());
    }
    if (request?.pageToken) {
      url.searchParams.append("pageToken", request.pageToken);
    }

    const args: RequestInit = {
      method: "GET",
      headers: {
        ...this.headers
      }
    };

    if (this.timeout !== undefined && this.timeout > 0) {
      args.signal = AbortSignal.timeout(this.timeout * 1000);
    }

    const response = await fetch(url.toString(), args);

    if (response.status !== 200) {
      try {
        const body = await response.json();
        throw new Error(body["message"] || "internal error");
      } catch (e) {
        if (e instanceof Error && e.message !== "internal error") {
          throw e;
        }
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
    }

    try {
      const data: ListFlagsResponse =
        (await response.json()) as ListFlagsResponse;
      return data;
    } catch (e) {
      throw new Error(
        `Failed to parse response: ${e instanceof Error ? e.message : "unknown error"}`
      );
    }
  }
}
