package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nkjmlab.sorm4j.internal.util.ClassUtils.primitiveToWrapper;

import org.junit.jupiter.api.Test;

class ClassUtilsTest {

  @Test
  void shouldConvertPrimitiveToWrapper() {
    assertThat(primitiveToWrapper(boolean.class)).isEqualTo(Boolean.class);
    assertThat(primitiveToWrapper(byte.class)).isEqualTo(Byte.class);
    assertThat(primitiveToWrapper(char.class)).isEqualTo(Character.class);
    assertThat(primitiveToWrapper(short.class)).isEqualTo(Short.class);
    assertThat(primitiveToWrapper(int.class)).isEqualTo(Integer.class);
    assertThat(primitiveToWrapper(long.class)).isEqualTo(Long.class);
    assertThat(primitiveToWrapper(double.class)).isEqualTo(Double.class);
    assertThat(primitiveToWrapper(float.class)).isEqualTo(Float.class);
  }

  @Test
  void shouldReturnNullForNonPrimitiveType() {
    assertThat(primitiveToWrapper(String.class)).isNull();
    assertThat(primitiveToWrapper(Integer.class)).isNull();
  }
}
