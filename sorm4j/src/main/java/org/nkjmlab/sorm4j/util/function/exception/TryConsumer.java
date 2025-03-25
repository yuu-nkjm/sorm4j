package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.Consumer;

@FunctionalInterface
public interface TryConsumer<T> {
  void accept(T t) throws Exception;

  /**
   * @param <T>
   * @param tryConsumer
   * @param exceptionConsumer
   * @return
   */
  public static <T> Consumer<T> toConsumer(
      TryConsumer<T> tryConsumer, Consumer<Exception> exceptionConsumer) {
    return x -> {
      try {
        tryConsumer.accept(x);
      } catch (Exception e) {
        exceptionConsumer.accept(e);
      }
    };
  }
}
