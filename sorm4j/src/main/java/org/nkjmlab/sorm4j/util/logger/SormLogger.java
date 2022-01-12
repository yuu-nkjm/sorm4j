package org.nkjmlab.sorm4j.util.logger;

import java.sql.Connection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

@Experimental
public interface SormLogger {

  void trace(String format, Object... params);

  void debug(String format, Object... params);

  void info(String format, Object... params);

  void warn(String format, Object... params);

  void error(String format, Object... params);

  void trace(int depth, String format, Object... params);

  void debug(int depth, String format, Object... params);

  void info(int depth, String format, Object... params);

  void warn(int depth, String format, Object... params);

  void error(int depth, String format, Object... params);


  void logBeforeSql(String tag, Connection connection, String sql, Object... parameters);

  void logBeforeSql(String tag, Connection connection, ParameterizedSql psql);

  void logBeforeMultiRow(String tag, Connection connection, Class<?> objectClass, int length,
      String tableName);

  void logAfterQuery(String tag, long elapsedTime, Object ret);

  void logAfterUpdate(String tag, long elapsedTime, int ret);

  void logAfterMultiRow(String tag, long elapsedTime, int[] result);

  void logMapping(String tag, String mappingInfo);

}
