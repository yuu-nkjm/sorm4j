package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

/**
 * A context for a {@link Sorm} instance. An instance of this class could be built by {{@link
 * Builder#build()}. The instance of this class should be thread-safe.
 *
 * @author yuu_nkjm
 */
public interface SormContext {

  ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter();

  ColumnValueToMapValueConverters getColumnValueToMapValueConverter();

  PreparedStatementSupplier getPreparedStatementSupplier();

  SqlParametersSetter getSqlParametersSetter();

  LoggerContext getLoggerContext();

  /**
   * Returns new {@link Builder} which has set values from the given {@link SormContext}
   *
   * @return
   */
  static Builder builder(SormContext context) {
    SormContextImpl ctx = (SormContextImpl) context;
    return ctx.builder();
  }

  /**
   * Returns new {@link Builder}
   *
   * @return
   */
  static Builder builder() {
    return new Builder();
  }

  static SormContext getDefaultContext() {
    return SormImpl.DEFAULT_CONTEXT;
  }

  public static class Builder {

    private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
        MultiRowProcessorFactory.builder().build();

    private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
        new DefaultSqlParametersSetter();

    private static final PreparedStatementSupplier DEFAULT_STATEMENT_SUPPLIER =
        new DefaultPreparedStatementSupplier();

    private static final ColumnValueToJavaObjectConverters DEFAULT_RESULT_SET_CONVERTERS =
        new DefaultColumnValueToJavaObjectConverters();

    public static final ColumnValueToMapValueConverters
        DEFAULT_COLUMN_VALUE_TO_MAP_VALUE_CONVERTERS = new DefaultColumnValueToMapValueConverters();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final LoggerContext DEFAULT_LOGGER_CONTEXT = LoggerContext.builder().build();

    private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_TO_FIELD_ACCESSOR_MAPPAER =
        new DefaultColumnToFieldAccessorMapper();

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ColumnValueToJavaObjectConverters columnValueToJavaObjectConverters =
        DEFAULT_RESULT_SET_CONVERTERS;
    private ColumnValueToMapValueConverters columnValueToMapValueConverter =
        DEFAULT_COLUMN_VALUE_TO_MAP_VALUE_CONVERTERS;
    private SqlParametersSetter sqlParametersSetter = DEFAULT_SQL_PARAMETER_SETTER;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnToFieldAccessorMapper columnFieldMapper =
        DEFAULT_COLUMN_TO_FIELD_ACCESSOR_MAPPAER;
    private LoggerContext loggerContext = DEFAULT_LOGGER_CONTEXT;

    private PreparedStatementSupplier statementSupplier = DEFAULT_STATEMENT_SUPPLIER;

    private Builder() {}

    public SormContext build() {
      return new SormContextImpl(
          loggerContext,
          columnFieldMapper,
          tableNameMapper,
          columnValueToJavaObjectConverters,
          columnValueToMapValueConverter,
          sqlParametersSetter,
          statementSupplier,
          tableSqlFactory,
          multiRowProcessorFactory);
    }

    public Builder setColumnToFieldAccessorMapper(ColumnToFieldAccessorMapper fieldNameMapper) {
      this.columnFieldMapper = fieldNameMapper;
      return this;
    }

    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }

    public Builder setColumnValueToJavaObjectConverters(
        ColumnValueToJavaObjectConverters converters) {
      this.columnValueToJavaObjectConverters = converters;
      return this;
    }

    public Builder setColumnValueToMapValueConverters(ColumnValueToMapValueConverters converters) {
      this.columnValueToMapValueConverter = converters;
      return this;
    }

    public Builder setSqlParametersSetter(SqlParametersSetter sqlParametersSetter) {
      this.sqlParametersSetter = sqlParametersSetter;
      return this;
    }

    public Builder setPreparedStatementSupplier(
        PreparedStatementSupplier preparedStatementSupplier) {
      this.statementSupplier = preparedStatementSupplier;
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

    public Builder setLoggerContext(LoggerContext loggerContext) {
      this.loggerContext = loggerContext;
      return this;
    }
  }
}
