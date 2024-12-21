package io.flipt.api.flags.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.flipt.api.models.PaginatedResponse;
import java.util.List;

public class ListFlagsResponse extends PaginatedResponse {

  @JsonProperty("flags")
  private List<Flag> flags;

  public List<Flag> getFlags() {
    return flags;
  }

  public void setFlags(List<Flag> flags) {
    this.flags = flags;
  }
}
