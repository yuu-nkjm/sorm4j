package org.nkjmlab.sorm4j.internal.util.function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableBiConsumer<T, S> {
  void accept(T t, S s) throws Exception;

  /**
   * @param <T>
   * @param <S>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T, S> BiConsumer<T, S> toBiConsumer(
      ThrowableBiConsumer<T, S> onTry, Consumer<Exception> exceptionHandler) {
    return (t, s) -> {
      try {
        onTry.accept(t, s);
      } catch (Exception e) {
        exceptionHandler.accept(e);
      }
    };
  }
}
