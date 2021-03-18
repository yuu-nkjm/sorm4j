package org.nkjmlab.sorm4j;

/**
 * Interface for handling with a return value.
 *
 * @param <T>
 */
@FunctionalInterface
public
interface FunctionHandler<T, R> {

  /**
   *
   * @param t
   * @return
   * @throws Exception
   */
  R apply(T t) throws Exception;
}