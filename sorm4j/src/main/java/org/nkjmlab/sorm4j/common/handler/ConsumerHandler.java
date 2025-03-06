package org.nkjmlab.sorm4j.common.handler;

import org.nkjmlab.sorm4j.Sorm;

/**
 * Interface for handling without a return value.
 *
 * <p>This interface is only designed for {@link Sorm} interface.
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
