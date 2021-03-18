package org.nkjmlab.sorm4j;

/**
 * Interface for handling without a return value.
 *
 * This interface is only designed for {@link Sorm} interface.
 *
 * @param <T>
 */
@FunctionalInterface
public interface ConsumerHandler<T> {
  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   * @throws Exception
   */
  void accept(T t) throws Exception;
}
