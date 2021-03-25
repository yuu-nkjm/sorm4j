package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

final class BatchHelper {

  private final int batchSize;
  private final PreparedStatement stmt;
  private int counter;
  private final List<int[]> result = new ArrayList<>();

  public BatchHelper(int batchSize, PreparedStatement stmt) {
    this.batchSize = batchSize;
    this.stmt = stmt;
  }

  public void addBatchAndExecuteIfReachedThreshold() throws SQLException {
    addBatch();
    if ((counter + 1) % batchSize == 0) {
      executeBatch();
    }
  }

  private void addBatch() throws SQLException {
    stmt.addBatch();
    counter++;
  }

  public int[] finish() throws SQLException {
    executeBatch();
    return result.stream().flatMapToInt(e -> IntStream.of(e)).toArray();
  }

  private void executeBatch() throws SQLException {
    if (counter == 0) {
      return;
    }
    int[] tmp = stmt.executeBatch();
    result.add(tmp);
    counter = 0;
  }
}
