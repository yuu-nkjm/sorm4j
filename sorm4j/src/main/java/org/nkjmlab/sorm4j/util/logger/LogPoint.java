package org.nkjmlab.sorm4j.util.logger;

import java.sql.Connection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

@Experimental
public final class LogPoint {

  private final String name;
  private final SormLogger logger;
  private long startTime;

  LogPoint(String name, SormLogger logger) {
    this.name = name;
    this.logger = logger;
  }


  private long getElapsedTime() {
    return System.nanoTime() - startTime;
  }

  public String getTag() {
    return name + ":" + (hashCode() / 10000);
  }

  public void logAfterMultiRow(int[] result) {
    logger.logAfterMultiRow(name, getElapsedTime(), result);
  }


  public void logAfterQuery(Object ret) {
    logger.logAfterQuery(getTag(), getElapsedTime(), ret);
  }


  public void logAfterUpdate(int ret) {
    logger.logAfterUpdate(name, getElapsedTime(), ret);
  }

  public void logBeforeMultiRow(Connection con, Class<?> objectClass, int length,
      String tableName) {
    logger.logBeforeMultiRow(getTag(), con, objectClass, length, tableName);
    this.startTime = System.nanoTime();
  }


  public void logBeforeSql(Connection connection, ParameterizedSql sql) {
    logger.logBeforeSql(getTag(), connection, sql);
    this.startTime = System.nanoTime();
  }


  public void logBeforeSql(Connection connection, String sql, Object... parameters) {
    logger.logBeforeSql(getTag(), connection, sql, parameters);
    this.startTime = System.nanoTime();
  }


  public void logMapping(String mappingInfo) {
    logger.logMapping(getTag(), mappingInfo);
  }


}
