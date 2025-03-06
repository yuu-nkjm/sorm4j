package org.nkjmlab.sorm4j.internal.result;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.nkjmlab.sorm4j.container.RowMap;
import org.nkjmlab.sorm4j.container.sql.result.InsertResult;

/** Represents a result from an insert operation with auto-generated keys. */
public final class InsertResultImpl implements InsertResult {

  public static final InsertResult EMPTY_INSERT_RESULT =
      new InsertResultImpl(new int[0], RowMap.create());
  private final int[] rowsModified;
  private final RowMap generatedKeys;
  private final int countRowsModified;

  /**
   * @param rowsModified Returns the row count by executing a insert statement
   * @param generatedKeys auto-generated keys by executing a insert statement
   */
  public InsertResultImpl(int[] rowsModified, RowMap generatedKeys) {
    this.rowsModified = rowsModified;
    this.generatedKeys = generatedKeys;
    this.countRowsModified = IntStream.of(rowsModified).sum();
  }

  /**
   * Returns the row count by executing a insert statement {@link
   * java.sql.PreparedStatement#executeUpdate()}.
   */
  @Override
  public int[] getRowsModified() {
    return rowsModified;
  }

  /**
   * Returns auto-generated keys.
   *
   * @return
   */
  @Override
  public RowMap getGeneratedKeys() {
    return generatedKeys;
  }

  @Override
  public String toString() {
    return "InsertResultImpl [rowsModified="
        + Arrays.toString(rowsModified)
        + ", generatedKeys="
        + generatedKeys
        + "]";
  }

  @Override
  public int countRowsModified() {
    return countRowsModified;
  }
}
