package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BatchEvaluationRequest {
  private final Optional<String> requestId;
  private final List<EvaluationRequest> requests;
  private Optional<String> reference;

  public BatchEvaluationRequest(
      Optional<String> requestId, List<EvaluationRequest> requests, Optional<String> reference) {
    this.requestId = requestId;
    this.requests = requests;
    this.reference = reference;
  }

  @JsonProperty("requests")
  public List<EvaluationRequest> getRequests() {
    return requests;
  }

  @JsonProperty("requestId")
  public Optional<String> getRequestId() {
    return requestId;
  }

  @JsonProperty("reference")
  public Optional<String> getReference() {
    return reference;
  }
}
