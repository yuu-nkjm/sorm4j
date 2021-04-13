package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import static org.nkjmlab.sorm4j.extension.Configurator.MultiRowProcessorType.*;
import java.sql.ResultSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.Configurator;
import org.nkjmlab.sorm4j.extension.Configurator.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter.ColumnValueConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParameterSetter;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.internal.mapping.ConfigStore;

class OrmConfigStoreTest {

  private static final String NEW_CONFIG = "NEW_CONFIG";

  private static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  private static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();
  private static final SqlParameterSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParameterSetter();
  private static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR = MULTI_ROW;

  @Test
  void testOrmConfigFail() {

    try {
      SormFactory.registerConfig(NEW_CONFIG, b -> b.setMultiRowProcessorType(null).setBatchSize(10)
          .setMultiRowSize(20).setBatchSizeWithMultiRow(30));
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NullPointerException.class);
    }

  }

  @Test
  void testOrmConfigStore() {

    DefaultResultSetConverter rsc =
        new DefaultResultSetConverter(List.of(new ColumnValueConverter() {

          @Override
          public boolean isConvertable(SormOptions options, ResultSet resultSet, int column,
              int columnType, Class<?> toType) {
            // TODO Auto-generated method stub
            return false;
          }

          @Override
          public Object convertTo(SormOptions options, ResultSet resultSet, int column,
              int columnType, Class<?> toType) {
            // TODO Auto-generated method stub
            return null;
          }
        }));
    SormFactory.registerConfig(NEW_CONFIG,
        b -> b.setColumnFieldMapper(DEFAULT_COLUMN_FIELD_MAPPER)
            .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER).setResultSetConverter(rsc)
            .setSqlParameterSetter(DEFAULT_SQL_PARAMETER_SETTER)
            .setMultiRowProcessorType(DEFAULT_MULTI_ROW_PROCESSOR).setBatchSize(10)
            .setMultiRowSize(20).setBatchSizeWithMultiRow(30));


    ConfigStore confs = ConfigStore.get(NEW_CONFIG);
    assertThat(confs.getConfigName()).isEqualTo(NEW_CONFIG);
    assertThat(confs.getColumnFieldMapper()).isEqualTo(DEFAULT_COLUMN_FIELD_MAPPER);
    assertThat(confs.getTableNameMapper()).isEqualTo(DEFAULT_TABLE_NAME_MAPPER);
    assertThat(confs.getResultSetConverter()).isEqualTo(rsc);
    assertThat(confs.getSqlParameterSetter()).isEqualTo(DEFAULT_SQL_PARAMETER_SETTER);
  }

  @Test
  void testConfigUpdate() {
    SormFactory.updateDefaultConfig(builder -> builder
        .setMultiRowProcessorType(Configurator.MultiRowProcessorType.MULTI_ROW_AND_BATCH));
    Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    assertThat(sorm.getConfigString())
        .contains(Configurator.MultiRowProcessorType.MULTI_ROW_AND_BATCH.toString());

    SormFactory.resetDefaultConfig();
  }

  @Test
  void testConfig() {
    Sorm sormImpl = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTableAll(sormImpl);

    sormImpl.accept(Guest.class, con -> con.insert(new Guest[0]));


    sormImpl = SormFactory.create(jdbcUrl, user, password);
    assertThat(sormImpl.getConfigName()).isEqualTo(SormFactory.DEFAULT_CONFIG_NAME);
  }

}
