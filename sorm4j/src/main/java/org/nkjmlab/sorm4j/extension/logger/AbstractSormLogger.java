package org.nkjmlab.sorm4j.extension.logger;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public abstract class AbstractSormLogger implements SormLogger {

  @Override
  public void logBeforeSql(String tag, Connection connection, String sql, Object... parameters) {
    logBeforeSql(tag, connection, ParameterizedSql.parse(sql, parameters));
  }

  @Override
  public void logBeforeSql(String tag, Connection connection, ParameterizedSql psql) {
    debug(StringUtils.format("[{}] At {}, Execute SQL [{}] to [{}]", tag, getCaller(),
        psql.getBindedSql(), getDbUrl(connection)));
  }


  @Override
  public void logBeforeMultiRow(String tag, Connection connection, Class<?> clazz, int length,
      String tableName) {
    debug(StringUtils.format(
        "[{}] At {}, Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]", tag,
        getCaller(), length, clazz, tableName, getDbUrl(connection)));
  }


  @Override
  public void logAfterQuery(String tag, long elapsedTime, Object ret) {
    debug(StringUtils.format("{} Read [{}] objects", getTagAndElapsedTime(tag, elapsedTime),
        ret instanceof Collection ? ((Collection<?>) ret).size() : 1));
  }

  @Override
  public void logAfterUpdate(String tag, long elapsedTime, int ret) {
    debug(StringUtils.format("{} Affect [{}] rows", getTagAndElapsedTime(tag, elapsedTime), ret));
  }


  @Override
  public void logAfterMultiRow(String tag, long elapsedTime, int[] result) {
    debug(StringUtils.format("{} Affect [{}] objects", getTagAndElapsedTime(tag, elapsedTime),
        IntStream.of(result).sum()));
  }

  @Override
  public void logMapping(String tag, String mappingInfo) {
    debug("[{}]" + System.lineSeparator() + "{}", tag, mappingInfo);
  }

  private String getTagAndElapsedTime(String tag, long elapsedTime) {
    return "[" + tag + "]" + " [" + String.format("%.3f", (double) elapsedTime / 1000 / 1000)
        + " msec] :";
  }

  private String getDbUrl(Connection connection) {
    return Try.getOrDefault(() -> connection.getMetaData().getURL(), "");
  }


  private String getCaller() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    String caller = Arrays.stream(stackTrace)
        .filter(s -> !s.getClassName().startsWith("org.nkjmlab.sorm4j")
            && !s.getClassName().startsWith("java."))
        .findFirst().map(se -> se.getClassName() + "." + se.getMethodName() + "(" + se.getFileName()
            + ":" + se.getLineNumber() + ")")
        .orElseGet(() -> "");
    return caller;
  }
}