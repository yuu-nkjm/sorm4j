package org.nkjmlab.sorm4j.internal.sql.result;

import java.util.Collections;
import java.util.List;
import org.nkjmlab.sorm4j.result.InsertResult;

/**
 * Represents a result from an insert operation with auto-generated keys.
 */
public final class InsertResultImpl<T> implements InsertResult<T> {

  private static final InsertResult<?> EMPTY_INSERT_RESULT =
      new InsertResultImpl<>(new int[] {0}, null, Collections.emptyList());
  private final int[] rowsModified;
  private final T object;

  private final List<Object> autoGeneratedKeys;

  /**
   *
   * @param rowsModified Returns the row count by executing a insert statement
   * @param lastInsertedObject last inserted object with auto-generated keys
   * @param autoGeneratedKeys auto-generated keys by executing a insert statement
   */
  public InsertResultImpl(int[] rowsModified, T lastInsertedObject,
      List<Object> autoGeneratedKeys) {
    this.rowsModified = rowsModified;
    this.object = lastInsertedObject;
    this.autoGeneratedKeys = autoGeneratedKeys;
  }

  /**
   * Returns the row count by executing a insert statement
   * {@link java.sql.PreparedStatement#executeUpdate()}.
   */
  @Override
  public int[] getRowsModified() {
    return rowsModified;
  }

  /**
   * Returns the object which insert last with auto-generated keys.
   *
   * @return
   */
  @Override
  public T getObject() {
    return object;
  }

  /**
   * Returns auto-generated keys.
   *
   * @return
   */
  @Override
  public List<Object> getAutoGeneratedKeys() {
    return autoGeneratedKeys;
  }

}
