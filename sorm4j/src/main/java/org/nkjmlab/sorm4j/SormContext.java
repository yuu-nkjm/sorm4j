package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.mapping.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.mapping.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.mapping.DefaultPreparedStatementSupplier;
import org.nkjmlab.sorm4j.mapping.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.mapping.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.TableNameMapper;
import org.nkjmlab.sorm4j.mapping.TableSqlFactory;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

/**
 * A context for ORM execution. Instance of this class could be built by {{@link Builder#build()}.
 *
 * @author yuu_nkjm
 *
 */
public interface SormContext {

  /**
   * Returns logger context.
   *
   * @return
   */
  LoggerContext getLoggerContext();

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
        MultiRowProcessorFactory.builder().build();

    private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
        new DefaultSqlParametersSetter();

    private static final PreparedStatementSupplier DEFAULT_STATEMENT_SUPPLIER =
        new DefaultPreparedStatementSupplier();

    private static final ColumnValueToJavaObjectConverters DEFAULT_RESULT_SET_CONVERTER =
        new DefaultColumnValueToJavaObjectConverters();

    public static final ColumnValueToMapEntryConverter DEFAULT_COLUMN_VALUE_TO_MAP_CONVERTER =
        new DefaultColumnValueToMapEntryConverter();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
        Connection.TRANSACTION_READ_COMMITTED;

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ColumnValueToJavaObjectConverters columnValueToJavaObjectConverter =
        DEFAULT_RESULT_SET_CONVERTER;
    private ColumnValueToMapEntryConverter columnValueToMapEntryConverter =
        DEFAULT_COLUMN_VALUE_TO_MAP_CONVERTER;
    private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnToFieldAccessorMapper columnFieldMapper;
    private LoggerContext.Builder loggerBuilder = LoggerContext.builder();

    private PreparedStatementSupplier preparedStatementSupplier = DEFAULT_STATEMENT_SUPPLIER;



    private Builder() {}

    public SormContext build() {
      LoggerContext loggerContext = loggerBuilder.build();
      columnFieldMapper = columnFieldMapper != null ? columnFieldMapper
          : new DefaultColumnToFieldAccessorMapper(loggerContext);
      return new SormContextImpl(loggerContext, columnFieldMapper, tableNameMapper,
          columnValueToJavaObjectConverter, columnValueToMapEntryConverter, sqlParametersSetter,
          preparedStatementSupplier, tableSqlFactory, multiRowProcessorFactory);
    }


    public Builder setColumnFieldMapper(ColumnToFieldAccessorMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }


    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }


    public Builder setColumnValueToJavaObjectConverter(
        ColumnValueToJavaObjectConverters converter) {
      this.columnValueToJavaObjectConverter = converter;
      return this;
    }

    public Builder setColumnValueToMapEntryConverter(ColumnValueToMapEntryConverter converter) {
      this.columnValueToMapEntryConverter = converter;
      return this;
    }

    public Builder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
      this.sqlParametersSetter = sqlParametersSetter;
      return this;
    }

    public Builder setpreparedStatementSupplier(
        PreparedStatementSupplier preparedStatementSupplier) {
      this.preparedStatementSupplier = preparedStatementSupplier;
      return this;
    }


    public Builder setTableSqlFactory(TableSqlFactory tableSqlFactory) {
      this.tableSqlFactory = tableSqlFactory;
      return this;
    }

    public Builder setMultiRowProcessorFactory(MultiRowProcessorFactory multiRowProcessorFactory) {
      this.multiRowProcessorFactory = multiRowProcessorFactory;
      return this;
    }

    public Builder setLoggerOnAll() {
      this.loggerBuilder.onAll();
      return this;
    }

    public Builder setLoggerOffAll() {
      this.loggerBuilder.offAll();
      return this;
    }

    public Builder setLoggerOn(LoggerContext.Category... categories) {
      this.loggerBuilder.on(categories);
      return this;
    }

    public Builder setLoggerOff(LoggerContext.Category... categories) {
      this.loggerBuilder.off(categories);
      return this;
    }

    public Builder setLoggerSupplier(Supplier<SormLogger> loggerSupplier) {
      this.loggerBuilder.setLoggerSupplier(loggerSupplier);
      return this;
    }

  }

}
