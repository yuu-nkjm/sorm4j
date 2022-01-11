package org.nkjmlab.sorm4j.extension.impl;

import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.multirow.BatchOfMultiRowInOneStatementProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowInOneStatementProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.SimpleBatchProcessor;

public final class MultiRowProcessorFactory {


  /**
   * Type of how to execute multi-row update SQL statements.
   */
  public enum MultiRowProcessorType {
    SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH;
  }


  private final MultiRowProcessorType multiRowProcessorType;
  private final int batchSize;
  private final int batchSizeWithMultiRow;
  private final int multiRowSize;

  public MultiRowProcessorFactory(MultiRowProcessorType multiRowProcessorType, int batchSize,
      int multiRowSize, int batchSizeWithMultiRow) {
    this.multiRowProcessorType = multiRowProcessorType;
    this.batchSize = batchSize;
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  public MultiRowProcessor<?> getMultiRowProcessor(LoggerContext loggerContext, SormOptions options,
      SqlParametersSetter sqlParametersSetter, SqlParametersToTableMapping<?> tableMapping) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new SimpleBatchProcessor<>(loggerContext, options, sqlParametersSetter, tableMapping,
            batchSize);
      case MULTI_ROW:
        return new MultiRowInOneStatementProcessor<>(loggerContext, options, sqlParametersSetter,
            tableMapping, batchSize, multiRowSize);
      case MULTI_ROW_AND_BATCH:
        return new BatchOfMultiRowInOneStatementProcessor<>(loggerContext, options,
            sqlParametersSetter, tableMapping, batchSize, multiRowSize, batchSizeWithMultiRow);
      default:
        throw new IllegalStateException(multiRowProcessorType + " is invalid");
    }
  }



  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "MultiRowProcessorFactory [multiRowProcessorType=" + multiRowProcessorType + "]";
  }

  public static class Builder {

    private MultiRowProcessorType multiRowProcessorType = MultiRowProcessorType.MULTI_ROW;
    private int batchSize = 32;
    private int multiRowSize = 32;
    private int batchSizeWithMultiRow = 5;


    public Builder setBatchSize(int size) {
      this.batchSize = size;
      return this;
    }


    public Builder setMultiRowSize(int size) {
      this.multiRowSize = size;
      return this;
    }


    public Builder setBatchSizeWithMultiRow(int size) {
      this.batchSizeWithMultiRow = size;
      return this;
    }


    public Builder setMultiRowProcessorType(MultiRowProcessorType type) {
      this.multiRowProcessorType = type;
      return this;
    }

    public MultiRowProcessorFactory build() {
      return new MultiRowProcessorFactory(multiRowProcessorType, batchSize, multiRowSize,
          batchSizeWithMultiRow);
    }

  }


}
