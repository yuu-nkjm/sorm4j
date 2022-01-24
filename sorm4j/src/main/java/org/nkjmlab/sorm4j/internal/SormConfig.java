package org.nkjmlab.sorm4j.internal;

import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.context.DefaultTableMetaDataParser;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableMetaDataParser;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.context.TableSqlFactory;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

final class SormConfig {

  private final TableNameMapper tableNameMapper;
  private final ColumnToFieldAccessorMapper columnFieldMapper;
  private final MultiRowProcessorFactory multiRowProcessorFactory;
  private final ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter;
  private final ColumnValueToMapEntryConverter columnValueToMapEntryConverter;
  private final SqlParametersSetter sqlParametersSetter;
  private final PreparedStatementSupplier preparedStatementSupplier;
  private final LoggerContext loggerContext;
  private final TableSqlFactory tableSqlFactory;
  private final TableMetaDataParser tableMetaDataReader = new DefaultTableMetaDataParser();

  SormConfig(LoggerContext loggerContext, ColumnToFieldAccessorMapper columnFieldMapper,
      TableNameMapper tableNameMapper,
      ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter,
      ColumnValueToMapEntryConverter columnValueToMapEntryConverter,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier preparedStatementSupplier,
      TableSqlFactory tableSqlFactory, MultiRowProcessorFactory multiRowProcessorFactory) {
    this.loggerContext = loggerContext;
    this.tableNameMapper = tableNameMapper;
    this.columnFieldMapper = columnFieldMapper;
    this.multiRowProcessorFactory = multiRowProcessorFactory;
    this.columnValueToJavaObjectConverter = columnValueToJavaObjectConverter;
    this.columnValueToMapEntryConverter = columnValueToMapEntryConverter;
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

  ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
    return columnValueToMapEntryConverter;
  }


  SqlParametersSetter getSqlParametersSetter() {
    return sqlParametersSetter;
  }

  PreparedStatementSupplier getPreparedStatementSupplier() {
    return preparedStatementSupplier;
  }

  LoggerContext getLoggerContext() {
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
    return "SormConfig [tableNameMapper=" + tableNameMapper + ", columnFieldMapper="
        + columnFieldMapper + ", multiRowProcessorFactory=" + multiRowProcessorFactory
        + ", columnValueToJavaObjectConverter=" + columnValueToJavaObjectConverter
        + ", columnValueToMapEntryConverter=" + columnValueToMapEntryConverter
        + ", sqlParametersSetter=" + sqlParametersSetter + ", loggerContext=" + loggerContext
        + ", tableSqlFactory=" + tableSqlFactory + "]";
  }



}
