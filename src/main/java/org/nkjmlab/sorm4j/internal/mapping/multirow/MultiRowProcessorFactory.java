package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.util.function.Function;
import org.nkjmlab.sorm4j.extension.SormConfigBuilder.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;

public final class MultiRowProcessorFactory {

  private final Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory;

  private MultiRowProcessorFactory(
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    this.multiRowProcessorFactory = multiRowProcessorFactory;
  }

  public MultiRowProcessor<?> getMultiRowProcessor(TableMapping<?> tableMapping) {
    return multiRowProcessorFactory.apply(tableMapping);
  }

  public static MultiRowProcessorFactory createMultiRowProcessorFactory(SormOptions options,
      SqlParametersSetter sqlParametersSetter, MultiRowProcessorType multiRowProcessorType,
      int batchSize, int multiRowSize, int batchSizeWithMultiRow) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new MultiRowProcessorFactory(
            t -> new SimpleBatchProcessor<>(options, sqlParametersSetter, t, batchSize));
      case MULTI_ROW:
        return new MultiRowProcessorFactory(t -> new MultiRowInOneStatementProcessor<>(options,
            sqlParametersSetter, t, batchSize, multiRowSize));
      case MULTI_ROW_AND_BATCH:
        return new MultiRowProcessorFactory(t -> new BatchOfMultiRowInOneStatementProcessor<>(
            options, sqlParametersSetter, t, batchSize, multiRowSize, batchSizeWithMultiRow));
      default:
        return null;
    }
  }


}
