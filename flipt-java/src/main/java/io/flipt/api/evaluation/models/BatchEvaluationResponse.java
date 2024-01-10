package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BatchEvaluationResponse {
  private final List<EvaluationResponse> responses;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public BatchEvaluationResponse(@JsonProperty("responses") List<EvaluationResponse> responses) {
    this.responses = responses;
  }

  @JsonProperty("responses")
  public List<EvaluationResponse> getResponses() {
    return responses;
  }
}
