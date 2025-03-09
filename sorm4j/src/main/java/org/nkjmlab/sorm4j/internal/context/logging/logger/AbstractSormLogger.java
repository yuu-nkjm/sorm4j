package org.nkjmlab.sorm4j.internal.context.logging.logger;

import java.sql.Connection;
import java.util.Collection;
import java.util.stream.IntStream;

import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.util.MethodInvokerInfoUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSqlFactory;

public abstract class AbstractSormLogger implements SormLogger {

  @Override
  public void logBeforeSql(String tag, Connection connection, String sql, Object... parameters) {
    logBeforeSql(tag, connection, ParameterizedSqlFactory.create(sql, parameters));
  }

  @Override
  public void logBeforeSql(String tag, Connection connection, ParameterizedSql psql) {
    Object[] params = {
      tag, getOutsideInvokerOfLibrary(), psql.getExecutableSql(), getDbUrl(connection)
    };
    debug(
        ParameterizedStringFormatter.LENGTH_256.format(
            "[{}] At {}, Execute SQL [{}] to [{}]", params));
  }

  @Override
  public void logBeforeMultiRow(
      String tag, Connection connection, Class<?> clazz, int length, String tableName) {
    Object[] params = {
      tag, getOutsideInvokerOfLibrary(), length, clazz, tableName, getDbUrl(connection)
    };
    debug(
        ParameterizedStringFormatter.LENGTH_256.format(
            "[{}] At {}, Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]",
            params));
  }

  @Override
  public void logAfterQuery(String tag, long elapsedTime, Object ret) {
    Object[] params = {
      getTagAndElapsedTime(tag, elapsedTime),
      ret instanceof Collection ? ((Collection<?>) ret).size() : 1
    };
    debug(ParameterizedStringFormatter.LENGTH_256.format("{} Read [{}] objects", params));
  }

  @Override
  public void logAfterUpdate(String tag, long elapsedTime, int ret) {
    Object[] params = {getTagAndElapsedTime(tag, elapsedTime), ret};
    debug(ParameterizedStringFormatter.LENGTH_256.format("{} Affect [{}] rows", params));
  }

  @Override
  public void logAfterMultiRow(String tag, long elapsedTime, int[] result) {
    Object[] params = {getTagAndElapsedTime(tag, elapsedTime), IntStream.of(result).sum()};
    debug(ParameterizedStringFormatter.LENGTH_256.format("{} Affect [{}] objects", params));
  }

  @Override
  public void logMapping(String tag, String mappingInfo) {
    debug(4, "[{}]" + System.lineSeparator() + "{}", tag, mappingInfo);
  }

  private String getTagAndElapsedTime(String tag, long elapsedTime) {
    return "["
        + tag
        + "]"
        + " ["
        + String.format("%.3f", (double) elapsedTime / 1000 / 1000)
        + " msec] :";
  }

  private String getDbUrl(Connection connection) {
    return Try.getOrElse(() -> connection.getMetaData().getURL(), "");
  }

  private static String getOutsideInvokerOfLibrary() {
    return MethodInvokerInfoUtils.getOutsideInvoker("org.nkjmlab.sorm4j");
  }
}
