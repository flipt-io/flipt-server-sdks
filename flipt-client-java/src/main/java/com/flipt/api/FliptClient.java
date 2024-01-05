package com.flipt.api;

import com.flipt.api.evaluation.Evaluation;
import okhttp3.OkHttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class FliptClient {
    public Evaluation evaluation;

    public FliptClient(String url, String token, int timeout) throws MalformedURLException {
        OkHttpClient httpClient = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(timeout)).build();
        URL baseURL = new URL(url);
        this.evaluation = new Evaluation(httpClient, baseURL, token);
    }
}
