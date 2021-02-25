package org.nkjmlab.sorm4j;

import java.sql.ResultSet;

public interface SqlExecutor {

  boolean execute(String sql, Object... parameters);

  ResultSet executeQuery(String sql, Object... parameters);

  int executeUpdate(String sql, Object... parameters);

}
