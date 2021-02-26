package org.nkjmlab.sorm4j.config;

import java.util.function.Function;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessor;
import org.nkjmlab.sorm4j.mapping.MultiRowInOneStatementProcessor;
import org.nkjmlab.sorm4j.mapping.TableMapping;

public final class MultiRowProcessorFactory implements OrmConfig {


  private static final int DEFAULT_BATCH_SIZE = 32;
  private static final int DEFAULT_MULTI_ROW_SIZE = 32;

  private static final Function<TableMapping<?>, MultiRowProcessor<?>> DEFAULT_MULTI_ROW_PROCESSOR =
      t -> new MultiRowInOneStatementProcessor<>(t, DEFAULT_BATCH_SIZE, DEFAULT_MULTI_ROW_SIZE);


  private final Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory;

  public MultiRowProcessorFactory() {
    this(DEFAULT_MULTI_ROW_PROCESSOR);
  }

  public MultiRowProcessorFactory(
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    this.multiRowProcessorFactory = multiRowProcessorFactory;
  }

  public Function<TableMapping<?>, MultiRowProcessor<?>> getMultiRowProcessorFactory() {
    return multiRowProcessorFactory;
  }

}
