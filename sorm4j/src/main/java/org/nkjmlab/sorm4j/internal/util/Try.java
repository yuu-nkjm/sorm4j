package org.nkjmlab.sorm4j.internal.util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.nkjmlab.sorm4j.internal.util.function.ThrowableRunnable;
import org.nkjmlab.sorm4j.internal.util.function.ThrowableSupplier;

/** Utility class for handling exceptions in functional operations. */
public final class Try {

  /**
   * Executes the given supplier and returns its result. If an exception occurs, returns the
   * provided alternative value.
   *
   * @param <T> the type of the result
   * @param onTry the supplier that may throw an exception
   * @param other the value to return if an exception occurs
   * @return the result of {@code onTry}, or {@code other} if an exception occurs
   */
  public static <T> T getOrElse(ThrowableSupplier<T> onTry, T other) {
    return ThrowableSupplier.toSupplier(onTry, e -> other).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, returns {@code
   * null}.
   *
   * @param <T> the type of the result
   * @param onTry the supplier that may throw an exception
   * @return the result of {@code onTry}, or {@code null} if an exception occurs
   */
  public static <T> T getOrElseNull(ThrowableSupplier<T> onTry) {
    return ThrowableSupplier.toSupplier(onTry, e -> null).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, applies the given
   * function to generate an alternative value.
   *
   * @param <T> the type of the result
   * @param onTry the supplier that may throw an exception
   * @param otherSupplier the function that provides an alternative value in case of an exception
   * @return the result of {@code onTry}, or the result of {@code otherSupplier.apply(e)} if an
   *     exception occurs
   */
  public static <T> T getOrElseGet(
      ThrowableSupplier<T> onTry, Function<Exception, T> otherSupplier) {
    return ThrowableSupplier.toSupplier(onTry, e -> otherSupplier.apply(e)).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, throws an exception
   * generated by the provided function.
   *
   * @param <T> the type of the result
   * @param <X> the type of the exception to be thrown
   * @param onTry the supplier that may throw an exception
   * @param exceptionSupplier the function that generates the exception to be thrown
   * @return the result of {@code onTry}
   * @throws X if an exception occurs during execution
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

  /**
   * Executes the given supplier and returns its result. If an exception occurs, rethrows it as an
   * unchecked exception.
   *
   * @param <T> the type of the result
   * @param onTry the supplier that may throw an exception
   * @return the result of {@code onTry}
   */
  public static <T> T getOrElseRethrow(ThrowableSupplier<T> onTry) {
    return getOrElseThrow(onTry, e -> Try.rethrow(e));
  }

  /**
   * Executes the given runnable. If an exception occurs, applies the given handler function.
   *
   * @param onTry the runnable that may throw an exception
   * @param exceptionHandler the consumer that handles the exception
   */
  public static void runOrElseDo(ThrowableRunnable onTry, Consumer<Exception> exceptionHandler) {
    ThrowableRunnable.toRunnable(onTry, exceptionHandler).run();
  }

  /**
   * Executes the given runnable. If an exception occurs, throws an exception generated by the
   * provided function.
   *
   * @param <X> the type of the exception to be thrown
   * @param onTry the runnable that may throw an exception
   * @param exceptionSupplier the function that generates the exception to be thrown
   * @throws X if an exception occurs during execution
   */
  public static <T, X extends RuntimeException> void runOrElseThrow(
      ThrowableRunnable onTry, Function<Exception, ? extends X> exceptionSupplier) throws X {
    ThrowableRunnable.toRunnable(
            onTry,
            e -> {
              throw exceptionSupplier.apply(e);
            })
        .run();
  }

  /**
   * Executes the given runnable. If an exception occurs, rethrows it as an unchecked exception.
   *
   * @param onTry the runnable that may throw an exception
   */
  public static <T> void runOrElseRethrow(ThrowableRunnable onTry) {
    runOrElseThrow(onTry, e -> Try.rethrow(e));
  }

  /**
   * Rethrows the given throwable as a runtime exception.
   *
   * @param <T> the type of the throwable
   * @param throwable the throwable to be rethrown
   * @return never returns (this method always throws an exception)
   * @throws T always throws the given throwable
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
    throw (T) throwable;
  }

  /** Private constructor to prevent instantiation. */
  private Try() {}
}
