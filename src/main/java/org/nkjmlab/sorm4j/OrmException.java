package org.nkjmlab.sorm4j;

/**
 * Represents an exception thrown by Sorm4j.
 */
public final class OrmException extends RuntimeException {

  private static final long serialVersionUID = -3645955311944195665L;

  public OrmException(String message) {
    super(message);
  }

  public OrmException(String message, Throwable cause) {
    super(message, cause);
  }
}

