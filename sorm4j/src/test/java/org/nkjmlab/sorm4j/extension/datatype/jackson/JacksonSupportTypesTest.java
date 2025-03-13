package org.nkjmlab.sorm4j.extension.datatype.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJacksonColumn;

class JacksonSupportTypesTest {

  private final JacksonSupportTypes supportTypes = new JacksonSupportTypes();

  @OrmJacksonColumn
  static class AnnotatedClass {}

  static class NonAnnotatedClass {}

  @Test
  void testIsSupport_withAnnotatedClass() {
    assertThat(supportTypes.isSupport(AnnotatedClass.class)).isTrue();
    assertThat(supportTypes.isSupport(AnnotatedClass.class)).isTrue();
  }

  @Test
  void testIsSupport_withNonAnnotatedClass() {
    assertThat(supportTypes.isSupport(NonAnnotatedClass.class)).isFalse();
    assertThat(supportTypes.isSupport(NonAnnotatedClass.class)).isFalse();
  }

  @Test
  void testIsSupport_withSupportedCollectionTypes() {
    assertThat(supportTypes.isSupport(List.class)).isTrue();
    assertThat(supportTypes.isSupport(Map.class)).isTrue();
    assertThat(supportTypes.isSupport(List.class)).isTrue();
    assertThat(supportTypes.isSupport(Map.class)).isTrue();
  }

  @Test
  void testIsSupport_withUnsupportedTypes() {
    assertThat(supportTypes.isSupport(String.class)).isFalse();
    assertThat(supportTypes.isSupport(Integer.class)).isFalse();
    assertThat(supportTypes.isSupport(String.class)).isFalse();
    assertThat(supportTypes.isSupport(Integer.class)).isFalse();
  }

  @Test
  void testIsSupport_withArrayType() {
    assertThat(supportTypes.isSupport(AnnotatedClass[].class)).isTrue();
    assertThat(supportTypes.isSupport(NonAnnotatedClass[].class)).isFalse();
    assertThat(supportTypes.isSupport(AnnotatedClass[].class)).isTrue();
    assertThat(supportTypes.isSupport(NonAnnotatedClass[].class)).isFalse();
  }

  @Test
  void testIsSupport_withNestedArrayType() {
    assertThat(supportTypes.isSupport(AnnotatedClass[][].class)).isTrue();
    assertThat(supportTypes.isSupport(NonAnnotatedClass[][].class)).isFalse();
    assertThat(supportTypes.isSupport(AnnotatedClass[][].class)).isTrue();
    assertThat(supportTypes.isSupport(NonAnnotatedClass[][].class)).isFalse();
  }

  @Test
  void testIsSupport_withNull() {
    assertThatThrownBy(() -> supportTypes.isSupport(null)).isInstanceOf(NullPointerException.class);
  }
}
