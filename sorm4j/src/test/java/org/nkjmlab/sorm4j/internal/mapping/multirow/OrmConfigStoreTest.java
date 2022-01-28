package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.context.DefaultTableSqlFactory;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableNameMapper;

class OrmConfigStoreTest {

  private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnToFieldAccessorMapper();
  private static final TableNameMapper DEFAULT_TABLE_NAME_MAPPER = new DefaultTableNameMapper();
  private static final ColumnValueToJavaObjectConverters DEFAULT_RESULT_SET_CONVERTER =
      new DefaultColumnValueToJavaObjectConverters();
  private static final SqlParametersSetter DEFAULT_SQL_PARAMETER_SETTER =
      new DefaultSqlParametersSetter();

  @Test
  void testOrmConfigFail() {

    try {
      SormContext.builder().setMultiRowProcessorFactory(MultiRowProcessorFactory.builder()
          .setBatchSize(10).setMultiRowSize(20).setBatchSizeWithMultiRow(30).build());
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NullPointerException.class);
    }

  }

  @Test
  void testOrmConfigStore() {

    ColumnValueToJavaObjectConverters rsc = DEFAULT_RESULT_SET_CONVERTER;

    SormContext.builder().setColumnToFieldAccessorMapper(DEFAULT_COLUMN_FIELD_MAPPER)
        .setTableNameMapper(DEFAULT_TABLE_NAME_MAPPER).setColumnValueToJavaObjectConverter(rsc)
        .setSqlParametersSetter(DEFAULT_SQL_PARAMETER_SETTER)
        .setTableSqlFactory(new DefaultTableSqlFactory())
        .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder().setBatchSize(10)
            .setMultiRowSize(20).setBatchSizeWithMultiRow(30).build())
        .build();
  }



}
