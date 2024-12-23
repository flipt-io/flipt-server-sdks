package io.flipt.api.evaluation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.error.Error;
import io.flipt.api.error.FliptException;
import io.flipt.api.evaluation.models.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.*;

public class Evaluation {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final AuthenticationStrategy authenticationStrategy;
  private final Map<String, String> headers;
  private final ObjectMapper objectMapper;
  private final Consumer<FliptException> unhandledExceptionProcessor;

  private Evaluation(EvaluationBuilder builder) {
    this.httpClient = builder.httpClient;
    this.baseURL = builder.baseURL;
    this.authenticationStrategy = builder.authenticationStrategy;
    this.headers = builder.headers;
    this.objectMapper =
        JsonMapper.builder()
            .addModule(new Jdk8Module())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    this.unhandledExceptionProcessor = builder.unhandledExceptionProcessor;
  }

  public static EvaluationBuilder builder() {
    return new EvaluationBuilder();
  }

  public static class EvaluationBuilder {
    private OkHttpClient httpClient;
    private String baseURL;
    private AuthenticationStrategy authenticationStrategy;
    private Map<String, String> headers;
    private Consumer<FliptException> unhandledExceptionProcessor;

    private EvaluationBuilder() {}

    public EvaluationBuilder httpClient(OkHttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public EvaluationBuilder baseURL(String baseURL) {
      this.baseURL = baseURL;
      return this;
    }

    public EvaluationBuilder authenticationStrategy(AuthenticationStrategy authenticationStrategy) {
      this.authenticationStrategy = authenticationStrategy;
      return this;
    }

    public EvaluationBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public EvaluationBuilder setUnhandledExceptionProcessor(Consumer<FliptException> processor) {
      this.unhandledExceptionProcessor = processor;
      return this;
    }

    public Evaluation build() {
      return new Evaluation(this);
    }
  }

  /**
   * Evaluates a variant flag based on the {@link EvaluationRequest}.
   *
   * @param request the {@link EvaluationRequest}
   * @return a {@link VariantEvaluationResponse} containing the evaluation result
   * @throws EvaluationException if an error occurs during the process, such as a network issue or
   *     invalid request
   */
  public VariantEvaluationResponse evaluateVariant(EvaluationRequest request)
      throws EvaluationException {
    return this.makeRequest(request, "/evaluate/v1/variant", VariantEvaluationResponse.class);
  }

  /**
   * Evaluates a variant flag based on the {@link EvaluationRequest}. If the evaluation fails due to
   * an {@link EvaluationException}, the specified fallback string will be returned. In the event of
   * an exception, the exception is passed to an unhandled exception processor for further handling
   * or logging.
   *
   * @param request the {@link EvaluationRequest}
   * @param fallback the string value to return in case of an evaluation error
   * @return the variant key if the evaluation is successful; otherwise, returns the fallback value
   */
  public String variantValue(EvaluationRequest request, String fallback) {
    try {
      return this.evaluateVariant(request).getVariantKey();
    } catch (EvaluationException e) {
      this.unhandledExceptionProcessor.accept(e);
    }

    return fallback;
  }

  /**
   * Evaluates a boolean flag based on the {@link EvaluationRequest}.
   *
   * @param request the {@link EvaluationRequest}
   * @return a {@link BooleanEvaluationResponse} containing the evaluation result
   * @throws EvaluationException if an error occurs during the process, such as a network issue or
   *     invalid request
   */
  public BooleanEvaluationResponse evaluateBoolean(EvaluationRequest request)
      throws EvaluationException {
    return this.makeRequest(request, "/evaluate/v1/boolean", BooleanEvaluationResponse.class);
  }

  /**
   * Evaluates a boolean value based on the {@link EvaluationRequest}. If the evaluation fails due
   * to an {@link EvaluationException}, the specified fallback boolean value will be returned. In
   * the event of an exception, the exception is passed to an unhandled exception processor for
   * further handling or logging.
   *
   * @param request the {@link EvaluationRequest}
   * @param fallback the boolean value to return in case of an evaluation error
   * @return the value if the evaluation is successful; otherwise, returns the fallback value
   */
  public boolean booleanValue(EvaluationRequest request, boolean fallback) {
    try {
      return this.evaluateBoolean(request).isEnabled();
    } catch (EvaluationException e) {
      this.unhandledExceptionProcessor.accept(e);
    }
    return fallback;
  }

  /**
   * Evaluates a batch of flags based on the {@link BatchEvaluationRequest}.
   *
   * @param request the {@link BatchEvaluationRequest}
   * @return a {@link BatchEvaluationResponse} containing the evaluation results
   * @throws EvaluationException if an error occurs during the evaluation process, such as a network
   *     issue or invalid request
   */
  public BatchEvaluationResponse evaluateBatch(BatchEvaluationRequest request)
      throws EvaluationException {
    return this.makeRequest(request, "/evaluate/v1/batch", BatchEvaluationResponse.class);
  }

  private <T> T makeRequest(Object request, String path, Class<T> clazz)
      throws EvaluationException {
    URL url;
    try {
      url = new URI(String.format("%s%s", this.baseURL, path)).toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new EvaluationException(e);
    }

    Response response = null;
    try {
      RequestBody body =
          RequestBody.create(
              this.objectMapper.writeValueAsString(request), MediaType.parse("application/json"));

      Request.Builder httpRequest = new Request.Builder().url(url).method("POST", body);

      if (this.headers != null) {
        this.headers.forEach(httpRequest::addHeader);
      }

      if (this.authenticationStrategy != null) {
        httpRequest.addHeader(
            "Authorization", this.authenticationStrategy.getAuthorizationHeader());
      }

      response = httpClient.newCall(httpRequest.build()).execute();

      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new EvaluationException(error);
      }
      return this.objectMapper.readValue(response.body().string(), clazz);
    } catch (IOException e) {
      throw new EvaluationException(e);
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }
}
