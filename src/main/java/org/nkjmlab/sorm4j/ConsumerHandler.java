package org.nkjmlab.sorm4j;

/**
 * Interface for handling without a return value.
 *
 * This interface is only designed for {@link Sorm} interface.
 *
 * @param <T>
 */
@FunctionalInterface
public
interface ConsumerHandler<T> {
  /**
   *
   * @param t
   * @throws Exception
   */
  void accept(T t) throws Exception;
}