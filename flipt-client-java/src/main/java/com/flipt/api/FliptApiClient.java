package com.flipt.api;

import com.flipt.api.evaluation.Evaluation;
import java.time.Duration;
import okhttp3.OkHttpClient;

public class FliptApiClient {
  private Evaluation evaluation;

  public FliptApiClient(String url, String clientToken, String jwtToken, int timeout) {
    OkHttpClient httpClient =
        new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(timeout)).build();
    this.evaluation = new Evaluation(httpClient, url, clientToken, jwtToken);
  }

  public Evaluation evaluation() {
    return evaluation;
  }

  public static FliptApiClientBuilder builder() {
    return new FliptApiClientBuilder();
  }

  public static final class FliptApiClientBuilder {
    private String baseURL = "http://localhost:8080";

    private String clientToken = "";

    private String jwtToken = "";

    private int timeout = 60;

    public FliptApiClientBuilder() {}

    public FliptApiClientBuilder url(String url) {
      this.baseURL = url;
      return this;
    }

    public FliptApiClientBuilder clientToken(String token) {
      this.clientToken = token;
      return this;
    }

    public FliptApiClientBuilder jwtToken(String token) {
      this.jwtToken = token;
      return this;
    }

    public FliptApiClientBuilder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    public FliptApiClient build() {
      return new FliptApiClient(baseURL, clientToken, jwtToken, timeout);
    }
  }
}
