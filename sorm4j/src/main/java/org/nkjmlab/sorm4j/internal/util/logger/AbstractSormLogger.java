package org.nkjmlab.sorm4j.internal.util.logger;

import java.sql.Connection;
import java.util.Collection;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.internal.util.MethodInvokerInfoUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormat;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

public abstract class AbstractSormLogger implements SormLogger {

  @Override
  public void logBeforeSql(String tag, Connection connection, String sql, Object... parameters) {
    logBeforeSql(tag, connection, ParameterizedSqlParser.parse(sql, parameters));
  }

  @Override
  public void logBeforeSql(String tag, Connection connection, ParameterizedSql psql) {
    Object[] params = {tag, getOutsideInvokerOfLibrary(), psql.getBindedSql(), getDbUrl(connection)};
    debug(ParameterizedStringFormat.DEFAULT.format("[{}] At {}, Execute SQL [{}] to [{}]", params));
  }


  @Override
  public void logBeforeMultiRow(String tag, Connection connection, Class<?> clazz, int length,
      String tableName) {
    Object[] params = {tag, getOutsideInvokerOfLibrary(), length, clazz, tableName, getDbUrl(connection)};
    debug(ParameterizedStringFormat.DEFAULT.format("[{}] At {}, Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]", params));
  }


  @Override
  public void logAfterQuery(String tag, long elapsedTime, Object ret) {
    Object[] params = {getTagAndElapsedTime(tag, elapsedTime), ret instanceof Collection ? ((Collection<?>) ret).size() : 1};
    debug(ParameterizedStringFormat.DEFAULT.format("{} Read [{}] objects", params));
  }

  @Override
  public void logAfterUpdate(String tag, long elapsedTime, int ret) {
    Object[] params = {getTagAndElapsedTime(tag, elapsedTime), ret};
    debug(ParameterizedStringFormat.DEFAULT.format("{} Affect [{}] rows", params));
  }


  @Override
  public void logAfterMultiRow(String tag, long elapsedTime, int[] result) {
    Object[] params = {getTagAndElapsedTime(tag, elapsedTime), IntStream.of(result).sum()};
    debug(ParameterizedStringFormat.DEFAULT.format("{} Affect [{}] objects", params));
  }

  @Override
  public void logMapping(String tag, String mappingInfo) {
    debug(4, "[{}]" + System.lineSeparator() + "{}", tag, mappingInfo);
  }

  private String getTagAndElapsedTime(String tag, long elapsedTime) {
    return "[" + tag + "]" + " [" + String.format("%.3f", (double) elapsedTime / 1000 / 1000)
        + " msec] :";
  }

  private String getDbUrl(Connection connection) {
    return Try.getOrElse(() -> connection.getMetaData().getURL(), "");
  }


  private static String getOutsideInvokerOfLibrary() {
    return MethodInvokerInfoUtils.getOutsideInvoker("org.nkjmlab.sorm4j");

  }

}
