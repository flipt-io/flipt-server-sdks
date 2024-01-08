package com.flipt.api;

import com.flipt.api.evaluation.Evaluation;
import java.time.Duration;
import okhttp3.OkHttpClient;

public class FliptClient {
  public Evaluation evaluation;

  public FliptClient(String url, String token, int timeout) {
    OkHttpClient httpClient =
        new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(timeout)).build();
    this.evaluation = new Evaluation(httpClient, url, token);
  }

  public static FliptClientBuilder builder() {
    return new FliptClientBuilder();
  }

  public static final class FliptClientBuilder {
    private String baseURL = "http://localhost:8080";

    private String token = "";

    private int timeout = 60;

    public FliptClientBuilder() {}

    public FliptClientBuilder url(String url) {
      this.baseURL = url;
      return this;
    }

    public FliptClientBuilder token(String token) {
      this.token = token;
      return this;
    }

    public FliptClientBuilder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    public FliptClient build() {
      return new FliptClient(baseURL, token, timeout);
    }
  }
}
