package io.flipt.api.error;

public abstract class FliptException extends Exception {

    public FliptException() {
      super();
    }

    public FliptException(Throwable cause) {
      super(cause);
    }

    public FliptException(String message, Throwable cause) {
      super(message, cause);
    }
}
