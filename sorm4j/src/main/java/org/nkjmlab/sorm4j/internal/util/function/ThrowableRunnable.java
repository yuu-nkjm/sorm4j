package org.nkjmlab.sorm4j.internal.util.function;

import java.util.function.Consumer;

/**
 * A functional interface similar to {@link Runnable}, but allows throwing checked exceptions. This
 * interface enables lambda expressions and method references to handle exceptions explicitly within
 * the execution logic.
 *
 * <p>Use {@link #toRunnable(ThrowableRunnable, Consumer)} to convert it into a standard {@link
 * Runnable} while handling exceptions gracefully.
 *
 * @author yuu_nkjm
 */
@FunctionalInterface
public interface ThrowableRunnable {

  /**
   * Executes the operation, allowing checked exceptions to be thrown.
   *
   * @throws Exception if an error occurs during execution
   */
  void run() throws Exception;

  /**
   * Converts a {@code ThrowableRunnable} into a {@link Runnable} by wrapping the execution inside a
   * try-catch block. If an exception occurs, it is passed to the given exception handler.
   *
   * <p>This method allows integrating exception-throwing runnables into standard Java functional
   * interfaces while handling errors in a controlled manner.
   *
   * @param onTry the {@code ThrowableRunnable} to be executed
   * @param exceptionHandler a {@code Consumer} to handle any thrown exceptions
   * @return a {@code Runnable} that wraps the given {@code ThrowableRunnable} with exception
   *     handling
   */
  public static Runnable toRunnable(ThrowableRunnable onTry, Consumer<Exception> exceptionHandler) {
    return () -> {
      try {
        onTry.run();
      } catch (Exception e) {
        exceptionHandler.accept(e);
      }
    };
  }
}
