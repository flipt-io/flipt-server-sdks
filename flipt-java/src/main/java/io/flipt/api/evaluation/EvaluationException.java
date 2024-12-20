package io.flipt.api.evaluation;

import io.flipt.api.error.FliptException;

public class EvaluationException extends FliptException {

  public EvaluationException() {
    super();
  }

  public EvaluationException(Throwable cause) {
    super(cause);
  }
}
