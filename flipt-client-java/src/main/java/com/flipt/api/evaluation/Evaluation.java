package com.flipt.api.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipt.api.evaluation.models.BooleanEvaluationResponse;
import com.flipt.api.evaluation.models.EvaluationRequest;
import com.flipt.api.evaluation.models.VariantEvaluationResponse;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;

public class Evaluation {
    private final OkHttpClient httpClient;
    private final URL url;
    private final String token;

    private final ObjectMapper objectMapper;

    public Evaluation(OkHttpClient httpClient, URL url, String token) {
        this.httpClient = httpClient;
        this.url = url;
        this.token = token;
        this.objectMapper = new ObjectMapper();
    }

    public VariantEvaluationResponse variant(EvaluationRequest request) {
        Request.Builder requestBuilder = makeRequest(request);

        try {
            Response response = httpClient.newCall(requestBuilder.build()).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                return this.objectMapper.readValue(response.body().string(), VariantEvaluationResponse.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Request.Builder makeRequest(EvaluationRequest request) {
        RequestBody body;

        try {
            body = RequestBody.create(
                    this.objectMapper.writeValueAsString(request), MediaType.parse("application/json"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Request.Builder httpRequest = new Request.Builder().url(this.url).method("POST", body);

        if (!this.token.isEmpty()) {
            httpRequest.addHeader("Authorization", String.format("Bearer %s", this.token));
        }

        return httpRequest;
    }

    public BooleanEvaluationResponse _boolean(EvaluationRequest request) {
        Request.Builder requestBuilder = makeRequest(request);

        try {
            Response response = httpClient.newCall(requestBuilder.build()).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                return this.objectMapper.readValue(response.body().string(), BooleanEvaluationResponse.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
