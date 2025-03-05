package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public interface MultiRowProcessor<T> {

  int[] multiRowInsert(Connection con, T[] objects);

  int[] multiRowMerge(Connection con, T[] objects);

  void setPrametersOfMultiRow(PreparedStatement stmt, T[] objects) throws SQLException;

  int[] batch(Connection con, String sql, Function<T, Object[]> parameterCreator, T[] objects);
}
