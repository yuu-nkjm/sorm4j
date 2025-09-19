package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface TryBiFunction<T, U, R> {
  R apply(T t, U u) throws Exception;

  /**
   * Wraps a TryBiFunction into a standard BiFunction, handling checked exceptions.
   *
   * @param <T> first argument type
   * @param <U> second argument type
   * @param <R> return type
   * @param tryBiFunction function that may throw
   * @param exceptionFunction function to map Exception into a return value
   * @return BiFunction that delegates to tryBiFunction, falling back to exceptionFunction on error
   */
  public static <T, U, R> BiFunction<T, U, R> toBiFunction(
      TryBiFunction<T, U, R> tryBiFunction, Function<Exception, R> exceptionFunction) {
    return (t, u) -> {
      try {
        return tryBiFunction.apply(t, u);
      } catch (Exception e) {
        return exceptionFunction.apply(e);
      }
    };
  }
}
