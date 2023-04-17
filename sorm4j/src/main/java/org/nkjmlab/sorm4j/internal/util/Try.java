package org.nkjmlab.sorm4j.internal.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Try {

  @FunctionalInterface
  public static interface ThrowableBiConsumer<T, S> {
    void accept(T t, S s) throws Exception;
  }

  @FunctionalInterface
  public static interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
  }

  @FunctionalInterface
  public static interface ThrowableFunction<T, R> {
    R apply(T t) throws Exception;
  }

  @FunctionalInterface
  public static interface ThrowableRunnable {
    void run() throws Exception;
  }

  @FunctionalInterface
  public static interface ThrowableSupplier<T> {
    T get() throws Exception;
  }


  /**
   *
   * @param <T>
   * @param <S>
   * @param onTry
   * @param exceptionConsumer
   * @return
   */
  public static <T, S> BiConsumer<T, S> createBiConsumer(ThrowableBiConsumer<T, S> onTry,
      Consumer<Exception> exceptionConsumer) {
    return (t, s) -> {
      try {
        onTry.accept(t, s);
      } catch (Exception e) {
        exceptionConsumer.accept(e);
      }
    };
  }

  /**
   *
   * @param <T>
   * @param <S>
   * @param <X>
   * @param onTry
   * @param exeptionThrower
   * @return
   * @throws X
   */
  public static <T, S, X extends RuntimeException> BiConsumer<T, S> createBiConsumerWithThrow(
      ThrowableBiConsumer<T, S> onTry, Function<Exception, ? extends X> exeptionThrower) throws X {
    return createBiConsumer(onTry, e -> {
      throw exeptionThrower.apply(e);
    });
  }


  /**
   *
   * @param <T>
   * @param <R>
   * @param onTry
   * @param exceptionConsumer
   * @return
   */
  public static <T, R> Consumer<T> createConsumer(ThrowableConsumer<T> onTry,
      Consumer<Exception> exceptionConsumer) {
    return x -> {
      try {
        onTry.accept(x);
      } catch (Exception e) {
        exceptionConsumer.accept(e);
      }
    };
  }


  /**
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param exeptionThrower
   * @return
   * @throws X
   */
  public static <T, X extends RuntimeException> Consumer<T> createConsumerWithThrow(
      ThrowableConsumer<T> onTry, Function<Exception, ? extends X> exeptionThrower) throws X {
    return createConsumer(onTry, e -> {
      throw exeptionThrower.apply(e);
    });
  }

  /**
   *
   * @param <T>
   * @param <R>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T, R> Function<T, R> createFunction(ThrowableFunction<T, R> onTry,
      Function<Exception, R> exceptionHandler) {
    return x -> {
      try {
        return onTry.apply(x);
      } catch (Exception e) {
        return exceptionHandler.apply(e);
      }
    };
  }



  /**
   *
   * @param <T>
   * @param <R>
   * @param <X>
   * @param onTry
   * @param exceptionThrower
   * @return
   * @throws X
   */
  public static <T, R, X extends RuntimeException> Function<T, R> createFunctionWithThrow(
      ThrowableFunction<T, R> onTry, Function<Exception, ? extends X> exceptionThrower) throws X {
    return createFunction(onTry, e -> {
      throw exceptionThrower.apply(e);
    });
  }


  /**
   *
   * @param onTry
   * @param exceptionConsumer
   * @return
   */
  public static Runnable createRunnable(ThrowableRunnable onTry,
      Consumer<Exception> exceptionConsumer) {
    return () -> {
      try {
        onTry.run();
      } catch (Exception e) {
        exceptionConsumer.accept(e);
      }
    };
  }

  /**
   *
   * @param <X>
   * @param onTry
   * @param exceptionThrower
   * @return
   * @throws X
   */
  public static <X extends RuntimeException> Runnable createRunnableWithThrow(
      ThrowableRunnable onTry, Function<Exception, ? extends X> exceptionThrower) throws X {
    return createRunnable(onTry, e -> {
      throw exceptionThrower.apply(e);
    });
  }

  /**
   *
   * @param <T>
   * @param onTry
   * @param exceptionHandler
   * @return
   */
  public static <T> Supplier<T> createSupplier(ThrowableSupplier<T> onTry,
      Function<Exception, T> exceptionHandler) {
    return () -> {
      try {
        return onTry.get();
      } catch (Exception e) {
        return exceptionHandler.apply(e);
      }
    };
  }

  /**
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param exceptionThrower
   * @return
   * @throws X
   */
  public static <T, X extends RuntimeException> Supplier<T> createSupplierWithThrow(
      ThrowableSupplier<T> onTry, Function<Exception, ? extends X> exceptionThrower) throws X {
    return createSupplier(onTry, e -> {
      throw exceptionThrower.apply(e);
    });
  }

  /**
   * Tries to get a value or gets other value if an exception occurs.
   *
   * @param <T>
   * @param onTry
   * @param other
   * @return
   */
  public static <T> T getOrElse(ThrowableSupplier<T> onTry, T other) {
    return createSupplier(onTry, e -> {
      return other;
    }).get();
  }

  /**
   * Tries to get a value or get other value if an exception occurs..
   *
   * @param <T>
   * @param <X>
   * @param onTry
   * @param ohterSupplier
   * @return
   */
  public static <T, X extends RuntimeException> T getOrElseGet(ThrowableSupplier<T> onTry,
      Function<Exception, T> ohterSupplier) {
    return createSupplier(onTry, e -> ohterSupplier.apply(e)).get();
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
  public static <T, X extends RuntimeException> T getOrElseThrow(ThrowableSupplier<T> onTry,
      Function<Exception, ? extends X> exceptionSupplier) throws X {
    return createSupplier(onTry, e -> {
      throw exceptionSupplier.apply(e);
    }).get();
  }

  /**
   * Tries to get a value or gets null if an exception occurs.
   *
   * @param <T>
   * @param onTry
   * @return
   */
  public static <T> T getOrElseNull(ThrowableSupplier<T> onTry) {
    return createSupplier(onTry, e -> {
      return null;
    }).get();
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

  /**
   * Tries to run and applies the exception handler if an exception occurs.
   *
   * @param onTry
   * @param exceptionHandler
   */
  public static void runOrElseDo(ThrowableRunnable onTry, Consumer<Exception> exceptionHandler) {
    createRunnable(onTry, exceptionHandler).run();
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
  public static <T, X extends RuntimeException> void runOrElseThrow(ThrowableRunnable onTry,
      Function<Exception, ? extends X> exceptionThrower) throws X {
    createRunnable(onTry, e -> {
      throw exceptionThrower.apply(e);
    }).run();
  }


  public static <T, X extends RuntimeException> void runOrElseRethrow(ThrowableRunnable onTry)
      throws X {
    runOrElseThrow(onTry, e -> Try.rethrow(e));
  }

  private Try() {}


}
