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

import okhttp3.*;

public class Evaluation {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final AuthenticationStrategy authenticationStrategy;
  private final Map<String, String> headers;
  private final ObjectMapper objectMapper;

  public Evaluation(
      OkHttpClient httpClient, String baseURL, AuthenticationStrategy authenticationStrategy,
      Map<String, String> headers) {
    this.httpClient = httpClient;
    this.baseURL = baseURL;
    this.authenticationStrategy = authenticationStrategy;
    this.headers = headers;
    this.objectMapper = JsonMapper.builder()
        .addModule(new Jdk8Module())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
  }

  @SuppressWarnings("resource")
  public VariantEvaluationResponse evaluateVariant(EvaluationRequest request) {
    URL url;

    try {
      url = new URL(String.format("%s%s", this.baseURL, "/evaluate/v1/variant"));
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

    try {
      url = new URL(String.format("%s%s", this.baseURL, "/evaluate/v1/boolean"));
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
      body = RequestBody.create(
          this.objectMapper.writeValueAsString(request), MediaType.parse("application/json"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    URL url;
    try {
      url = new URL(String.format("%s%s", this.baseURL, "/evaluate/v1/batch"));
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
      body = RequestBody.create(
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
