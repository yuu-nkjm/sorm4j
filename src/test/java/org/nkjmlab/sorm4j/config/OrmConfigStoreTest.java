package org.nkjmlab.sorm4j.config;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.OrmTestUtils.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.mapping.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.DefaultPreparedStatementParametersSetter;
import org.nkjmlab.sorm4j.mapping.DefaultResultSetValueGetter;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.SimpleBatchProcessor;

class OrmConfigStoreTest {

  private static final String NEW_CONFIG = "NEW_CONFIG";

  private static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  private static final ResultSetValueGetter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetValueGetter();
  private static final PreparedStatementParametersSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultPreparedStatementParametersSetter();
  private static final MultiRowProcessorFactory DEFAULT_MULTI_ROW_PROCESSOR_FACTORY =
      new MultiRowProcessorFactory();

  @Test
  void testOrmConfigStore() {

    OrmConfigStore confs =
        new OrmConfigStore.Builder(NEW_CONFIG).setColumnFieldMapper(DEFAULT_COLUMN_FIELD_MAPPER)
            .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER)
            .setSqlToJavaDataConverter(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER)
            .setJavaToSqlDataConverter(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER)
            .setMultiRowProcessorFactory(DEFAULT_MULTI_ROW_PROCESSOR_FACTORY).build();

    assertThat(confs.getConfigName()).isEqualTo(NEW_CONFIG);
    assertThat(confs.getColumnFieldMapper()).isEqualTo(DEFAULT_COLUMN_FIELD_MAPPER);
    assertThat(confs.getTableNameMapper()).isEqualTo(DEFAULT_TABLE_NAME_MAPPER);
    assertThat(confs.getSqlToJavaDataConverter()).isEqualTo(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER);
    assertThat(confs.getJavaToSqlDataConverter()).isEqualTo(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER);
    assertThat(confs.getMultiProcessorFactory()).isEqualTo(DEFAULT_MULTI_ROW_PROCESSOR_FACTORY);
  }

  @Test
  void testConfig() {
    Sorm.updateDefaultConfigStore(builder -> builder
        .setMultiRowProcessorFactory(t -> new SimpleBatchProcessor<>(t, 10)).build());
    Sorm sorm = Sorm.create(ConnectionSource.of(jdbcUrl, user, password));
    assertThat(sorm.getConfigStore().getConfigName()).isEqualTo(OrmConfigStore.DEFAULT_CONFIG_NAME);
    Sorm.resetDefaultConfigStore();
  }

}
