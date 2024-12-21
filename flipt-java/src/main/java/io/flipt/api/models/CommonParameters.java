package io.flipt.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommonParameters {

  @JsonProperty("reference")
  private String reference;

  public CommonParameters(Builder builder) {
    this.reference = builder.reference;
  }

  public String getReference() {
    return reference;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String reference;

    public Builder reference(String reference) {
      this.reference = reference;
      return this;
    }

    public CommonParameters build() {
      return new CommonParameters(this);
    }
  }
}
