package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EvaluationRequest {
  private final String namespaceKey;
  private final String flagKey;
  private final String entityId;
  private final Map<String, String> context;
  private final Optional<String> reference;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public EvaluationRequest(
      @JsonProperty("namespace_key") String namespaceKey,
      @JsonProperty("flag_key") String flagKey,
      @JsonProperty("entity_id") String entityId,
      @JsonProperty("context") Map<String, String> context,
      @JsonProperty("reference") Optional<String> reference) {
    this.namespaceKey = namespaceKey;
    this.flagKey = flagKey;
    this.entityId = entityId;
    this.context = context;
    this.reference = reference;
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

  @JsonProperty("reference")
  public Optional<String> getReference() {
    return reference;
  }

  public static EvaluationRequestBuilder builder() {
    return new EvaluationRequestBuilder();
  }

  public static final class EvaluationRequestBuilder {
    private String namespaceKey;
    private String flagKey;
    private String entityId;
    private Map<String, String> context;
    private Optional<String> reference;

    public EvaluationRequestBuilder() {}

    public EvaluationRequestBuilder namespaceKey(String namespaceKey) {
      this.namespaceKey = namespaceKey;
      return this;
    }

    public EvaluationRequestBuilder flagKey(String flagKey) {
      this.flagKey = flagKey;
      return this;
    }

    public EvaluationRequestBuilder entityId(String entityId) {
      this.entityId = entityId;
      return this;
    }

    public EvaluationRequestBuilder context(Map<String, String> context) {
      this.context = context;
      return this;
    }

    public EvaluationRequestBuilder reference(String reference) {
      this.reference = Optional.of(reference);
      return this;
    }

    public EvaluationRequest build() {
      return new EvaluationRequest(namespaceKey, flagKey, entityId, context, reference);
    }
  }
}
