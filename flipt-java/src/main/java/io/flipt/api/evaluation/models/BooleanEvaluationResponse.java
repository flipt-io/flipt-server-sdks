package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanEvaluationResponse {
  private final boolean enabled;

  private final String flagKey;

  private final EvaluationReason reason;

  private final float requestDurationMillis;

  private final String timestamp;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public BooleanEvaluationResponse(
      @JsonProperty("enabled") boolean enabled,
      @JsonProperty("flagKey") String flagKey,
      @JsonProperty("reason") EvaluationReason reason,
      @JsonProperty("requestDurationMillis") float requestDurationMillis,
      @JsonProperty("timesteamp") String timestamp) {
    this.enabled = enabled;
    this.flagKey = flagKey;
    this.reason = reason;
    this.requestDurationMillis = requestDurationMillis;
    this.timestamp = timestamp;
  }

  @JsonProperty("enabled")
  public boolean isEnabled() {
    return enabled;
  }

  @JsonProperty("flagKey")
  public String getFlagKey() {
    return flagKey;
  }

  @JsonProperty("reason")
  public EvaluationReason getReason() {
    return reason;
  }

  @JsonProperty("requestDurationMillis")
  public float getRequestDurationMillis() {
    return requestDurationMillis;
  }

  @JsonProperty("timestamp")
  public String getTimestamp() {
    return timestamp;
  }
}
