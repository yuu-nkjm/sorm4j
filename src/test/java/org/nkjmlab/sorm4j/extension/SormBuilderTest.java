package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;

class SormBuilderTest {

  @Test
  void testBuild() {

    Sorm.newBuilder(SormTestUtils.createDataSourceH2())
        .setColumnFieldMapper(new DefaultColumnFieldMapper())
        .setTableNameMapper(new DefaultTableNameMapper())
        .setResultSetConverter(new DefaultResultSetConverter())
        .setSqlParametersSetter(new DefaultSqlParametersSetter()).setBatchSize(10)
        .setBatchSizeWithMultiRow(20).setOption("mode", "h2").setLoggerOn(Category.MAPPING)
        .setLoggerOff(Category.MAPPING).build();
  }

}
