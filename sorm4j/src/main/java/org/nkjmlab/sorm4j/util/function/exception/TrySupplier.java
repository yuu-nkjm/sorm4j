package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface TrySupplier<T> {
  T get() throws Exception;

  /**
   * @param <T>
   * @param trySupplier
   * @param exceptionFunction
   * @return
   */
  public static <T> Supplier<T> toSupplier(
      TrySupplier<T> trySupplier, Function<Exception, T> exceptionFunction) {
    return () -> {
      try {
        return trySupplier.get();
      } catch (Exception e) {
        return exceptionFunction.apply(e);
      }
    };
  }
}
