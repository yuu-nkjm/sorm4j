package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;


/**
 * Builder for Configuration. An instance of the class should be get from {@link SormFactory}.
 *
 * @author nkjm
 *
 */
public interface Configurator {

  /**
   * Type of how to execute multi-row query.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
  }

  Configurator setBatchSize(int size);

  Configurator setBatchSizeWithMultiRow(int size);

  Configurator setColumnFieldMapper(ColumnFieldMapper fieldNameMapper);

  Configurator setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType);

  Configurator setMultiRowSize(int size);

  Configurator setResultSetConverter(ResultSetConverter resultSetValueGetter);

  Configurator setSqlParameterSetter(SqlParameterSetter sqlParameterSetter);

  Configurator setTableNameMapper(TableNameMapper tableNameMapper);

  Configurator setTransactionIsolationLevel(int level);

}
