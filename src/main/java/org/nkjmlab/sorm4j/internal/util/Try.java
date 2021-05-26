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

  public static <T, S> BiConsumer<T, S> createBiConsumer(ThrowableBiConsumer<T, S> onTry,
      Consumer<Exception> onCatch) {
    return (t, s) -> {
      try {
        onTry.accept(t, s);
      } catch (Exception e) {
        onCatch.accept(e);
      }
    };
  }


  public static <T, S, X extends RuntimeException> BiConsumer<T, S> createBiConsumerWithThrow(
      ThrowableBiConsumer<T, S> onTry, Function<Exception, ? extends X> ex) throws X {
    return createBiConsumer(onTry, e -> {
      throw ex.apply(e);
    });
  }


  public static <T, R> Consumer<T> createConsumer(ThrowableConsumer<T> onTry,
      Consumer<Exception> onCatch) {
    return x -> {
      try {
        onTry.accept(x);
      } catch (Exception e) {
        onCatch.accept(e);
      }
    };
  }

  public static <T, X extends RuntimeException> Consumer<T> createConsumerWithThrow(
      ThrowableConsumer<T> onTry, Function<Exception, ? extends X> ex) throws X {
    return createConsumer(onTry, e -> {
      throw ex.apply(e);
    });
  }



  public static <T, R> Function<T, R> createFunction(ThrowableFunction<T, R> onTry,
      Function<Exception, R> onCatch) {
    return x -> {
      try {
        return onTry.apply(x);
      } catch (Exception e) {
        return onCatch.apply(e);
      }
    };
  }


  public static <T, R, X extends RuntimeException> Function<T, R> createFunctionWithThrow(
      ThrowableFunction<T, R> onTry, Function<Exception, ? extends X> ex) throws X {
    return createFunction(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static Runnable createRunnable(ThrowableRunnable onTry, Consumer<Exception> onCatch) {
    return () -> {
      try {
        onTry.run();
      } catch (Exception e) {
        onCatch.accept(e);
      }
    };
  }

  public static <X extends RuntimeException> Runnable createRunnableWithThrow(
      ThrowableRunnable onTry, Function<Exception, ? extends X> ex) throws X {
    return createRunnable(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static <T> Supplier<T> createSupplier(ThrowableSupplier<T> onTry,
      Function<Exception, T> onCatch) {
    return () -> {
      try {
        return onTry.get();
      } catch (Exception e) {
        return onCatch.apply(e);
      }
    };
  }

  public static <T, X extends RuntimeException> Supplier<T> createSupplierWithThrow(
      ThrowableSupplier<T> onTry, Function<Exception, ? extends X> ex) throws X {
    return createSupplier(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static <T> T getOrDefault(ThrowableSupplier<T> onTry, T defaultValue) {
    return createSupplier(onTry, e -> {
      return defaultValue;
    }).get();
  }


  public static <T> T getOrNull(ThrowableSupplier<T> onTry) {
    return createSupplier(onTry, e -> {
      return null;
    }).get();
  }

  public static <T, X extends RuntimeException> T getOrThrow(ThrowableSupplier<T> onTry,
      Function<Exception, ? extends X> ex) throws X {
    return createSupplier(onTry, e -> {
      throw ex.apply(e);
    }).get();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
    throw (T) throwable;
  }

  public static <T, X extends RuntimeException> void runOrThrow(ThrowableRunnable onTry,
      Function<Exception, ? extends X> ex) throws X {
    createRunnable(onTry, e -> {
      throw ex.apply(e);
    }).run();
  }

  private Try() {}

  public static void runOrElse(ThrowableRunnable onTry, Consumer<Exception> onCatch) {
    createRunnable(onTry, onCatch).run();
  }

}
