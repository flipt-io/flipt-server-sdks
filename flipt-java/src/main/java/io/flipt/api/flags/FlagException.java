package io.flipt.api.flags;

import io.flipt.api.error.FliptException;

public class FlagException extends FliptException {

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
