package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import org.nkjmlab.sorm4j.helper.SqlStatement;

public interface SqlExecutor {

  boolean execute(String sql, Object... parameters);

  ResultSet executeQuery(String sql, Object... parameters);

  int executeUpdate(String sql, Object... parameters);

  boolean execute(SqlStatement sql);

  ResultSet executeQuery(SqlStatement sql);

  int executeUpdate(SqlStatement sql);


}
