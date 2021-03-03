package org.nkjmlab.sorm4j.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Try {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private Try() {}

  @FunctionalInterface
  public static interface ThrowableRunnable {
    void run() throws Throwable;
  }

  @FunctionalInterface
  public static interface ThrowableConsumer<T> {
    void accept(T t) throws Throwable;
  }

  @FunctionalInterface
  public static interface ThrowableSupplier<T> {
    T get() throws Throwable;
  }


  @FunctionalInterface
  public static interface ThrowableFunction<T, R> {
    R apply(T t) throws Throwable;
  }

  @FunctionalInterface
  public static interface ThrowableBiConsumer<T, S> {
    void accept(T t, S s) throws Throwable;
  }



  public static Runnable createRunnable(ThrowableRunnable onTry, Consumer<Throwable> onCatch) {
    return () -> {
      try {
        onTry.run();
      } catch (Throwable e) {
        onCatch.accept(e);
      }
    };
  }


  public static <T> Supplier<T> createSupplier(ThrowableSupplier<T> onTry,
      Function<Throwable, T> onCatch) {
    return () -> {
      try {
        return onTry.get();
      } catch (Throwable e) {
        return onCatch.apply(e);
      }
    };
  }

  public static <T, X extends RuntimeException> Supplier<T> createSupplierWithThrow(
      ThrowableSupplier<T> onTry, Function<Throwable, ? extends X> ex) throws X {
    return createSupplier(onTry, e -> {
      throw ex.apply(e);
    });
  }



  public static <T, R> Consumer<T> createConsumer(ThrowableConsumer<T> onTry,
      Consumer<Throwable> onCatch) {
    return x -> {
      try {
        onTry.accept(x);
      } catch (Throwable e) {
        onCatch.accept(e);
      }
    };
  }


  public static <T, X extends RuntimeException> Consumer<T> createConsumerWithThrow(
      ThrowableConsumer<T> onTry, Function<Throwable, ? extends X> ex) throws X {
    return createConsumer(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static <T, R> Function<T, R> createFunction(ThrowableFunction<T, R> onTry,
      Function<Throwable, R> onCatch) {
    return x -> {
      try {
        return onTry.apply(x);
      } catch (Throwable e) {
        return onCatch.apply(e);
      }
    };
  }

  public static <T, S> BiConsumer<T, S> createBiConsumer(ThrowableBiConsumer<T, S> onTry,
      Consumer<Throwable> onCatch) {
    return (t, s) -> {
      try {
        onTry.accept(t, s);
      } catch (Throwable e) {
        onCatch.accept(e);
      }
    };
  }

  public static <T, R, X extends RuntimeException> Function<T, R> createFunctionWithThrow(
      ThrowableFunction<T, R> onTry, Function<Throwable, ? extends X> ex) throws X {
    return createFunction(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static <T, S, X extends RuntimeException> BiConsumer<T, S> createBiConsumerWithThrow(
      ThrowableBiConsumer<T, S> onTry, Function<Throwable, ? extends X> ex) throws X {
    return createBiConsumer(onTry, e -> {
      throw ex.apply(e);
    });
  }


  public static <T> T getOrNull(ThrowableSupplier<T> onTry) {
    return createSupplier(onTry, e -> {
      return null;
    }).get();
  }

  public static <T, X extends RuntimeException> T getOrThrow(ThrowableSupplier<T> onTry,
      Function<Throwable, ? extends X> ex) throws X {
    return createSupplier(onTry, e -> {
      throw ex.apply(e);
    }).get();
  }

  public static <T, X extends RuntimeException> void runOrThrow(ThrowableRunnable onTry,
      Function<Throwable, ? extends X> ex) throws X {
    createRunnable(onTry, e -> {
      throw ex.apply(e);
    }).run();;
  }



}
