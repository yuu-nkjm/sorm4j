package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.impl.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.extension.impl.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.Category;

class SormBuilderTest {

  @Test
  void testBuild() {

    Sorm.builder(SormTestUtils.createDataSourceH2())
        .setColumnFieldMapper(new DefaultColumnToFieldAccessorMapper())
        .setTableNameMapper(new DefaultTableNameMapper())
        .setColumnValueToJavaObjectConverter(new DefaultColumnValueToJavaObjectConverters())
        .setSqlParametersSetter(new DefaultSqlParametersSetter())
        .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder().setBatchSize(10)
            .setBatchSizeWithMultiRow(20).build())
        .setOption("mode", "h2").setLoggerOn(Category.MAPPING).setLoggerOff(Category.MAPPING)
        .build();
  }

}
