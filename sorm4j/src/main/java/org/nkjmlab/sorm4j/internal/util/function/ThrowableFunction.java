package org.nkjmlab.sorm4j.internal.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T, R> {
  R apply(T t) throws Exception;

  /**
   * @param <T>
   * @param <R>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T, R> Function<T, R> toFunction(
      ThrowableFunction<T, R> onTry, Function<Exception, R> exceptionHandler) {
    return x -> {
      try {
        return onTry.apply(x);
      } catch (Exception e) {
        return exceptionHandler.apply(e);
      }
    };
  }
}
