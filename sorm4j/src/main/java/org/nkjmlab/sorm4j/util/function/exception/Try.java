package org.nkjmlab.sorm4j.util.function.exception;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for handling exceptions in functional operations in a "Try-style" manner. Provides
 * static methods to handle checked exceptions in a functional way.
 */
public final class Try {

  /**
   * Executes the given supplier and returns its result. If an exception occurs, returns the
   * provided alternative value.
   *
   * @param <T> the type of the result
   * @param trySupplier the supplier that may throw an exception
   * @param other the value to return if an exception occurs
   * @return the result of {@code trySupplier}, or {@code other} if an exception occurs
   */
  public static <T> T getOrElse(TrySupplier<T> trySupplier, T other) {
    return TrySupplier.toSupplier(trySupplier, e -> other).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, applies the given
   * function to generate an alternative value.
   *
   * @param <T> the type of the result
   * @param trySupplier the supplier that may throw an exception
   * @param otherSupplier the function that provides an alternative value in case of an exception
   * @return the result of {@code trySupplier}, or the result of {@code otherSupplier.apply(e)} if
   *     an exception occurs
   */
  public static <T> T getOrElseGet(
      TrySupplier<T> trySupplier, Function<Exception, T> otherSupplier) {
    return TrySupplier.toSupplier(trySupplier, e -> otherSupplier.apply(e)).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, returns {@code
   * null}.
   *
   * @param <T> the type of the result
   * @param trySupplier the supplier that may throw an exception
   * @return the result of {@code trySupplier}, or {@code null} if an exception occurs
   */
  public static <T> T getOrElseNull(TrySupplier<T> trySupplier) {
    return TrySupplier.toSupplier(trySupplier, e -> null).get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, throws an exception
   * generated by the provided function.
   *
   * @param <T> the type of the result
   * @param <X> the type of the exception to be thrown
   * @param trySupplier the supplier that may throw an exception
   * @param exceptionFunction the function that generates the exception to be thrown
   * @return the result of {@code trySupplier}
   * @throws X if an exception occurs during execution
   */
  public static <T, X extends RuntimeException> T getOrElseThrow(
      TrySupplier<T> trySupplier, Function<Exception, ? extends X> exceptionFunction) throws X {
    return TrySupplier.toSupplier(
            trySupplier,
            e -> {
              throw exceptionFunction.apply(e);
            })
        .get();
  }

  /**
   * Executes the given supplier and returns its result. If an exception occurs, rethrows it as an
   * unchecked exception.
   *
   * @param <T> the type of the result
   * @param trySupplier the supplier that may throw an exception
   * @return the result of {@code trySupplier}
   */
  public static <T> T getOrElseThrow(TrySupplier<T> trySupplier) {
    return getOrElseThrow(trySupplier, e -> Try.rethrow(e));
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

  /**
   * Executes the given runnable. If an exception occurs, applies the given handler function.
   *
   * @param tryRunnable the runnable that may throw an exception
   * @param exceptionHandler the consumer that handles the exception
   */
  public static void runOrHandle(TryRunnable tryRunnable, Consumer<Exception> exceptionHandler) {
    TryRunnable.toRunnable(tryRunnable, exceptionHandler).run();
  }

  /**
   * Executes the given runnable. If an exception occurs, throws an exception generated by the
   * provided function.
   *
   * @param <X> the type of the exception to be thrown
   * @param tryRunnable the runnable that may throw an exception
   * @param exceptionFunction the function that generates the exception to be thrown
   * @throws X if an exception occurs during execution
   */
  public static <T, X extends RuntimeException> void runOrThrow(
      TryRunnable tryRunnable, Function<Exception, ? extends X> exceptionFunction) throws X {
    TryRunnable.toRunnable(
            tryRunnable,
            e -> {
              throw exceptionFunction.apply(e);
            })
        .run();
  }

  /**
   * Executes the given runnable. If an exception occurs, rethrows it as an unchecked exception.
   *
   * @param tryRunnable the runnable that may throw an exception
   */
  public static <T> void runOrThrow(TryRunnable tryRunnable) {
    runOrThrow(tryRunnable, e -> Try.rethrow(e));
  }

  /** Private constructor to prevent instantiation. */
  private Try() {}
}
