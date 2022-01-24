package org.nkjmlab.sorm4j.internal.util;

public final class ClassUtils {
  private ClassUtils() {}

  public static Class<?> convertToClass(String className) {
    switch (className) {
      case "java.lang.Boolean":
        return Boolean.class;
      case "java.lang.Character":
        return Character.class;
      case "java.lang.Byte":
        return Byte.class;
      case "java.lang.Short":
        return Short.class;
      case "java.lang.Integer":
        return Integer.class;
      case "java.lang.Long":
        return Long.class;
      case "java.lang.Float":
        return Float.class;
      case "java.lang.Double":
        return Double.class;
      case "java.lang.Object":
        return Object.class;
      case "boolean":
        return boolean.class;
      case "char":
        return char.class;
      case "byte":
        return byte.class;
      case "short":
        return short.class;
      case "int":
        return int.class;
      case "long":
        return long.class;
      case "float":
        return float.class;
      case "double":
        return double.class;
      default:
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException e) {
          throw Try.rethrow(e);
        }
    }
  }
}
