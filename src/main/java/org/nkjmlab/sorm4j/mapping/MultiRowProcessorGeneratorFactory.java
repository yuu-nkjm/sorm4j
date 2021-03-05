package org.nkjmlab.sorm4j.mapping;

import java.util.function.Function;

final class MultiRowProcessorGeneratorFactory {

  private final Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory;

  MultiRowProcessorGeneratorFactory(
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    this.multiRowProcessorFactory = multiRowProcessorFactory;
  }

  public Function<TableMapping<?>, MultiRowProcessor<?>> getMultiRowProcessorFunction() {
    return multiRowProcessorFactory;
  }


  public static MultiRowProcessorGeneratorFactory of(
      Function<TableMapping<?>, MultiRowProcessor<?>> multiRowProcessorFactory) {
    return new MultiRowProcessorGeneratorFactory(multiRowProcessorFactory);
  }

}
