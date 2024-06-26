package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableSupplier;

class TryTest {

  @Test
  void testGetOrDefault() {
    String s =
        Try.getOrElse(
            () -> {
              throw new RuntimeException("error");
            },
            "test");
    assertThat(s).isEqualTo("test");
  }

  @Test
  void testCreateRunnable() {
    try {
      Try.createRunnable(
              () -> {
                throw new RuntimeException("try");
              },
              e -> {})
          .run();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateSupplier() {
    try {
      Try.createSupplier(
              () -> {
                throw new RuntimeException("try");
              },
              e -> "")
          .get();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateSupplierWithThrow() {
    try {
      Try.createSupplierWithThrow(
              () -> {
                throw new RuntimeException("try");
              },
              Try::rethrow)
          .get();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateConsumer() {
    try {
      Try.createConsumer(
              con -> {
                throw new RuntimeException("try");
              },
              e -> {})
          .accept("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateConsumerWithThrow() {
    try {
      Try.createConsumerWithThrow(
              con -> {
                throw new RuntimeException("try");
              },
              Try::rethrow)
          .accept("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.createConsumerWithThrow(con -> {}, Try::rethrow).accept("a");
  }

  @Test
  void testCreateFunction() {
    try {
      Try.createFunction(
              con -> {
                throw new RuntimeException("try");
              },
              e -> "")
          .apply("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateFunctionWithThrow() {
    try {
      Try.createFunctionWithThrow(
              con -> {
                throw new RuntimeException("try");
              },
              Try::rethrow)
          .apply("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testGetOrNull() {
    Try.getOrElseNull(
        () -> {
          throw new RuntimeException("try");
        });
  }

  @Test
  void testGetOrThrow() {
    try {
      Try.getOrElseThrow(
          () -> {
            throw new RuntimeException("try");
          },
          Try::rethrow);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.getOrElseThrow(
        () -> {
          return null;
        },
        Try::rethrow);
  }

  @Test
  void testRunOrThrow() {
    try {
      Try.runOrElseThrow(
          () -> {
            throw new RuntimeException("try");
          },
          Try::rethrow);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.runOrElseThrow(() -> {}, Try::rethrow);
  }

  @Test
  void testCreateBiConsumer() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createBiConsumer(null, e -> Try.rethrow(e)).accept(null, null));

    AtomicInteger i = new AtomicInteger(0);
    BiConsumer<Integer, Integer> func =
        Try.createBiConsumer(
            (Integer a, Integer b) -> i.addAndGet(a + b), e -> System.err.println(e));

    func.accept(1, 2);
    assertThat(i.get()).isEqualTo(3);
  }

  @Test
  void testCreateBiConsumerWithThrow() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createBiConsumerWithThrow(null, e -> Try.rethrow(e)).accept(null, null));

    AtomicInteger i = new AtomicInteger(0);
    BiConsumer<Integer, Integer> func =
        Try.createBiConsumerWithThrow(
            (Integer a, Integer b) -> i.addAndGet(a + b), e -> Try.rethrow(e));

    func.accept(1, 2);
    assertThat(i.get()).isEqualTo(3);

    assertThrowsExactly(
        IllegalAccessError.class,
        () ->
            Try.createBiConsumerWithThrow(
                    (Integer a, Integer b) -> {
                      throw new IllegalAccessError();
                    },
                    e -> Try.rethrow(e))
                .accept(1, 2));
  }

  @Test
  void testCreateConsumer1() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createConsumer(null, e -> Try.rethrow(e)).accept(null));

    AtomicInteger i = new AtomicInteger(0);
    Consumer<Integer> func =
        Try.createConsumer((Integer a) -> i.addAndGet(a), e -> System.err.println(e));

    func.accept(2);
    assertThat(i.get()).isEqualTo(2);
  }

  @Test
  void testCreateConsumerWithThrow1() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createConsumerWithThrow(null, e -> Try.rethrow(e)).accept(null));

    AtomicInteger i = new AtomicInteger(0);
    Consumer<Integer> func =
        Try.createConsumerWithThrow(
            (Integer a) -> {
              i.addAndGet(a);
            },
            e -> Try.rethrow(e));

    func.accept(2);
    assertThat(i.get()).isEqualTo(2);

    assertThrowsExactly(
        IllegalAccessError.class,
        () ->
            Try.createConsumerWithThrow(
                    (Integer a) -> {
                      throw new IllegalAccessError();
                    },
                    e -> Try.rethrow(e))
                .accept(2));
  }

  @Test
  void testCreateFunction1() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createFunction(null, e -> Try.rethrow(e)).apply(null));

    AtomicInteger i = new AtomicInteger(0);
    Function<Integer, Integer> func = Try.createFunction((Integer a) -> i.addAndGet(a), e -> -1);

    func.apply(2);
    assertThat(i.get()).isEqualTo(2);
  }

  @Test
  void testCreateFunctionWithThrow1() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createFunctionWithThrow(null, e -> Try.rethrow(e)).apply(null));

    AtomicInteger i = new AtomicInteger(0);
    Function<Integer, Integer> func =
        Try.createFunctionWithThrow((Integer a) -> i.addAndGet(a), e -> Try.rethrow(e));

    func.apply(2);

    assertThat(i.get()).isEqualTo(2);

    assertThrowsExactly(
        IllegalAccessError.class,
        () ->
            Try.createConsumerWithThrow(
                    (Integer a) -> {
                      throw new IllegalAccessError();
                    },
                    e -> Try.rethrow(e))
                .accept(2));
  }

  @Test
  void testCreateRunnable1() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.createRunnable(null, e -> Try.rethrow(e)).run());

    AtomicInteger i = new AtomicInteger(0);
    Runnable func = Try.createRunnable(() -> i.incrementAndGet(), e -> System.err.println(e));

    func.run();
    assertThat(i.get()).isEqualTo(1);
  }

  @Test
  void testCreateRunnableWithThrow() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createRunnableWithThrow(null, e -> Try.rethrow(e)).run());

    AtomicInteger i = new AtomicInteger(0);
    Runnable func = Try.createRunnableWithThrow(() -> i.incrementAndGet(), e -> Try.rethrow(e));

    func.run();
    assertThat(i.get()).isEqualTo(1);

    assertThrowsExactly(
        IllegalAccessError.class,
        () ->
            Try.createRunnableWithThrow(
                    () -> {
                      throw new IllegalAccessError();
                    },
                    e -> Try.rethrow(e))
                .run());
  }

