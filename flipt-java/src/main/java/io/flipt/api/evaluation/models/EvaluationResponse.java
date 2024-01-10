package io.flipt.api.evaluation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class EvaluationResponse {
  private final String type;

  private final Optional<BooleanEvaluationResponse> booleanResponse;

  private final Optional<VariantEvaluationResponse> variantResponse;

  private final Optional<ErrorEvaluationResponse> errorResponse;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public EvaluationResponse(
      @JsonProperty("type") String type,
      @JsonProperty("booleanResponse") Optional<BooleanEvaluationResponse> booleanResponse,
      @JsonProperty("variantResponse") Optional<VariantEvaluationResponse> variantResponse,
      @JsonProperty("errorResponse") Optional<ErrorEvaluationResponse> errorResponse) {
    this.type = type;
    this.booleanResponse = booleanResponse;
    this.variantResponse = variantResponse;
    this.errorResponse = errorResponse;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("booleanResponse")
  public Optional<BooleanEvaluationResponse> getBooleanResponse() {
    return booleanResponse;
  }

  @JsonProperty("variantResponse")
  public Optional<VariantEvaluationResponse> getVariantResponse() {
    return variantResponse;
  }

  @JsonProperty("errorResponse")
  public Optional<ErrorEvaluationResponse> getErrorResponse() {
    return errorResponse;
  }
}
