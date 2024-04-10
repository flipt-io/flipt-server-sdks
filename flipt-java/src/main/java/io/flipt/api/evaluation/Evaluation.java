package io.flipt.api.evaluation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.error.Error;
import io.flipt.api.evaluation.models.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import okhttp3.*;

public class Evaluation {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final AuthenticationStrategy authenticationStrategy;
  private final Map<String, String> headers;
  private final ObjectMapper objectMapper;
  private final Optional<String> reference;

  private Evaluation(EvaluationBuilder builder) {
    this.httpClient = builder.httpClient;
    this.baseURL = builder.baseURL;
    this.authenticationStrategy = builder.authenticationStrategy;
    this.headers = builder.headers;
    this.reference = builder.reference;
    this.objectMapper =
        JsonMapper.builder()
            .addModule(new Jdk8Module())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
  }

  public static EvaluationBuilder builder() {
    return new EvaluationBuilder();
  }

  public static class EvaluationBuilder {
    private OkHttpClient httpClient;
    private String baseURL;
    private AuthenticationStrategy authenticationStrategy;
    private Map<String, String> headers;
    private Optional<String> reference = Optional.empty();

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

    public EvaluationBuilder reference(Optional<String> reference) {
      this.reference = reference;
      return this;
    }

    public Evaluation build() {
      return new Evaluation(this);
    }
  }

  @SuppressWarnings("resource")
  public VariantEvaluationResponse evaluateVariant(EvaluationRequest request) {
    URL url;

    String path = "/evaluate/v1/variant";
    if (this.reference.isPresent()) {
      path = String.format("%s?reference=%s", path, this.reference.get());
    }

    try {
      url = new URL(String.format("%s%s", this.baseURL, path));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Request.Builder requestBuilder = makeRequest(request, url);

    Response response = null;

    try {
      response = httpClient.newCall(requestBuilder.build()).execute();
      assert response.body() != null;

      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }
      return this.objectMapper.readValue(response.body().string(), VariantEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  @SuppressWarnings("resource")
  public BooleanEvaluationResponse evaluateBoolean(EvaluationRequest request) {
    URL url;

    String path = "/evaluate/v1/boolean";
    if (this.reference.isPresent()) {
      path = String.format("%s?reference=%s", path, this.reference.get());
    }

    try {
      url = new URL(String.format("%s%s", this.baseURL, path));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Request.Builder requestBuilder = makeRequest(request, url);

    Response response = null;
    try {
      response = httpClient.newCall(requestBuilder.build()).execute();
      assert response.body() != null;
      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }

      return this.objectMapper.readValue(response.body().string(), BooleanEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  @SuppressWarnings("resource")
  public BatchEvaluationResponse evaluateBatch(BatchEvaluationRequest request) {
    RequestBody body;

    try {
      body =
          RequestBody.create(
              this.objectMapper.writeValueAsString(request), MediaType.parse("application/json"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    URL url;

    String path = "/evaluate/v1/batch";
    if (this.reference.isPresent()) {
      path = String.format("%s?reference=%s", path, this.reference.get());
    }

    try {
      url = new URL(String.format("%s%s", this.baseURL, path));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Request.Builder httpRequest = new Request.Builder().url(url).method("POST", body);

    if (this.headers != null) {
      this.headers.forEach(httpRequest::addHeader);
    }

    if (this.authenticationStrategy != null) {
      httpRequest.addHeader("Authorization", this.authenticationStrategy.getAuthorizationHeader());
    }

    Response response = null;

    try {
      response = httpClient.newCall(httpRequest.build()).execute();
      assert response.body() != null;
      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }

      return this.objectMapper.readValue(response.body().string(), BatchEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  private Request.Builder makeRequest(EvaluationRequest request, URL url) {
    RequestBody body;

    try {
      body =
          RequestBody.create(
              this.objectMapper.writeValueAsString(request), MediaType.parse("application/json"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Request.Builder httpRequest = new Request.Builder().url(url).method("POST", body);

    if (this.headers != null) {
      this.headers.forEach(httpRequest::addHeader);
    }

    if (this.authenticationStrategy != null) {
      httpRequest.addHeader("Authorization", this.authenticationStrategy.getAuthorizationHeader());
    }

    return httpRequest;
  }
}
