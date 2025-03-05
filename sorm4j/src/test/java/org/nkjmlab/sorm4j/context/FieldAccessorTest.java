package org.nkjmlab.sorm4j.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.SormException;

class FieldAccessorTest {

  @Test
  void testAccessor() {
    FieldAccessor ac = new FieldAccessor(null, null, null, null);
    assertThatThrownBy(() -> ac.get("foo"))
        .isInstanceOfSatisfying(
            SormException.class,
            e -> assertThat(e.getMessage()).isEqualTo("No valid getter for foo"));
    assertThatThrownBy(() -> ac.set("foo", null))
        .isInstanceOfSatisfying(
            SormException.class,
            e -> assertThat(e.getMessage()).isEqualTo("No valid setter for foo"));
  }
}
