package org.nkjmlab.sorm4j.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.internal.context.impl.ContainerAccessor;

class FieldAccessorTest {

  @Test
  void testAccessor() {
    ContainerAccessor ac = new ContainerAccessor(null, null, null, null);
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
