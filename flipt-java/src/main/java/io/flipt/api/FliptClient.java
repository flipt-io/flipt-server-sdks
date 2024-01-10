package io.flipt.api;

import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.evaluation.Evaluation;
import java.time.Duration;
import okhttp3.OkHttpClient;

public class FliptClient {
  private final Evaluation evaluation;

  private FliptClient(String url, int timeout, AuthenticationStrategy authenticationStrategy) {
    OkHttpClient httpClient = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(timeout)).build();
    this.evaluation = new Evaluation(httpClient, url, authenticationStrategy);
  }

  public Evaluation evaluation() {
    return evaluation;
  }

  public static FliptClientBuilder builder() {
    return new FliptClientBuilder();
  }

  public static final class FliptClientBuilder {
    private String baseURL = "http://localhost:8080";

    private AuthenticationStrategy authenticationStrategy;

    private int timeout = 60;

    public FliptClientBuilder() {
    }

    public FliptClientBuilder url(String url) {
      this.baseURL = url;
      return this;
    }

    public FliptClientBuilder authentication(AuthenticationStrategy authenticationStrategy) {
      this.authenticationStrategy = authenticationStrategy;
      return this;
    }

    public FliptClientBuilder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    public FliptClient build() {
      return new FliptClient(baseURL, timeout, authenticationStrategy);
    }
  }
}
