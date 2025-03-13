package org.nkjmlab.sorm4j.internal.util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.nkjmlab.sorm4j.internal.util.function.ThrowableRunnable;
import org.nkjmlab.sorm4j.internal.util.function.ThrowableSupplier;

public final class Try {

  /**
   * Tries to get a value or gets other value if an exception occurs.
   *
   * @param <T>
   * @param onTry
   * @param other
   * @return
   */
  public static <T> T getOrElse(ThrowableSupplier<T> onTry, T other) {
    return ThrowableSupplier.toSupplier(onTry, e -> other).get();
  }

  /**
   * Tries to get a value or gets null if an exception occurs.
   *
   * @param <T>
   * @param onTry
   * @return
   */
  public static <T> T getOrElseNull(ThrowableSupplier<T> onTry) {
    return ThrowableSupplier.toSupplier(onTry, e -> null).get();
  }

  /**
   * Tries to get a value or get other value if an exception occurs..
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param otherSupplier
   * @return
   */
  public static <T, X extends RuntimeException> T getOrElseGet(
      ThrowableSupplier<T> onTry, Function<Exception, T> otherSupplier) {
    return ThrowableSupplier.toSupplier(onTry, e -> otherSupplier.apply(e)).get();
  }

  /**
   * Tries to get a value or throws an exception if an exception occurs.
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param exceptionSupplier
   * @return
   * @throws X
   */
  public static <T, X extends RuntimeException> T getOrElseThrow(
      ThrowableSupplier<T> onTry, Function<Exception, ? extends X> exceptionSupplier) throws X {
    return ThrowableSupplier.toSupplier(
            onTry,
            e -> {
              throw exceptionSupplier.apply(e);
            })
        .get();
  }

  public static <T, X extends RuntimeException> T getOrElseRethrow(ThrowableSupplier<T> onTry)
      throws X {
    return getOrElseThrow(onTry, e -> Try.rethrow(e));
  }

  /**
   * Tries to run and applies the exception handler if an exception occurs.
   *
   * @param onTry
   * @param exceptionHandler
   */
  public static void runOrElseDo(ThrowableRunnable onTry, Consumer<Exception> exceptionHandler) {
    ThrowableRunnable.toRunnable(onTry, exceptionHandler).run();
  }

  /**
   * Tries to run and applies the exception thrower if an exception occurs.
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param exceptionThrower
   * @throws X
   */
  public static <T, X extends RuntimeException> void runOrElseThrow(
      ThrowableRunnable onTry, Function<Exception, ? extends X> exceptionThrower) throws X {
    ThrowableRunnable.toRunnable(
            onTry,
            e -> {
              throw exceptionThrower.apply(e);
            })
        .run();
  }

  public static <T, X extends RuntimeException> void runOrElseRethrow(ThrowableRunnable onTry)
      throws X {
    runOrElseThrow(onTry, e -> Try.rethrow(e));
  }

  /**
   * Rethrows an exception.
   *
   * @param <T>
   * @param throwable
   * @return
   * @throws T
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
    throw (T) throwable;
  }

  private Try() {}
}
