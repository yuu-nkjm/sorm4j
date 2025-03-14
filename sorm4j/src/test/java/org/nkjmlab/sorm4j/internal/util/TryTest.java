package org.nkjmlab.sorm4j.internal.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.function.exception.Try;
import org.nkjmlab.sorm4j.util.function.exception.TryBiConsumer;
import org.nkjmlab.sorm4j.util.function.exception.TryConsumer;
import org.nkjmlab.sorm4j.util.function.exception.TryFunction;
import org.nkjmlab.sorm4j.util.function.exception.TryRunnable;
import org.nkjmlab.sorm4j.util.function.exception.TrySupplier;

class TryTest {

  @Test
  void testGetOrElse() {
    assertEquals("test", Try.getOrElse(() -> "test", "other"));
    assertEquals(
        "other",
        Try.getOrElse(
            () -> {
              throw new RuntimeException();
            },
            "other"));
  }

  @Test
  void testGetOrElseNull() {
    assertNull(
        Try.getOrElseNull(
            () -> {
              throw new RuntimeException();
            }));
    assertEquals("test", Try.getOrElseNull(() -> "test"));
  }

  @Test
  void testGetOrElseThrow() {
    assertThrows(
        RuntimeException.class,
        () ->
            Try.getOrElseThrow(
                () -> {
                  throw new RuntimeException();
                },
                Try::rethrow));
    assertEquals("test", Try.getOrElseThrow(() -> "test", Try::rethrow));
  }

  @Test
  void testGetOrElseGet() {
    assertEquals("test", Try.getOrElseGet(() -> "test", e -> "error"));
    assertEquals(
        "error",
        Try.getOrElseGet(
            () -> {
              throw new RuntimeException();
            },
            e -> "error"));
  }

  @Test
  void testGetOrElseRethrow() {
    assertThrows(
        RuntimeException.class,
        () ->
            Try.getOrElseThrow(
                () -> {
                  throw new RuntimeException();
                }));
  }

  @Test
  void testCreateBiConsumer() {
    AtomicInteger i = new AtomicInteger(0);
    BiConsumer<Integer, Integer> func =
        TryBiConsumer.toBiConsumer((a, b) -> i.addAndGet(a + b), e -> {});
    func.accept(1, 2);
    assertEquals(3, i.get());
    {
      BiConsumer<Integer, Integer> f =
          TryBiConsumer.toBiConsumer((a, b) -> Integer.parseInt("a"), e -> {});
      assertDoesNotThrow(() -> f.accept(1, 2));
    }
  }

  @Test
  void testCreateConsumer() {
    AtomicInteger i = new AtomicInteger(0);
    Consumer<Integer> func = TryConsumer.toConsumer(a -> i.addAndGet(a), e -> {});
    func.accept(2);
    assertEquals(2, i.get());
    {
      Consumer<Integer> f = TryConsumer.toConsumer(a -> Integer.parseInt("a"), e -> {});
      assertDoesNotThrow(() -> f.accept(1));
    }
  }

  @Test
  void testCreateFunction() {
    {
      Function<Integer, Integer> func = TryFunction.toFunction(a -> a + 1, e -> -1);
      assertEquals(3, func.apply(2));
    }
    {
      Function<Integer, Integer> func =
          TryFunction.toFunction(
              a -> {
                throw new RuntimeException();
              },
              e -> -1);
      assertEquals(-1, func.apply(2));
    }
  }

  @Test
  void testCreateRunnable() {
    AtomicInteger i = new AtomicInteger(0);
    Runnable func = TryRunnable.toRunnable(() -> i.incrementAndGet(), e -> {});
    func.run();
    assertEquals(1, i.get());
  }

  @Test
  void testCreateSupplier() {
    Supplier<Integer> func = TrySupplier.toSupplier(() -> 42, e -> -1);
    assertEquals(42, func.get());
  }

  @Test
  void testRunOrElseThrow() {
    assertThrows(
        RuntimeException.class,
        () ->
            Try.runOrThrow(
                () -> {
                  throw new RuntimeException();
                },
                e -> new RuntimeException(e)));
  }

  @Test
  void testRunOrElseDo() {
    assertDoesNotThrow(() -> Try.runOrHandle(() -> {}, e -> {}));
    assertDoesNotThrow(
        () ->
            Try.runOrHandle(
                () -> {
                  throw new RuntimeException();
                },
                e -> new RuntimeException(e)));
  }

  @Test
  void testRunOrElseRethrow() {
    assertThrows(
        RuntimeException.class,
        () ->
            Try.runOrThrow(
                () -> {
                  throw new RuntimeException();
                }));
  }
}
