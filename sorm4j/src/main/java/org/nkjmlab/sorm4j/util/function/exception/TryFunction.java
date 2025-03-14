package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.Function;

@FunctionalInterface
public interface TryFunction<T, R> {
  R apply(T t) throws Exception;

  /**
   * @param <T>
   * @param <R>
   * @param tryFunction
   * @param exceptionFunction
   * @return
   */
  public static <T, R> Function<T, R> toFunction(
      TryFunction<T, R> tryFunction, Function<Exception, R> exceptionFunction) {
    return x -> {
      try {
        return tryFunction.apply(x);
      } catch (Exception e) {
        return exceptionFunction.apply(e);
      }
    };
  }
}
