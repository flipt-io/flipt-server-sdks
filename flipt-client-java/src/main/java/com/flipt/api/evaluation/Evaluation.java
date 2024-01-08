/* (C) 2024 */
package com.flipt.api.evaluation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.flipt.api.error.Error;
import com.flipt.api.evaluation.models.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.*;

public class Evaluation {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final String token;
  private final ObjectMapper objectMapper;

  public Evaluation(OkHttpClient httpClient, String baseURL, String token) {
    this.httpClient = httpClient;
    this.baseURL = baseURL;
    this.token = token;
    this.objectMapper =
        JsonMapper.builder()
            .addModule(new Jdk8Module())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
  }

  public VariantEvaluationResponse variant(EvaluationRequest request) {
    URL url;

    try {
      url = new URL(String.format("%s%s", this.baseURL, "/evaluate/v1/variant"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Request.Builder requestBuilder = makeRequest(request, url);

    try {
      Response response = httpClient.newCall(requestBuilder.build()).execute();
      assert response.body() != null;

      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }
      return this.objectMapper.readValue(response.body().string(), VariantEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
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

    if (!this.token.isEmpty()) {
      httpRequest.addHeader("Authorization", String.format("Bearer %s", this.token));
    }

    return httpRequest;
  }

  public BooleanEvaluationResponse booleanEvaluation(EvaluationRequest request) {
    URL url;

    try {
      url = new URL(String.format("%s%s", this.baseURL, "/evaluate/v1/boolean"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    Request.Builder requestBuilder = makeRequest(request, url);

    try {
      Response response = httpClient.newCall(requestBuilder.build()).execute();
      assert response.body() != null;
      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }

      return this.objectMapper.readValue(response.body().string(), BooleanEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public BatchEvaluationResponse batch(BatchEvaluationRequest request) {
    RequestBody body;

    try {
      body =
          RequestBody.create(
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

    if (!this.token.isEmpty()) {
      httpRequest.addHeader("Authorization", String.format("Bearer %s", this.token));
    }

    try {
      Response response = httpClient.newCall(httpRequest.build()).execute();
      assert response.body() != null;
      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new RuntimeException(error);
      }

      return this.objectMapper.readValue(response.body().string(), BatchEvaluationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
