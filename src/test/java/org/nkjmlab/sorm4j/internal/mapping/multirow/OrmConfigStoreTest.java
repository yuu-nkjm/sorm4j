package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormConfig;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.internal.SormContext;

class OrmConfigStoreTest {

  private static final ColumnFieldMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnFieldMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  private static final ResultSetConverter DEFAULT_RESULT_SET_CONVERTER =
      new DefaultResultSetConverter();
  private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParametersSetter();
  private static final MultiRowProcessorType DEFAULT_MULTI_ROW_PROCESSOR =
      MultiRowProcessorType.MULTI_ROW;

  @Test
  void testOrmConfigFail() {

    try {
      SormContext conf = new SormContext(SormConfig.newBuilder().setMultiRowProcessorType(null)
          .setBatchSize(10).setMultiRowSize(20).setBatchSizeWithMultiRow(30).build());
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NullPointerException.class);
    }

  }

  @Test
  void testOrmConfigStore() {

    ResultSetConverter rsc = DEFAULT_RESULT_SET_CONVERTER;

    SormContext confs =
        new SormContext(SormConfig.newBuilder().setColumnFieldMapper(DEFAULT_COLUMN_FIELD_MAPPER)
            .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER).setResultSetConverter(rsc)
            .setSqlParametersSetter(DEFAULT_SQL_PARAMETER_SETTER)
            .setMultiRowProcessorType(DEFAULT_MULTI_ROW_PROCESSOR).setBatchSize(10)
            .setMultiRowSize(20).setBatchSizeWithMultiRow(30).build());
  }



}
