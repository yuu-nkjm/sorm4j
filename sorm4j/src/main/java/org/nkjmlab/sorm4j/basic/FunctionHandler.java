package org.nkjmlab.sorm4j.basic;

/**
 * Interface for handling with a return value.
 *
 * @param <T>
 */
@FunctionalInterface
public interface FunctionHandler<T, R> {

  /**
   * Performs this operation on the given argument and return a value.
   *
   * @param t the input argument
   * @return
   * @throws Exception
   */
  R apply(T t) throws Exception;
}
