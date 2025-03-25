package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.ContainerToTableMapper;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactoryImpl;

public interface MultiRowProcessorFactory {

  <T> MultiRowProcessor<T> createMultiRowProcessor(
      LogContext loggerContext,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      Class<T> objectClass,
      ContainerToTableMapper<T> sqlParametersToTableMapping);

  /** Type of how to execute multi-row update SQL statements. */
  enum ProcessorType {
    SIMPLE_BATCH,
    MULTI_ROW,
    MULTI_ROW_AND_BATCH;
  }

  public static Builder builder() {
    return new Builder();
  }

  public class Builder {

    private MultiRowProcessorFactory.ProcessorType multiRowProcessorType =
        MultiRowProcessorFactory.ProcessorType.MULTI_ROW;
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

    public Builder setMultiRowProcessorType(MultiRowProcessorFactory.ProcessorType type) {
      this.multiRowProcessorType = type;
      return this;
    }

    public MultiRowProcessorFactory build() {
      return new MultiRowProcessorFactoryImpl(
          multiRowProcessorType, batchSize, multiRowSize, batchSizeWithMultiRow);
    }
  }
}
