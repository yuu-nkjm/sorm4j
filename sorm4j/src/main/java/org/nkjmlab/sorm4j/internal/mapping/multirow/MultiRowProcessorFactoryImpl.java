package org.nkjmlab.sorm4j.internal.mapping.multirow;

import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.ContainerToTableMapper;

public class MultiRowProcessorFactoryImpl implements MultiRowProcessorFactory {
  private final MultiRowProcessorFactory.ProcessorType multiRowProcessorType;
  private final int batchSize;
  private final int batchSizeWithMultiRow;
  private final int multiRowSize;

  public MultiRowProcessorFactoryImpl(
      MultiRowProcessorFactory.ProcessorType multiRowProcessorType,
      int batchSize,
      int multiRowSize,
      int batchSizeWithMultiRow) {
    this.multiRowProcessorType = multiRowProcessorType;
    this.batchSize = batchSize;
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  @Override
  public <T> MultiRowProcessor<T> createMultiRowProcessor(
      LogContext loggerContext,
      SqlParametersSetter sqlParametersSetter,
      PreparedStatementSupplier statementSupplier,
      Class<T> objectClass,
      ContainerToTableMapper<T> tableMapping) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new SimpleBatchProcessor<>(
            loggerContext, sqlParametersSetter, statementSupplier, tableMapping, batchSize);
      case MULTI_ROW:
        return new MultiRowInOneStatementProcessor<>(
            loggerContext,
            sqlParametersSetter,
            statementSupplier,
            tableMapping,
            batchSize,
            multiRowSize);
      case MULTI_ROW_AND_BATCH:
        return new BatchOfMultiRowInOneStatementProcessor<>(
            loggerContext,
            sqlParametersSetter,
            statementSupplier,
            tableMapping,
            batchSize,
            multiRowSize,
            batchSizeWithMultiRow);
      default:
        throw new IllegalStateException(multiRowProcessorType + " is invalid");
    }
  }

  @Override
  public String toString() {
    return "MultiRowProcessorFactory [multiRowProcessorType="
        + multiRowProcessorType
        + ", batchSize="
        + batchSize
        + ", batchSizeWithMultiRow="
        + batchSizeWithMultiRow
        + ", multiRowSize="
        + multiRowSize
        + "]";
  }
}
