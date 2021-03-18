package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
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

}
