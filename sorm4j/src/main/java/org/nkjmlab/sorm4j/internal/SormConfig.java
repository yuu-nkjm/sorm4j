package org.nkjmlab.sorm4j.internal;

import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.context.TableMetaDataParser;
import org.nkjmlab.sorm4j.internal.context.TableSqlFactory;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultTableMetaDataParser;

final class SormConfig {

  private final TableNameMapper tableNameMapper;
  private final ColumnToFieldAccessorMapper columnFieldMapper;
  private final MultiRowProcessorFactory multiRowProcessorFactory;
  private final ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter;
  private final ColumnValueToMapValueConverters columnValueToMapValueConverter;
  private final SqlParametersSetter sqlParametersSetter;
  private final PreparedStatementSupplier preparedStatementSupplier;
  private final LogContext loggerContext;
  private final TableSqlFactory tableSqlFactory;
  private final TableMetaDataParser tableMetaDataReader = new DefaultTableMetaDataParser();

  SormConfig(
      LogContext loggerContext,
      ColumnToFieldAccessorMapper columnFieldMapper,
      TableNameMapper tableNameMapper,
      ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
      ColumnValueToMapValueConverters columnValueToMapValueConverter,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier preparedStatementSupplier,
      TableSqlFactory tableSqlFactory,
      MultiRowProcessorFactory multiRowProcessorFactory) {
    this.loggerContext = loggerContext;
    this.tableNameMapper = tableNameMapper;
    this.columnFieldMapper = columnFieldMapper;
    this.multiRowProcessorFactory = multiRowProcessorFactory;
    this.columnValueToJavaObjectConverter = columnValueToJavaObjectConverter;
    this.columnValueToMapValueConverter = columnValueToMapValueConverter;
    this.sqlParametersSetter = sqlParametersSetter;
    this.preparedStatementSupplier = preparedStatementSupplier;
    this.tableSqlFactory = tableSqlFactory;
  }

  TableMetaDataParser getTableMetaDataReader() {
    return this.tableMetaDataReader;
  }

  ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return columnValueToJavaObjectConverter;
  }

  ColumnValueToMapValueConverters getColumnValueToMapValueConverter() {
    return columnValueToMapValueConverter;
  }

  SqlParametersSetter getSqlParametersSetter() {
    return sqlParametersSetter;
  }

  PreparedStatementSupplier getPreparedStatementSupplier() {
    return preparedStatementSupplier;
  }

  LogContext getLoggerContext() {
    return loggerContext;
  }

  ColumnToFieldAccessorMapper getColumnToFieldAccessorMapper() {
    return columnFieldMapper;
  }

  TableNameMapper getTableNameMapper() {
    return tableNameMapper;
  }

  TableSqlFactory getTableSqlFactory() {
    return tableSqlFactory;
  }

  MultiRowProcessorFactory getMultiRowProcessorFactory() {
    return multiRowProcessorFactory;
  }

  @Override
  public String toString() {
    return "SormConfig [tableNameMapper="
        + tableNameMapper
        + ", columnFieldMapper="
        + columnFieldMapper
        + ", multiRowProcessorFactory="
        + multiRowProcessorFactory
        + ", columnValueToJavaObjectConverter="
        + columnValueToJavaObjectConverter
        + ", columnValueToMapValueConverter="
        + columnValueToMapValueConverter
        + ", sqlParametersSetter="
        + sqlParametersSetter
        + ", loggerContext="
        + loggerContext
        + ", tableSqlFactory="
        + tableSqlFactory
        + "]";
  }
}
