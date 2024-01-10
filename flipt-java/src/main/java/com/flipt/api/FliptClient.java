package com.flipt.api;

import com.flipt.api.evaluation.Evaluation;
import java.time.Duration;
import okhttp3.OkHttpClient;

public class FliptClient {
  private final Evaluation evaluation;

  private FliptClient(String url, String clientToken, String jwtToken, int timeout) {
    OkHttpClient httpClient =
        new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(timeout)).build();
    this.evaluation = new Evaluation(httpClient, url, clientToken, jwtToken);
  }

  public Evaluation evaluation() {
    return evaluation;
  }

  public static FliptClientBuilder builder() {
    return new FliptClientBuilder();
  }

  public static final class FliptClientBuilder {
    private String baseURL = "http://localhost:8080";

    private String clientToken = "";

    private String jwtToken = "";

    private int timeout = 60;

    public FliptClientBuilder() {}

    public FliptClientBuilder url(String url) {
      this.baseURL = url;
      return this;
    }

    public FliptClientBuilder clientToken(String token) {
      this.clientToken = token;
      return this;
    }

    public FliptClientBuilder jwtToken(String token) {
      this.jwtToken = token;
      return this;
    }

    public FliptClientBuilder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    public FliptClient build() {
      return new FliptClient(baseURL, clientToken, jwtToken, timeout);
    }
  }
}
