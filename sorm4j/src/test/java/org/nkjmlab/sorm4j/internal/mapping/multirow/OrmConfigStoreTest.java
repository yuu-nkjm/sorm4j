package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultTableSqlFactory;

class OrmConfigStoreTest {

  @Test
  void testOrmConfigFail() {

    try {
      SormContext.builder()
          .setMultiRowProcessorFactory(
              MultiRowProcessorFactory.builder()
                  .setBatchSize(10)
                  .setMultiRowSize(20)
                  .setBatchSizeWithMultiRow(30)
                  .build());
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NullPointerException.class);
    }
  }

  @Test
  void testOrmConfigStore() {

    SormContext.builder()
        .setTableNameMapper(new DefaultTableNameMapper())
        .setTableSqlFactory(new DefaultTableSqlFactory())
        .setMultiRowProcessorFactory(
            MultiRowProcessorFactory.builder()
                .setBatchSize(10)
                .setMultiRowSize(20)
                .setBatchSizeWithMultiRow(30)
                .build())
        .build();
  }
}
