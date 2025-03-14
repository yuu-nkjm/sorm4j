package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface TryBiConsumer<T, S> {
  void accept(T t, S s) throws Exception;

  /**
   * @param <T>
   * @param <S>
   * @param tryBiConsumer
   * @param exceptionConsumer
   * @return
   */
  public static <T, S> BiConsumer<T, S> toBiConsumer(
      TryBiConsumer<T, S> tryBiConsumer, Consumer<Exception> exceptionConsumer) {
    return (t, s) -> {
      try {
        tryBiConsumer.accept(t, s);
      } catch (Exception e) {
        exceptionConsumer.accept(e);
      }
    };
  }
}
