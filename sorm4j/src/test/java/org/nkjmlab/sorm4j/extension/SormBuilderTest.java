package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;

class SormBuilderTest {

  @Test
  void testBuild() {

    Sorm.builder(SormTestUtils.createDataSourceH2())
        .setColumnFieldMapper(new DefaultColumnFieldMapper())
        .setTableNameMapper(new DefaultTableNameMapper())
        .setResultSetConverter(new DefaultResultSetConverter())
        .setSqlParametersSetter(new DefaultSqlParametersSetter()).setBatchSize(10)
        .setBatchSizeWithMultiRow(20).setOption("mode", "h2").setLoggerOn(Category.MAPPING)
        .setLoggerOff(Category.MAPPING).build();
  }

}
