package org.nkjmlab.sorm4j;

/**
 * Represents an exception thrown by Sorm4j.
 */
public final class OrmException extends RuntimeException {

  private static final long serialVersionUID = -3645955311944195665L;

  public OrmException(Throwable cause) {
    super(cause);
  }

  public OrmException(String message) {
    super(message);
  }

  public OrmException(String message, Throwable cause) {
    super(message, cause);
  }

  public static RuntimeException wrapIfCheckedException(Exception e) {
    return e instanceof RuntimeException ? (RuntimeException) e : new OrmException(e);
  }

}

