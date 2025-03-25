package org.nkjmlab.sorm4j.common.exception;

/** Represents an exception thrown by Sorm4j. */
public final class SormException extends RuntimeException {

  private static final long serialVersionUID = -3645955311944195665L;

  public SormException(String message) {
    super(message);
  }

  public SormException(String message, Throwable cause) {
    super(message, cause);
  }
}
