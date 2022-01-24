package org.nkjmlab.sorm4j.internal.mapping.multirow;

import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public class MultiRowProcessorFactoryImpl implements MultiRowProcessorFactory {
  private final MultiRowProcessorType multiRowProcessorType;
  private final int batchSize;
  private final int batchSizeWithMultiRow;
  private final int multiRowSize;

  public MultiRowProcessorFactoryImpl(MultiRowProcessorType multiRowProcessorType, int batchSize,
      int multiRowSize, int batchSizeWithMultiRow) {
    this.multiRowProcessorType = multiRowProcessorType;
    this.batchSize = batchSize;
    this.multiRowSize = multiRowSize;
    this.batchSizeWithMultiRow = batchSizeWithMultiRow;
  }

  @Override
  public <T> MultiRowProcessor<T> getMultiRowProcessor(LoggerContext loggerContext,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier statementSupplier,
      Class<T> objectClass, SqlParametersToTableMapping<T> tableMapping) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new SimpleBatchProcessor<>(loggerContext, sqlParametersSetter, statementSupplier,
            tableMapping, batchSize);
      case MULTI_ROW:
        return new MultiRowInOneStatementProcessor<>(loggerContext, sqlParametersSetter,
            statementSupplier, tableMapping, batchSize, multiRowSize);
      case MULTI_ROW_AND_BATCH:
        return new BatchOfMultiRowInOneStatementProcessor<>(loggerContext, sqlParametersSetter,
            statementSupplier, tableMapping, batchSize, multiRowSize, batchSizeWithMultiRow);
      default:
        throw new IllegalStateException(multiRowProcessorType + " is invalid");
    }
  }

  @Override
  public String toString() {
    return "MultiRowProcessorFactory [multiRowProcessorType=" + multiRowProcessorType
        + ", batchSize=" + batchSize + ", batchSizeWithMultiRow=" + batchSizeWithMultiRow
        + ", multiRowSize=" + multiRowSize + "]";
  }

}
