package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType.*;
import java.sql.Connection;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;


/**
 * Builder for Configuration. An instance of the class supplies by {@link SormFactory}.
 *
 * @author nkjm
 *
 */
public interface Configurator {

  /**
   * Type of how to execute multi-row update SQL statements.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
  }

  /**
   * Sets batch size for processing simple batch.
   *
   * @param size
   * @return
   */
  Configurator setBatchSize(int size);

  /**
   * Set batch size for processing batch with multirow.
   *
   * @param size
   * @return
   */
  Configurator setBatchSizeWithMultiRow(int size);

  /**
   * Sets {@link ColumnFieldMapper}.
   *
   * @param columnFieldMapper
   * @return
   */
  Configurator setColumnFieldMapper(ColumnFieldMapper columnFieldMapper);

  /**
   * Sets multi-row processor type.
   *
   * @param multiRowProcessorType
   * @return
   */
  Configurator setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType);

  /**
   * Sets multi-row size for in a SQL statement.
   *
   * @param size
   * @return
   */
  Configurator setMultiRowSize(int size);


  /**
   * Sets {@link ResultSetConverter}
   *
   * @param resultSetConverter
   * @return
   */
  Configurator setResultSetConverter(ResultSetConverter resultSetConverter);

  /**
   * Sets {@link SqlParameterSetter}
   *
   * @param sqlParameterSetter
   * @return
   */
  Configurator setSqlParameterSetter(SqlParameterSetter sqlParameterSetter);

  /**
   * Sets {@link TableNameMapper}
   *
   * @param tableNameMapper
   * @return
   */
  Configurator setTableNameMapper(TableNameMapper tableNameMapper);

  /**
   * Sets transaction isolation level. For example, {@link Connection#TRANSACTION_SERIALIZABLE}
   * {@link Connection#TRANSACTION_REPEATABLE_READ}, {@link Connection#TRANSACTION_READ_COMMITTED},
   * {@link Connection#TRANSACTION_READ_UNCOMMITTED}, {@link Connection#TRANSACTION_NONE}.
   *
   * @param level
   * @return
   */
  Configurator setTransactionIsolationLevel(int level);

  public static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  public static final SqlParameterSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParameterSetter();

  public static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();

  public static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();

  public static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();

  public static final int DEFAULT_TRANSACTION_ISOLATION_LEVEL =
      Connection.TRANSACTION_READ_COMMITTED;

}
