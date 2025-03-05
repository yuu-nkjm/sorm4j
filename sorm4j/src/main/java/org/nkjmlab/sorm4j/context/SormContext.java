package org.nkjmlab.sorm4j.context;

import java.util.ArrayList;
import java.util.List;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Internal;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.context.ColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.context.TableSqlFactory;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultPreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.internal.util.CanonicalStringCache;

/**
 * A context for a {@link Sorm} instance. An instance of this class could be built by {{@link
 * Builder#build()}. The instance of this class should be thread-safe.
 *
 * @author yuu_nkjm
 */
public interface SormContext {

  /**
   * Returns new {@link Builder}
   *
   * @return
   */
  static Builder builder() {
    return new Builder();
  }

  /**
   * Returns new {@link Builder} which has set values from the given {@link SormContext}
   *
   * @return
   */
  static Builder builder(SormContext context) {
    SormContextImpl ctx = (SormContextImpl) context;
    return ctx.builder();
  }

  static CanonicalStringCache getDefaultCanonicalStringCache() {
    return CanonicalStringCache.getDefault();
  }

  static SormContext getDefaultContext() {
    return SormImpl.DEFAULT_CONTEXT;
  }

  ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter();

  ColumnValueToMapValueConverters getColumnValueToMapValueConverter();

  LogContext getLogContext();

  PreparedStatementSupplier getPreparedStatementSupplier();

  SqlParametersSetter getSqlParametersSetter();

  public static class Builder {

    private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
        MultiRowProcessorFactory.builder().build();

    private static final PreparedStatementSupplier DEFAULT_STATEMENT_SUPPLIER =
        new DefaultPreparedStatementSupplier();

    public static final ColumnValueToMapValueConverters
        DEFAULT_COLUMN_VALUE_TO_MAP_VALUE_CONVERTERS = new DefaultColumnValueToMapValueConverters();

    private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

    private static final TableSqlFactory DEFAULT_TABLE_SQL_FACTORY = new DefaultTableSqlFactory();

    private static final LogContext DEFAULT_LOGGER_CONTEXT = LogContext.builder().build();

    private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_TO_FIELD_ACCESSOR_MAPPAER =
        new DefaultColumnToFieldAccessorMapper();

    private TableNameMapper tableNameMapper = DEFAULT_TABLE_NAME_MAPPER;
    private ColumnValueToMapValueConverters columnValueToMapValueConverter =
        DEFAULT_COLUMN_VALUE_TO_MAP_VALUE_CONVERTERS;
    private MultiRowProcessorFactory multiRowProcessorFactory = DEFAULT_MULTI_ROW_PROCESSOR_FACTORY;
    private TableSqlFactory tableSqlFactory = DEFAULT_TABLE_SQL_FACTORY;

    private ColumnToFieldAccessorMapper columnFieldMapper =
        DEFAULT_COLUMN_TO_FIELD_ACCESSOR_MAPPAER;
    private LogContext logContext = DEFAULT_LOGGER_CONTEXT;

    private PreparedStatementSupplier statementSupplier = DEFAULT_STATEMENT_SUPPLIER;

    private List<SqlParameterSetter> sqlParameterSettersList = new ArrayList<>();

    private List<ColumnValueToJavaObjectConverter> columnValueToJavaObjectConvertersList =
        new ArrayList<>();

    private Builder() {}

    public SormContext build() {
      return new SormContextImpl(
          logContext,
          columnFieldMapper,
          tableNameMapper,
          new DefaultColumnValueToJavaObjectConverters(
              columnValueToJavaObjectConvertersList.toArray(
                  ColumnValueToJavaObjectConverter[]::new)),
          columnValueToMapValueConverter,
          new DefaultSqlParametersSetter(
              sqlParameterSettersList.toArray(SqlParameterSetter[]::new)),
          statementSupplier,
          tableSqlFactory,
          multiRowProcessorFactory);
    }

    public Builder addColumnValueToJavaObjectConverter(ColumnValueToJavaObjectConverter converter) {
      this.columnValueToJavaObjectConvertersList.add(converter);
      return this;
    }

    public Builder setLogContext(LogContext logContext) {
      this.logContext = logContext;
      return this;
    }

    public Builder setMultiRowProcessorFactory(MultiRowProcessorFactory multiRowProcessorFactory) {
      this.multiRowProcessorFactory = multiRowProcessorFactory;
      return this;
    }

    public Builder addSqlParameterSetter(SqlParameterSetter sqlParameterSetter) {
      this.sqlParameterSettersList.add(sqlParameterSetter);
      return this;
    }

    public Builder setTableNameMapper(TableNameMapper tableNameMapper) {
      this.tableNameMapper = tableNameMapper;
      return this;
    }

    @Internal
    public Builder setTableSqlFactory(TableSqlFactory tableSqlFactory) {
      this.tableSqlFactory = tableSqlFactory;
      return this;
    }
  }
}
