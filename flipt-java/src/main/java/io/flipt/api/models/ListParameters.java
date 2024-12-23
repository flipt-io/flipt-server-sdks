package io.flipt.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ListParameters extends CommonParameters {

  @JsonProperty("limit")
  private final Integer limit;

  @JsonProperty("offset")
  private final Integer offset;

  @JsonProperty("pageToken")
  private final String pageToken;

  public ListParameters(Builder builder) {
    super(builder);
    this.limit = builder.limit;
    this.offset = builder.offset;
    this.pageToken = builder.pageToken;
  }

  public Integer getLimit() {
    return limit;
  }

  public Integer getOffset() {
    return offset;
  }

  public String getPageToken() {
    return pageToken;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends CommonParameters.Builder {
    private int limit;
    private int offset;
    private String pageToken;

    private Builder() {}

    public Builder limit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder offset(int offset) {
      this.offset = offset;
      return this;
    }

    public Builder pageToken(String pageToken) {
      this.pageToken = pageToken;
      return this;
    }

    public ListParameters build() {
      return new ListParameters(this);
    }
  }
}
