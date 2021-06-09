package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.util.function.Function;
import org.nkjmlab.sorm4j.extension.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;

public final class MultiRowProcessorFactory {

  private final MultiRowProcessorType multiRowProcessorType;
  private final Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory;

  private MultiRowProcessorFactory(MultiRowProcessorType multiRowProcessorType,
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    this.multiRowProcessorType = multiRowProcessorType;
    this.multiRowProcessorFactory = multiRowProcessorFactory;
  }


  public MultiRowProcessor<?> getMultiRowProcessor(TableMapping<?> tableMapping) {
    return multiRowProcessorFactory.apply(tableMapping);
  }

  public static MultiRowProcessorFactory createMultiRowProcessorFactory(LoggerContext loggerContext,
      SormOptions options, SqlParametersSetter sqlParametersSetter,
      MultiRowProcessorType multiRowProcessorType, int batchSize, int multiRowSize,
      int batchSizeWithMultiRow) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new MultiRowProcessorFactory(multiRowProcessorType,
            t -> new SimpleBatchProcessor<>(loggerContext, options, sqlParametersSetter, t,
                batchSize));
      case MULTI_ROW:
        return new MultiRowProcessorFactory(multiRowProcessorType,
            t -> new MultiRowInOneStatementProcessor<>(loggerContext, options, sqlParametersSetter,
                t, batchSize, multiRowSize));
      case MULTI_ROW_AND_BATCH:
        return new MultiRowProcessorFactory(multiRowProcessorType,
            t -> new BatchOfMultiRowInOneStatementProcessor<>(loggerContext,
                options,
                sqlParametersSetter, t, batchSize, multiRowSize, batchSizeWithMultiRow));
      default:
        return null;
    }
  }


  @Override
  public String toString() {
    return "MultiRowProcessorFactory [multiRowProcessorType=" + multiRowProcessorType + "]";
  }


}
