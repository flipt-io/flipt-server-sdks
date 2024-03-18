package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.flipt.api.evaluation.models.*;

public class VariantTest {
  private final EvaluationRequest request;

  private final VariantEvaluationResponse expectation;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public VariantTest(
      @JsonProperty("request") EvaluationRequest request,
      @JsonProperty("expectation") VariantEvaluationResponse expectation) {
    this.request = request;
    this.expectation = expectation;
  }

  @JsonProperty("request")
  public EvaluationRequest getRequest() {
    return request;
  }

  @JsonProperty("expectation")
  public VariantEvaluationResponse getExpectation() {
    return expectation;
  }
}
