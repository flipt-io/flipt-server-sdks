package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Corpus {
  private final List<VariantTest> variantTests;
  private final List<BooleanTest> booleanTests;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Corpus(
      @JsonProperty("VARIANT") List<VariantTest> variantTests,
      @JsonProperty("BOOLEAN") List<BooleanTest> booleanTests) {
    this.variantTests = variantTests;
    this.booleanTests = booleanTests;
  }

  public List<VariantTest> getVariantTests() {
    return variantTests;
  }

  public List<BooleanTest> getBooleanTests() {
    return booleanTests;
  }
}
