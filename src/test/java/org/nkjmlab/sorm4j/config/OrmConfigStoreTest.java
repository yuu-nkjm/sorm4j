package org.nkjmlab.sorm4j.config;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OrmConfigStoreTest {

  private static final String DEFAULT_CACHE = "DEFAULT_CACHE";

  private static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameGuesser();
  private static final ResultSetValueGetter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetValueGetter();
  private static final PreparedStatementParametersSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultPreparedStatementParametersSetter();
  private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
      new MultiRowProcessorFactory();

  @Test
  void testOrmConfigStore() {

    OrmConfigStore confs = new OrmConfigStore.Builder().setCacheName(DEFAULT_CACHE)
        .setColumnFieldMapper(DEFAULT_COLUMN_FIELD_MAPPER)
        .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER)
        .setSqlToJavaDataConverter(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER)
        .setJavaToSqlDataConverter(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER)
        .setMultiRowProcessorFactory(DEFAULT_MULTI_ROW_PROCESSOR_FACTORY).build();

    assertThat(confs.getCacheName()).isEqualTo(DEFAULT_CACHE);
    assertThat(confs.getColumnFieldMapper()).isEqualTo(DEFAULT_COLUMN_FIELD_MAPPER);
    assertThat(confs.getTableNameMapper()).isEqualTo(DEFAULT_TABLE_NAME_MAPPER);
    assertThat(confs.getSqlToJavaDataConverter()).isEqualTo(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER);
    assertThat(confs.getJavaToSqlDataConverter()).isEqualTo(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER);
    assertThat(confs.getMultiProcessorFactory()).isEqualTo(DEFAULT_MULTI_ROW_PROCESSOR_FACTORY);
  }

}
