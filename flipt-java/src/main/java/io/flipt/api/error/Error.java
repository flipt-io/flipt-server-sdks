package io.flipt.api.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error extends Exception {
  private final int code;
  private final String message;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Error(@JsonProperty("code") int code, @JsonProperty("message") String message) {
    this.code = code;
    this.message = message;
  }

  @JsonProperty("code")
  public int getCode() {
    return code;
  }

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }
}
