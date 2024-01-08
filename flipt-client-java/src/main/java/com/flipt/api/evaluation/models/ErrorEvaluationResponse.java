/* (C) 2024 */
package com.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorEvaluationResponse {
  private final String flagKey;

  private final String namespaceKey;

  private final String reason;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ErrorEvaluationResponse(
      @JsonProperty("flagKey") String flagKey,
      @JsonProperty("namespaceKey") String namespaceKey,
      @JsonProperty("reason") String reason) {
    this.flagKey = flagKey;
    this.namespaceKey = namespaceKey;
    this.reason = reason;
  }

  @JsonProperty("flagKey")
  public String getFlagKey() {
    return flagKey;
  }

  @JsonProperty("namespaceKey")
  public String getNamespaceKey() {
    return namespaceKey;
  }

  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }
}
