package org.nkjmlab.sorm4j.core;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType.*;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Configurator;
import org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class OrmConfigStoreTest {

  private static final String NEW_CONFIG = "NEW_CONFIG";

  private static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  private static final ResultSetConverter DEFAULT_SQL_TO_JAVA_DATA_CONVERTER =
      new DefaultResultSetConverter();
  private static final SqlParameterSetter DEFAULT_JAVA_TO_SQL_DATA_CONVERTER =
      new DefaultSqlParameterSetter();
  private static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  @Test
  void testOrmConfigFail() {

    try {
      SormFactory.registerNewConfig(NEW_CONFIG, b -> b.setMultiRowProcessorType(null)
          .setBatchSize(10).setMultiRowSize(20).setBatchSizeWithMultiRow(30));
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NullPointerException.class);
    }

  }

  @Test
  void testOrmConfigStore() {

    SormFactory.registerNewConfig(NEW_CONFIG,
        b -> b.setColumnFieldMapper(DEFAULT_COLUMN_FIELD_MAPPER)
            .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER)
            .setResultSetConverter(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER)
            .setSqlParameterSetter(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER)
            .setMultiRowProcessorType(DEFAULT_MULTI_ROW_PROCESSOR).setBatchSize(10)
            .setMultiRowSize(20).setBatchSizeWithMultiRow(30));


    ConfigStore confs = SormTestUtils.createSorm(NEW_CONFIG).getConfigStore();
    assertThat(confs.getConfigName()).isEqualTo(NEW_CONFIG);
    assertThat(confs.getColumnFieldMapper()).isEqualTo(DEFAULT_COLUMN_FIELD_MAPPER);
    assertThat(confs.getTableNameMapper()).isEqualTo(DEFAULT_TABLE_NAME_MAPPER);
    assertThat(confs.getSqlToJavaDataConverter()).isEqualTo(DEFAULT_SQL_TO_JAVA_DATA_CONVERTER);
    assertThat(confs.getSqlParameterSetter()).isEqualTo(DEFAULT_JAVA_TO_SQL_DATA_CONVERTER);
  }

  @Test
  void testConfigUpdate() {
    SormFactory.updateDefaultConfig(builder -> builder
        .setMultiRowProcessorType(Configurator.MultiRowProcessorType.MULTI_ROW_AND_BATCH));
    Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    String s = sorm.apply(Player.class, conn -> ((TypedOrmConnectionImpl<Player>) conn)
        .getTableMapping(Player.class).getFormattedString());

    assertThat(s.toString()).contains(BatchOfMultiRowInOneStatementProcessor.class.getSimpleName());

    assertThat(s.toString()).contains(BatchOfMultiRowInOneStatementProcessor.class.getSimpleName());
    SormFactory.resetDefaultConfig();
    assertThat(s.toString()).contains(MultiRowInOneStatementProcessor.class.getSimpleName());
  }

  @Test
  void testConfig() {
    Sorm sormImpl = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTableAll(sormImpl);

    sormImpl.accept(Guest.class, con -> con.insert(new Guest[0]));


    sormImpl = SormFactory.create(jdbcUrl, user, password);
    assertThat(sormImpl.getConfigStore().getConfigName())
        .isEqualTo(ConfigStore.DEFAULT_CONFIG_NAME);
    SormFactory.resetDefaultConfig();
  }

}
