package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.ConfigStoreBuilder.MultiRowProcessorType.*;
import java.sql.Connection;
import org.nkjmlab.sorm4j.mapping.ConfigStore;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;


/**
 * Builder for {@link ConfigStore}. An instance of the class should be get from {@link SormFactory}.
 *
 * @author nkjm
 *
 */
public interface ConfigStoreBuilder {

  public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
      Connection.TRANSACTION_READ_COMMITTED;

  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  public static final SqlParameterSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultSqlParameterSetter();

  public static final ResultSetConverter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();

  /**
   * Type of how to execute multi-row query.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
  }

  ConfigStore build();

  ConfigStoreBuilder setBatchSize(int size);

  ConfigStoreBuilder setBatchSizeWithMultiRow(int size);

  ConfigStoreBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper);

  ConfigStoreBuilder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType);

  ConfigStoreBuilder setMultiRowSize(int size);

  ConfigStoreBuilder setResultSetConverter(ResultSetConverter resultSetValueGetter);

  ConfigStoreBuilder setSqlParameterSetter(SqlParameterSetter sqlParameterSetter);

  ConfigStoreBuilder setTableNameMapper(TableNameMapper tableNameMapper);

  ConfigStoreBuilder setTransactionIsolationLevel(int level);

}
