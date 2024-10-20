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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import okhttp3.*;

public class Evaluation {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final AuthenticationStrategy authenticationStrategy;
  private final Map<String, String> headers;
  private final ObjectMapper objectMapper;

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
  }

  public static EvaluationBuilder builder() {
    return new EvaluationBuilder();
  }

  public static class EvaluationBuilder {
    private OkHttpClient httpClient;
    private String baseURL;
    private AuthenticationStrategy authenticationStrategy;
    private Map<String, String> headers;

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

    public Evaluation build() {
      return new Evaluation(this);
    }
  }

  public VariantEvaluationResponse evaluateVariant(EvaluationRequest request)
      throws EvaluationException {
    return this.makeRequest(request, "/evaluate/v1/variant", VariantEvaluationResponse.class);
  }

  public BooleanEvaluationResponse evaluateBoolean(EvaluationRequest request)
      throws EvaluationException {
    return this.makeRequest(request, "/evaluate/v1/boolean", BooleanEvaluationResponse.class);
  }

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
