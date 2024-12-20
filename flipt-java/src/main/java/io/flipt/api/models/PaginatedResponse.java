package io.flipt.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse {

  @JsonProperty("nextPageToken")
  private String nextPageToken;

  @JsonProperty("totalCount")
  private int totalCount;

  public String getNextPageToken() {
    return nextPageToken;
  }

  public void setNextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
}
