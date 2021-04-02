package org.nkjmlab.sorm4j.internal.sql;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Represents a row joined two rows.
 *
 * @author nkjm
 *
 * @param <T>
 * @param <S>
 */
@Experimental
public class JoinedRow<T, S> {

  private final T left;
  private final S right;

  public JoinedRow(T left, S right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Gets a left part of row.
   *
   * @return
   */
  public T getLeft() {
    return left;
  }

  /**
   * Gets a right part of row.
   *
   * @return
   */
  public S getRight() {
    return right;
  }

  @Override
  public String toString() {
    return "JoinedRow [left=" + left + ", right=" + right + "]";
  }

}
