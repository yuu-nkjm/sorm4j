package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.util.function.Function;
import org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
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

  public static MultiRowProcessorFactory createMultiRowProcessorFactory(
      SqlParameterSetter sqlParameterSetter, MultiRowProcessorType multiRowProcessorType,
      int batchSize, int multiRowSize, int batchSizeWithMultiRow) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new MultiRowProcessorFactory(
            t -> new SimpleBatchProcessor<>(sqlParameterSetter, t, batchSize));
      case MULTI_ROW:
        return new MultiRowProcessorFactory(t -> new MultiRowInOneStatementProcessor<>(
            sqlParameterSetter, t, batchSize, multiRowSize));
      case MULTI_ROW_AND_BATCH:
        return new MultiRowProcessorFactory(t -> new BatchOfMultiRowInOneStatementProcessor<>(
            sqlParameterSetter, t, batchSize, multiRowSize, batchSizeWithMultiRow));
      default:
        return null;
    }
  }


}
