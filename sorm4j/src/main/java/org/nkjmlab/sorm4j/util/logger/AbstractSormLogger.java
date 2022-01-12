package org.nkjmlab.sorm4j.util.logger;

import java.sql.Connection;
import java.util.Collection;
import java.util.stream.IntStream;
import org.nkjmlab.sorm4j.internal.util.MethodInvokerInfoUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public abstract class AbstractSormLogger implements SormLogger {

  @Override
  public void logBeforeSql(String tag, Connection connection, String sql, Object... parameters) {
    logBeforeSql(tag, connection, ParameterizedSql.parse(sql, parameters));
  }

  @Override
  public void logBeforeSql(String tag, Connection connection, ParameterizedSql psql) {
    debug(ParameterizedStringUtils.newString("[{}] At {}, Execute SQL [{}] to [{}]", tag,
        getOutsideInvokerOfLibrary(), psql.getBindedSql(), getDbUrl(connection)));
  }


  @Override
  public void logBeforeMultiRow(String tag, Connection connection, Class<?> clazz, int length,
      String tableName) {
    debug(ParameterizedStringUtils.newString(
        "[{}] At {}, Execute multirow insert with [{}] objects of [{}] into [{}] on [{}]", tag,
        getOutsideInvokerOfLibrary(), length, clazz, tableName, getDbUrl(connection)));
  }


  @Override
  public void logAfterQuery(String tag, long elapsedTime, Object ret) {
    debug(ParameterizedStringUtils.newString("{} Read [{}] objects", getTagAndElapsedTime(tag, elapsedTime),
        ret instanceof Collection ? ((Collection<?>) ret).size() : 1));
  }

  @Override
  public void logAfterUpdate(String tag, long elapsedTime, int ret) {
    debug(ParameterizedStringUtils.newString("{} Affect [{}] rows", getTagAndElapsedTime(tag, elapsedTime), ret));
  }


  @Override
  public void logAfterMultiRow(String tag, long elapsedTime, int[] result) {
    debug(ParameterizedStringUtils.newString("{} Affect [{}] objects", getTagAndElapsedTime(tag, elapsedTime),
        IntStream.of(result).sum()));
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
