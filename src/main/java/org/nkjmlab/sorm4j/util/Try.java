package org.nkjmlab.sorm4j.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Try {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

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
    R apply(T t) throws Exception;
  }



  public static Runnable runOr(ThrowableRunnable onTry, Consumer<Throwable> onCatch) {
    return () -> {
      try {
        onTry.run();
      } catch (Throwable e) {
        onCatch.accept(e);
      }
    };
  }


  public static <T> Supplier<T> supplyOr(ThrowableSupplier<T> onTry,
      Function<Throwable, T> onCatch) {
    return () -> {
      try {
        return onTry.get();
      } catch (Throwable e) {
        return onCatch.apply(e);
      }
    };
  }


  public static <T, R> Consumer<T> consumeOr(ThrowableConsumer<T> onTry,
      BiConsumer<Throwable, T> onCatch) {
    return x -> {
      try {
        onTry.accept(x);
      } catch (Throwable e) {
        onCatch.accept(e, x);
      }
    };
  }

  public static <T, R> Function<T, R> applyOr(ThrowableFunction<T, R> onTry,
      BiFunction<Throwable, T, R> onCatch) {
    return x -> {
      try {
        return onTry.apply(x);
      } catch (Throwable e) {
        return onCatch.apply(e, x);
      }
    };
  }



  public static <T> T getForceOrNull(ThrowableSupplier<T> onTry) {
    return supplyForce(onTry).get();
  }

  public static <T, X extends RuntimeException> T getForceOrThrow(ThrowableSupplier<T> onTry,
      Function<Throwable, ? extends X> ex) throws X {
    return supplyOr(onTry, e -> {
      throw ex.apply(e);
    }).get();
  }

  public static <T> Supplier<T> supplyForce(ThrowableSupplier<T> onTry) {
    return supplyOr(onTry, e -> {
      log.error(e.getMessage(), e);
      return null;
    });
  }



  public static <X extends RuntimeException> Runnable runOrThrow(ThrowableRunnable onTry,
      Function<Throwable, ? extends X> ex) throws X {
    return runOr(onTry, e -> {
      throw ex.apply(e);
    });
  }

  public static <T, X extends RuntimeException> Supplier<T> supplyOrThrow(
      ThrowableSupplier<T> onTry, Function<Throwable, ? extends X> ex) throws X {
    return supplyOr(onTry, e -> {
      throw ex.apply(e);
    });
  }


  public static <T, X extends RuntimeException> Consumer<T> consumeOrThrow(
      ThrowableConsumer<T> onTry, Function<Throwable, ? extends X> ex) throws X {
    return consumeOr(onTry, (e, x) -> {
      throw ex.apply(e);
    });
  }

  public static <T, R, X extends RuntimeException> Function<T, R> applyOrThrow(
      ThrowableFunction<T, R> onTry, Function<Throwable, ? extends X> ex) throws X {
    return applyOr(onTry, (e, x) -> {
      throw ex.apply(e);
    });
  }



}
