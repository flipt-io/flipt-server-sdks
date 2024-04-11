package io.flipt.api;

import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.evaluation.Evaluation;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;

public class FliptClient {
  private final Evaluation evaluation;

  private FliptClient(FliptClientBuilder builder) {
    final OkHttpClient httpClient = new OkHttpClient.Builder().callTimeout(builder.timeout).build();
    this.evaluation =
        Evaluation.builder()
            .httpClient(httpClient)
            .baseURL(builder.baseURL)
            .authenticationStrategy(builder.authenticationStrategy)
            .headers(builder.headers)
            .build();
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
    private Map<String, String> headers = new HashMap<>();
    private Duration timeout = Duration.ofSeconds(60);

    private FliptClientBuilder() {}

    public FliptClientBuilder url(String url) {
      this.baseURL = url;
      return this;
    }

    public FliptClientBuilder authentication(AuthenticationStrategy authenticationStrategy) {
      this.authenticationStrategy = authenticationStrategy;
      return this;
    }

    public FliptClientBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public FliptClientBuilder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public FliptClientBuilder timeout(int timeout) {
      this.timeout = Duration.ofSeconds(timeout);
      return this;
    }

    public FliptClient build() {
      return new FliptClient(this);
    }
  }
}
