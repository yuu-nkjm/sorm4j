package org.nkjmlab.sorm4j;

/**
 * Represents an exception thrown by Sorm.
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


}

