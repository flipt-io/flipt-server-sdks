package io.flipt.api.flags;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.flipt.api.authentication.AuthenticationStrategy;
import io.flipt.api.error.FliptException;
import io.flipt.api.flags.models.ListFlagsResponse;
import io.flipt.api.models.CommonParameters;
import io.flipt.api.models.ListParameters;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Flag {
  private final OkHttpClient httpClient;
  private final String baseURL;
  private final AuthenticationStrategy authenticationStrategy;
  private final Map<String, String> headers;
  private final ObjectMapper objectMapper;
  private final Consumer<FliptException> unhandledExceptionProcessor;

  private Flag(FlagBuilder builder) {
    this.httpClient = builder.httpClient;
    this.baseURL = builder.baseURL;
    this.authenticationStrategy = builder.authenticationStrategy;
    this.headers = builder.headers;
    this.objectMapper =
        JsonMapper.builder()
            .addModule(new Jdk8Module())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .addModule(new JavaTimeModule())
            .build();
    this.unhandledExceptionProcessor = builder.unhandledExceptionProcessor;
  }

  public static FlagBuilder builder() {
    return new FlagBuilder();
  }

  public static class FlagBuilder {
    private OkHttpClient httpClient;
    private String baseURL;
    private AuthenticationStrategy authenticationStrategy;
    private Map<String, String> headers;
    private Consumer<FliptException> unhandledExceptionProcessor;

    public FlagBuilder httpClient(OkHttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public FlagBuilder baseURL(String baseURL) {
      this.baseURL = baseURL;
      return this;
    }

    public FlagBuilder authenticationStrategy(AuthenticationStrategy authenticationStrategy) {
      this.authenticationStrategy = authenticationStrategy;
      return this;
    }

    public FlagBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public FlagBuilder setUnhandledExceptionProcessor(Consumer<FliptException> processor) {
      this.unhandledExceptionProcessor = processor;
      return this;
    }

    public Flag build() {
      return new Flag(this);
    }
  }

  public io.flipt.api.flags.models.Flag getFlag(String namespaceKey, String flagKey) throws FlagException {
    return getFlag(namespaceKey, flagKey, CommonParameters.builder().build());
  }

  public io.flipt.api.flags.models.Flag getFlag(String namespaceKey, String flagKey, CommonParameters params)
      throws FlagException {
    return this.makeGetRequest(
        String.format("/api/v1/namespaces/%s/flags/%s", namespaceKey, flagKey),
        params,
        io.flipt.api.flags.models.Flag.class
    );
  }

  public ListFlagsResponse listFlags(String namespaceKey) throws FlagException {
    return listFlags(namespaceKey, ListParameters.builder().build());
  }

  public ListFlagsResponse listFlags(String namespaceKey, ListParameters params) throws FlagException {
    return this.makeGetRequest(
        String.format("/api/v1/namespaces/%s/flags", namespaceKey), params, ListFlagsResponse.class
    );
  }

  private Map<String, String> toQueryParamMap(Object object) {
    ObjectNode jsonNode = objectMapper.valueToTree(object);  // Convert object to JSON node
    Map<String, String> queryParams = new HashMap<>();

    // Iterate over the fields of the JSON node and add them to the query map
    jsonNode.fields().forEachRemaining(entry -> {
      queryParams.put(entry.getKey(), entry.getValue().asText());
    });

    return queryParams;
  }

  private <T> T makeGetRequest(String path, Object params, Class<T> clazz)
      throws FlagException {
    URL url = null;
    try {
      HttpUrl httpUrl = HttpUrl.parse(String.format("%s%s", this.baseURL, path));
      Objects.requireNonNull(httpUrl, "Invalid URL");
      HttpUrl.Builder httpBuilder = httpUrl.newBuilder();

      if (Objects.nonNull(params)) {
        Map<String, String> queryParams = toQueryParamMap(params);
        queryParams.forEach(httpBuilder::addQueryParameter);
      }

      url = httpBuilder.build().url();
    } catch (Exception e) {
      throw new FlagException("Invalid URL or query params", e);
    }

    Request.Builder httpRequest = new Request.Builder().get().url(url);

    if (this.headers != null) {
      this.headers.forEach(httpRequest::addHeader);
    }

    if (Objects.nonNull(this.authenticationStrategy)) {
      httpRequest.addHeader("Authorization", this.authenticationStrategy.getAuthorizationHeader());
    }

    try (Response response = httpClient.newCall(httpRequest.build()).execute()) {
      if (!response.isSuccessful()) {
        Error error = this.objectMapper.readValue(response.body().string(), Error.class);
        throw new FlagException(error);
      }
      return objectMapper.readValue(response.body().string(), clazz);
    } catch (IOException e) {
      throw new FlagException(e);
    }
  }
}
