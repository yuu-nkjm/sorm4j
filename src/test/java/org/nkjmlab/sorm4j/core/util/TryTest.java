package org.nkjmlab.sorm4j.core.util;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.core.util.Try;

class TryTest {

  @Test
  void testCreateRunnable() {
    try {
      Try.createRunnable(() -> {
        throw new RuntimeException("try");
      }, e -> {
      }).run();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }

  }

  @Test
  void testCreateSupplier() {
    try {
      Try.createSupplier(() -> {
        throw new RuntimeException("try");
      }, e -> "").get();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateSupplierWithThrow() {
    try {
      Try.createSupplierWithThrow(() -> {
        throw new RuntimeException("try");
      }, Try::rethrow).get();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }

  }

  @Test
  void testCreateConsumer() {
    try {
      Try.createConsumer(con -> {
        throw new RuntimeException("try");
      }, e -> {
      }).accept("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateBiConsumer() {
    try {
      Try.createBiConsumer((con1, con2) -> {
        throw new RuntimeException("try");
      }, e -> {
      }).accept("a", "b");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.createBiConsumer((con1, con2) -> {
    }, e -> {
    }).accept("a", "b");
  }



  @Test
  void testCreateConsumerWithThrow() {
    try {
      Try.createConsumerWithThrow(con -> {
        throw new RuntimeException("try");
      }, Try::rethrow).accept("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.createConsumerWithThrow(con -> {
    }, Try::rethrow).accept("a");
  }

  @Test
  void testCreateBiConsumerWithThrow() {
    try {
      Try.createBiConsumerWithThrow((c1, c2) -> {
        throw new RuntimeException("try");
      }, Try::rethrow).accept("a", "b");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
  }

  @Test
  void testCreateFunction() {
    try {
      Try.createFunction(con -> {
        throw new RuntimeException("try");
      }, e -> "").apply("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }

  }

  @Test
  void testCreateFunctionWithThrow() {
    try {
      Try.createFunctionWithThrow(con -> {
        throw new RuntimeException("try");
      }, Try::rethrow).apply("a");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }

  }

  @Test
  void testGetOrNull() {
    Try.getOrNull(() -> {
      throw new RuntimeException("try");
    });
  }

  @Test
  void testGetOrThrow() {
    try {
      Try.getOrThrow(() -> {
        throw new RuntimeException("try");
      }, Try::rethrow);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.getOrThrow(() -> {
      return null;
    }, Try::rethrow);
  }

  @Test
  void testRunOrThrow() {
    try {
      Try.runOrThrow(() -> {
        throw new RuntimeException("try");
      }, Try::rethrow);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("try");
    }
    Try.runOrThrow(() -> {
    }, Try::rethrow);
  }

}
