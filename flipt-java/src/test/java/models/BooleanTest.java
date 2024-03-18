package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.flipt.api.evaluation.models.BooleanEvaluationResponse;
import io.flipt.api.evaluation.models.EvaluationRequest;

public class BooleanTest {
  private final EvaluationRequest request;

  private final BooleanEvaluationResponse expectation;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public BooleanTest(
      @JsonProperty("request") EvaluationRequest request,
      @JsonProperty("expectation") BooleanEvaluationResponse expectation) {
    this.request = request;
    this.expectation = expectation;
  }

  @JsonProperty("request")
  public EvaluationRequest getRequest() {
    return request;
  }

  @JsonProperty("expectation")
  public BooleanEvaluationResponse getExpectation() {
    return expectation;
  }
}
