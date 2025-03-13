package org.nkjmlab.sorm4j.internal.util.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<T> {
  void accept(T t) throws Exception;

  /**
   * @param <T>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T> Consumer<T> toConsumer(
      ThrowableConsumer<T> onTry, Consumer<Exception> exceptionHandler) {
    return x -> {
      try {
        onTry.accept(x);
      } catch (Exception e) {
        exceptionHandler.accept(e);
      }
    };
  }
}
