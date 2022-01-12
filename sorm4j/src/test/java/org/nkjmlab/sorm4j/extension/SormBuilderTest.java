package org.nkjmlab.sorm4j.extension;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.mapping.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.util.logger.LoggerContext.Category;

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
        .setLoggerOn(Category.MAPPING).setLoggerOff(Category.MAPPING).build();
  }

}
