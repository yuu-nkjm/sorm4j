package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TryTest {

  @Test
  void testGetOrDefault() {
    String s = Try.getOrDefault(() -> {
      throw new RuntimeException("error");
    }, "test");
    assertThat(s).isEqualTo("test");
  }

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
