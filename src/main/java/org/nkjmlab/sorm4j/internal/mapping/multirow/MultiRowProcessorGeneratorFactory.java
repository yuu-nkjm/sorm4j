package org.nkjmlab.sorm4j.internal.mapping.multirow;

import java.util.function.Function;
import org.nkjmlab.sorm4j.Configurator.MultiRowProcessorType;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.mapping.TableMapping;

public final class MultiRowProcessorGeneratorFactory {

  private final Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory;

  private MultiRowProcessorGeneratorFactory(
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    this.multiRowProcessorFactory = multiRowProcessorFactory;
  }

  public Function<TableMapping<?>, MultiRowProcessor<?>> getMultiRowProcessorFunction() {
    return multiRowProcessorFactory;
  }

  public static MultiRowProcessorGeneratorFactory createMultiRowProcessorFactory(
      SqlParameterSetter sqlParameterSetter, MultiRowProcessorType multiRowProcessorType,
      int batchSize, int multiRowSize, int batchSizeWithMultiRow) {
    switch (multiRowProcessorType) {
      case SIMPLE_BATCH:
        return new MultiRowProcessorGeneratorFactory(
            t -> new SimpleBatchProcessor<>(sqlParameterSetter, t, batchSize));
      case MULTI_ROW:
        return new MultiRowProcessorGeneratorFactory(t -> new MultiRowInOneStatementProcessor<>(
            sqlParameterSetter, t, batchSize, multiRowSize));
      case MULTI_ROW_AND_BATCH:
        return new MultiRowProcessorGeneratorFactory(
            t -> new BatchOfMultiRowInOneStatementProcessor<>(sqlParameterSetter, t, batchSize,
                multiRowSize, batchSizeWithMultiRow));
      default:
        return null;
    }
  }


}
