package io.flipt.api;

import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.error.FliptException;
import io.flipt.api.evaluation.Evaluation;
import io.flipt.api.flags.Flag;
import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FliptClient {
  private final Evaluation evaluation;
  private final Flag flag;

  private FliptClient(FliptClientBuilder builder) {
    final OkHttpClient httpClient = new OkHttpClient.Builder().callTimeout(builder.timeout).build();
    this.evaluation =
        Evaluation.builder()
            .httpClient(httpClient)
            .baseURL(builder.baseURL)
            .authenticationStrategy(builder.authenticationStrategy)
            .headers(builder.headers)
            .setUnhandledExceptionProcessor(builder.unhandledExceptionProcessor)
            .build();
    this.flag =
        Flag.builder()
            .httpClient(httpClient)
            .baseURL(builder.baseURL)
            .authenticationStrategy(builder.authenticationStrategy)
            .headers(builder.headers)
            .setUnhandledExceptionProcessor(builder.unhandledExceptionProcessor)
            .build();
  }

  public Evaluation evaluation() {
    return evaluation;
  }

  public Flag flag() {
    return flag;
  }

  public static FliptClientBuilder builder() {
    return new FliptClientBuilder();
  }

  public static final class FliptClientBuilder {

    private static final Consumer<FliptException> nullProcessor =
        new Consumer<FliptException>() {
          public void accept(FliptException e) {}
        };
    private String baseURL = "http://localhost:8080";
    private AuthenticationStrategy authenticationStrategy;
    private Map<String, String> headers = new HashMap<>();
    private Duration timeout = Duration.ofSeconds(60);
    private Consumer<FliptException> unhandledExceptionProcessor = nullProcessor;

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

    public FliptClientBuilder setUnhandledExceptionProcessor(
        Consumer<FliptException> processor) {
      this.unhandledExceptionProcessor = processor;
      return this;
    }

    public FliptClient build() {
      return new FliptClient(this);
    }
  }
}
