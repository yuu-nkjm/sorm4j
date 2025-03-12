package org.nkjmlab.sorm4j.extension.datatype.jts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class JtsSupportTypesTest {

  private final JtsSupportTypes supportTypes = new JtsSupportTypes();

  @Test
  void testIsSupport_withSupportedType() {
    assertThat(supportTypes.isSupport(GeometryJts.class)).isTrue();
  }

  @Test
  void testIsSupport_withUnsupportedType() {
    assertThat(supportTypes.isSupport(String.class)).isFalse();
    assertThat(supportTypes.isSupport(Integer.class)).isFalse();
  }

  @Test
  void testIsSupport_withArrayType() {
    assertThat(supportTypes.isSupport(GeometryJts[].class)).isTrue();
    assertThat(supportTypes.isSupport(String[].class)).isFalse();
  }

  @Test
  void testIsSupport_withNestedArrayType() {
    assertThat(supportTypes.isSupport(GeometryJts[][].class)).isTrue();
    assertThat(supportTypes.isSupport(Integer[][].class)).isFalse();
  }

  @Test
  void testIsSupport_withNull() {
    assertThatThrownBy(() -> supportTypes.isSupport(null)).isInstanceOf(NullPointerException.class);
  }
}