  @Test
  void testCreateSupplier1() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.createSupplier(null, e -> Try.rethrow(e)).get());

    AtomicInteger i = new AtomicInteger(0);
    Supplier<Integer> func = Try.createSupplier(() -> i.incrementAndGet(), e -> -1);

    assertThat(func.get()).isEqualTo(1);
  }

  @Test
  void testCreateSupplierWithThrow1() {
    assertThrowsExactly(
        NullPointerException.class,
        () -> Try.createRunnableWithThrow(null, e -> Try.rethrow(e)).run());

    AtomicInteger i = new AtomicInteger(0);
    Supplier<Integer> func =
        Try.createSupplierWithThrow(() -> i.incrementAndGet(), e -> Try.rethrow(e));

    assertThat(func.get()).isEqualTo(1);

    assertThrowsExactly(
        IllegalAccessError.class,
        () ->
            Try.createSupplierWithThrow(
                    () -> {
                      throw new IllegalAccessError();
                    },
                    e -> Try.rethrow(e))
                .get());
  }

  @Test
  void testGetOrElse() {
    assertThat(Try.getOrElse(null, -1)).isEqualTo(-1);
    assertThat(Try.getOrElse(() -> 2, -1)).isEqualTo(2);
  }

  @Test
  void testGetOrElseNull() {
    assertThat(Try.getOrElseNull((ThrowableSupplier<Integer>) null)).isEqualTo(null);
    assertThat(Try.getOrElseNull(() -> 2)).isEqualTo(2);
  }

  @Test
  void testGetOrElseThrow() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.getOrElseThrow(null, e -> Try.rethrow(e)));
    assertThat(Try.getOrElseThrow(() -> 2, e -> Try.rethrow(e))).isEqualTo(2);
  }

  @Test
  void testGetOrElseGet() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.getOrElseGet(null, e -> Try.rethrow(e)));
    assertThat(Try.getOrElseGet(() -> 2, e -> 3)).isEqualTo(2);
  }

  @Test
  void testRunOrElseDo() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.runOrElseDo(null, e -> Try.rethrow(e)));
    Try.runOrElseDo(() -> {}, e -> Try.rethrow(e));
  }

  @Test
  void testRunOrElseThrow() {
    assertThrowsExactly(
        NullPointerException.class, () -> Try.runOrElseThrow(null, e -> Try.rethrow(e)));
    Try.runOrElseThrow(() -> {}, e -> Try.rethrow(e));
  }

  @Test
  void test1CreateBiConsumer() {

    Try.createBiConsumer((t, s) -> {}, e -> {}).accept("test", "test");

    assertDoesNotThrow(
        () ->
            Try.createBiConsumer(
                    (t, s) -> {
                      throw new RuntimeException();
                    },
                    e -> {})
                .accept("test", "test"));
  }

  @Test
  void test1CreateBiConsumerWithThrow() {

    Try.createBiConsumerWithThrow((t, s) -> {}, e -> new RuntimeException()).accept("test", "test");

    assertThrows(
        RuntimeException.class,
        () ->
            Try.createBiConsumerWithThrow(
                    (t, s) -> {
                      throw new RuntimeException();
                    },
                    e -> new RuntimeException())
                .accept("test", "test"));
  }

  @Test
  void test1CreateConsumer() {

    Try.createConsumer(t -> {}, e -> {}).accept("test");

    assertDoesNotThrow(
        () ->
            Try.createConsumer(
                    t -> {
                      throw new RuntimeException();
                    },
                    e -> {})
                .accept("test"));
  }

  @Test
  void test1CreateConsumerWithThrow() {

    Try.createConsumerWithThrow(t -> {}, e -> new RuntimeException()).accept("test");

    assertThrows(
        RuntimeException.class,
        () ->
            Try.createConsumerWithThrow(
                    t -> {
                      throw new RuntimeException();
                    },
                    e -> new RuntimeException())
                .accept("test"));
  }

  @Test
  void test1CreateFunction() {

    assertEquals("test", Try.createFunction(t -> "test", e -> "error").apply("test"));

    assertEquals(
        "error",
        Try.createFunction(
                t -> {
                  throw new RuntimeException();
                },
                e -> "error")
            .apply("test"));
  }

  @Test
  void test1CreateFunctionWithThrow() {

    assertEquals(
        "test",
        Try.createFunctionWithThrow(t -> "test", e -> new RuntimeException()).apply("test"));

    assertThrows(
        RuntimeException.class,
        () ->
            Try.createFunctionWithThrow(
                    t -> {
                      throw new RuntimeException();
                    },
                    e -> new RuntimeException())
                .apply("test"));
  }

  @Test
  void test1CreateRunnable() {

    Try.createRunnable(() -> {}, e -> {}).run();

    assertDoesNotThrow(
        () ->
            Try.createRunnable(
                    () -> {
                      throw new RuntimeException();
                    },
                    e -> {})
                .run());
  }

  @Test
  void test1CreateRunnableWithThrow() {

    Try.createRunnableWithThrow(() -> {}, e -> new RuntimeException()).run();

    assertThrows(
        RuntimeException.class,
        () ->
            Try.createRunnableWithThrow(
                    () -> {
                      throw new RuntimeException();
                    },
                    e -> new RuntimeException())
                .run());
  }

  @Test
  void test1CreateSupplier() {

    assertEquals("test", Try.createSupplier(() -> "test", e -> "error").get());

    assertEquals(
        "error",
        Try.createSupplier(
                () -> {
                  throw new RuntimeException();
                },
                e -> "error")
            .get());
  }

  @Test
  void test1CreateSupplierWithThrow() {

    assertEquals(
        "test", Try.createSupplierWithThrow(() -> "test", e -> new RuntimeException()).get());

    assertThrows(
        RuntimeException.class,
        () ->
            Try.createSupplierWithThrow(
                    () -> {
                      throw new RuntimeException();
                    },
                    e -> new RuntimeException())
                .get());
  }

  @Test
  void test1OrElse() {

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
  void test1OrElseGet() {

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
  void test1OrElseThrow() {

    assertEquals("test", Try.getOrElseThrow(() -> "test", e -> new RuntimeException()));

    assertThrows(
        RuntimeException.class,
        () ->
            Try.getOrElseThrow(
                () -> {
                  throw new RuntimeException();
                },
                e -> new RuntimeException()));
  }

  @Test
  void test1OrElseNull() {

    assertEquals("test", Try.getOrElseNull(() -> "test"));

    assertNull(
        Try.getOrElseNull(
            () -> {
              throw new RuntimeException();
            }));
  }

  @Test
  void test1Rethrow() {

    RuntimeException exception = new RuntimeException("test");
    assertThrows(RuntimeException.class, () -> Try.rethrow(exception));
  }

  @Test
  void test1RunOrElseDo() {
    Try.runOrElseDo(() -> {}, e -> {});

    assertDoesNotThrow(
        () ->
            Try.runOrElseDo(
                () -> {
                  throw new RuntimeException();
                },
                e -> {}));
  }

  @Test
  void test1RunOrElseThrow() {

    assertDoesNotThrow(() -> Try.runOrElseThrow(() -> {}, e -> new RuntimeException()));

    assertThrows(
        RuntimeException.class,
        () ->
            Try.runOrElseThrow(
                () -> {
                  throw new RuntimeException();
                },
                e -> new RuntimeException()));
  }

  @Test
  void test1RunOrElseRethrow() {

    assertThrows(
        RuntimeException.class,
        () ->
            Try.runOrElseRethrow(
                () -> {
                  throw new RuntimeException();
                }));
  }
}
