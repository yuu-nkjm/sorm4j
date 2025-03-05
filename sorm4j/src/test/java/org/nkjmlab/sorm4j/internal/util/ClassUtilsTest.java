package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ClassUtilsTest {

  @Test
  void testConvertToClass() {
    Map<String, Class<?>> clazzes =
        Map.of(
            "java.lang.Boolean",
            Boolean.class,
            "java.lang.Character",
            Character.class,
            "java.lang.Byte",
            Byte.class,
            "java.lang.Short",
            Short.class,
            "java.lang.Integer",
            Integer.class,
            "java.lang.Long",
            Long.class,
            "java.lang.Float",
            Float.class,
            "java.lang.Double",
            Double.class,
            "java.lang.Object",
            Object.class);

    clazzes
        .entrySet()
        .forEach(en -> assertThat(ClassUtils.convertToClass(en.getKey())).isEqualTo(en.getValue()));

    Map<String, Class<?>> primitives =
        Map.of(
            "boolean",
            boolean.class,
            "char",
            char.class,
            "byte",
            byte.class,
            "short",
            short.class,
            "int",
            int.class,
            "long",
            long.class,
            "float",
            float.class,
            "double",
            double.class);

    primitives
        .entrySet()
        .forEach(en -> assertThat(ClassUtils.convertToClass(en.getKey())).isEqualTo(en.getValue()));
  }
}
