package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EvaluationRequest {
  private final String namespaceKey;
  private final String flagKey;
  private final String entityId;
  private final Map<String, String> context;

  public EvaluationRequest(
      String namespaceKey, String flagKey, String entityId, Map<String, String> context) {
    this.namespaceKey = namespaceKey;
    this.flagKey = flagKey;
    this.entityId = entityId;
    this.context = context;
  }

  @JsonProperty("namespaceKey")
  public String getNamespaceKey() {
    return namespaceKey;
  }

  @JsonProperty("flagKey")
  public String getFlagKey() {
    return flagKey;
  }

  @JsonProperty("entityId")
  public String getEntityId() {
    return entityId;
  }

  @JsonProperty("context")
  public Map<String, String> getContext() {
    return context;
  }
}
