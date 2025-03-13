package org.nkjmlab.sorm4j.internal.util.function;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowableSupplier<T> {
  T get() throws Exception;

  /**
   * @param <T>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T> Supplier<T> toSupplier(
      ThrowableSupplier<T> onTry, Function<Exception, T> exceptionHandler) {
    return () -> {
      try {
        return onTry.get();
      } catch (Exception e) {
        return exceptionHandler.apply(e);
      }
    };
  }
}
