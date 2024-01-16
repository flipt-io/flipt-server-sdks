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

  public static BatchEvaluationRequestBuilder builder() {
    return new BatchEvaluationRequestBuilder();
  }

  public static final class BatchEvaluationRequestBuilder {
    private Optional<String> requestId;
    private List<EvaluationRequest> requests;
    private Optional<String> reference;

    public BatchEvaluationRequestBuilder() {}

    public BatchEvaluationRequestBuilder requestId(Optional<String> requestId) {
      this.requestId = requestId;
      return this;
    }

    public BatchEvaluationRequestBuilder requests(List<EvaluationRequest> requests) {
      this.requests = requests;
      return this;
    }

    public BatchEvaluationRequestBuilder reference(Optional<String> reference) {
      this.reference = reference;
      return this;
    }

    public BatchEvaluationRequest build() {
      return new BatchEvaluationRequest(requestId, requests, reference);
    }
  }
}
