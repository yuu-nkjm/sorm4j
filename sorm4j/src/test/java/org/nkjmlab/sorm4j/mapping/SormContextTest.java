package org.nkjmlab.sorm4j.mapping;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

class SormContextTest {

  @Test
  void testBuild() {
    SormContext context =
        SormContext.builder().setColumnFieldMapper(new DefaultColumnToFieldAccessorMapper())
            .setTableNameMapper(new DefaultTableNameMapper())
            .setColumnValueToMapEntryConverter(new DefaultColumnValueToMapEntryConverter())
            .setColumnValueToJavaObjectConverter(new DefaultColumnValueToJavaObjectConverters())
            .setSqlParametersSetter(new DefaultSqlParametersSetter())
            .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder().setBatchSize(10)
                .setBatchSizeWithMultiRow(20).build())
            .setLoggerOn(LoggerContext.Category.MAPPING).setLoggerOff(LoggerContext.Category.MAPPING).build();
    Sorm.create(SormTestUtils.createDataSourceH2(), context);
  }

}
