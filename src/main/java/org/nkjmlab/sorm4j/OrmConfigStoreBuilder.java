package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.mapping.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;


/**
 * Builder for {@link OrmConfigStore}. An instance of the class should be get from
 * {@link SormFactory}.
 *
 * @author nkjm
 *
 */
public interface OrmConfigStoreBuilder {

  OrmConfigStore build();

  OrmConfigStoreBuilder setColumnFieldMapper(ColumnFieldMapper fieldNameMapper);

  OrmConfigStoreBuilder setTableNameMapper(TableNameMapper tableNameMapper);

  OrmConfigStoreBuilder setResultSetConverter(ResultSetConverter resultSetValueGetter);

  OrmConfigStoreBuilder setSqlParameterSetter(SqlParameterSetter sqlParameterSetter);

  OrmConfigStoreBuilder setMultiRowProcessorType(MultiRowProcessorType multiRowProcessorType);

  OrmConfigStoreBuilder setBatchSize(int size);

  OrmConfigStoreBuilder setMultiRowSize(int size);

  OrmConfigStoreBuilder setBatchSizeWithMultiRow(int size);

  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH
  }

}
