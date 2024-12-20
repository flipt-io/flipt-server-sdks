package io.flipt.api.flags;

public class FlagException extends Exception {

  public FlagException() {
    super();
  }

  public FlagException(Throwable cause) {
    super(cause);
  }

  public FlagException(String message, Exception e) {
    super(message, e);
  }
}
