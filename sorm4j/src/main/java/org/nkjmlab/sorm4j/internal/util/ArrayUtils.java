package org.nkjmlab.sorm4j.internal.util;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nkjmlab.sorm4j.util.function.exception.Try;

public final class ArrayUtils {
  private static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
  private static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
  private static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
  private static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
  private static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
  private static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
  private static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
  private static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

  private ArrayUtils() {}

  public static int[] add(int[] array, int i) {
    final int arrayLength = Array.getLength(array);
    final int[] newArray = new int[arrayLength + 1];
    System.arraycopy(array, 0, newArray, 0, arrayLength);
    newArray[arrayLength] = i;
    return newArray;
  }

  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T> List<T[]> split(int size, T... objects) {
    int slotNum = Math.floorDiv(objects.length, size);
    List<T[]> result = new ArrayList<>(slotNum + 1);

    for (int i = 0; i < slotNum; i++) {
      result.add(Arrays.copyOfRange(objects, size * i, size * (i + 1)));
    }
    if (size * slotNum != objects.length) {
      result.add(Arrays.copyOfRange(objects, size * slotNum, objects.length));
    }
    return result;
  }

  public static Boolean[] toObjectArray(boolean[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_BOOLEAN_OBJECT_ARRAY;
    }
    final Boolean[] result = new Boolean[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Boolean.valueOf(array[i]);
    }
    return result;
  }

  public static Byte[] toObjectArray(byte[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_BYTE_OBJECT_ARRAY;
    }
    final Byte[] result = new Byte[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Byte.valueOf(array[i]);
    }
    return result;
  }

  public static Character[] toObjectArray(char[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_CHARACTER_OBJECT_ARRAY;
    }
    final Character[] result = new Character[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Character.valueOf(array[i]);
    }
    return result;
  }

  public static Double[] toObjectArray(double[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_DOUBLE_OBJECT_ARRAY;
    }
    final Double[] result = new Double[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Double.valueOf(array[i]);
    }
    return result;
  }

  public static Float[] toObjectArray(float[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_FLOAT_OBJECT_ARRAY;
    }
    final Float[] result = new Float[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Float.valueOf(array[i]);
    }
    return result;
  }

  public static Integer[] toObjectArray(int[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_INTEGER_OBJECT_ARRAY;
    }
    final Integer[] result = new Integer[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Integer.valueOf(array[i]);
    }
    return result;
  }

  public static Long[] toObjectArray(long[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_LONG_OBJECT_ARRAY;
    }
    final Long[] result = new Long[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Long.valueOf(array[i]);
    }
    return result;
  }

  public static Short[] toObjectArray(short[] array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_SHORT_OBJECT_ARRAY;
    }
    final Short[] result = new Short[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Short.valueOf(array[i]);
    }
    return result;
  }

  /**
   * @param <T>
   * @param toComponentType
   * @param srcArray
   * @return
   */
  public static Object convertSqlArrayToArray(Class<?> toComponentType, java.sql.Array srcArray) {
    return convertSqlArrayToArrayAux(toComponentType, srcArray);
  }

  private static Object convertSqlArrayToArrayAux(Class<?> toComponentType, Object srcArray) {
    if (srcArray == null) {
      return null;
    }
    if (srcArray instanceof java.sql.Array) {
      try {
        java.sql.Array sqlArray = (java.sql.Array) srcArray;
        Object array = sqlArray.getArray();
        return convertSqlArrayToArrayAux(toComponentType, array);
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    }
    if (toComponentType.isArray()) {
      int length = java.lang.reflect.Array.getLength(srcArray);
      Class<?> compArrType = toComponentType.getComponentType();
      Object destArray = java.lang.reflect.Array.newInstance(toComponentType, length);
      for (int i = 0; i < length; i++) {
        Object v = java.lang.reflect.Array.get(srcArray, i);
        java.lang.reflect.Array.set(destArray, i, convertSqlArrayToArrayAux(compArrType, v));
      }
      return destArray;
    } else {
      int length = java.lang.reflect.Array.getLength(srcArray);
      Object destArray = java.lang.reflect.Array.newInstance(toComponentType, length);
      for (int i = 0; i < length; i++) {
        Object v = java.lang.reflect.Array.get(srcArray, i);
        java.lang.reflect.Array.set(destArray, i, v);
      }
      return destArray;
    }
  }

  public static <T> T[] convertToObjectArray(Class<T> componentType, Object srcArray) {
    if (srcArray == null) {
      return null;
    }
    final int length = Array.getLength(srcArray);
    Object destArray =
        Array.newInstance(
            componentType.isPrimitive()
                ? ClassUtils.primitiveToWrapper(componentType)
                : componentType,
            length);
    for (int i = 0; i < length; i++) {
      Object v = Array.get(srcArray, i);
      Array.set(destArray, i, v);
    }
    @SuppressWarnings("unchecked")
    T[] ret = (T[]) destArray;
    return ret;
  }

  public static Object[] convertToObjectArray(Object srcArray) {
    if (srcArray == null) {
      return null;
    }
    Class<?> componentType = srcArray.getClass().getComponentType();
    if (componentType == null) {
      throw new IllegalArgumentException("the argument could not be convert to an object array.");
    }
    if (!componentType.isArray()) {
      switch (srcArray.getClass().getComponentType().toString()) {
        case "boolean":
          return toObjectArray((boolean[]) srcArray);
        case "byte":
          return toObjectArray((byte[]) srcArray);
        case "char":
          return toObjectArray((char[]) srcArray);
        case "short":
          return toObjectArray((short[]) srcArray);
        case "int":
          return toObjectArray((int[]) srcArray);
        case "long":
          return toObjectArray((long[]) srcArray);
        case "float":
          return toObjectArray((float[]) srcArray);
        case "double":
          return toObjectArray((double[]) srcArray);
        default:
          return (Object[]) srcArray;
      }
    }
    Object o = Array.get(srcArray, 0);
    final int length = Array.getLength(srcArray);
    Object destArray =
        Array.newInstance(
            Array.newInstance(convertToObjectArray(o).getClass().getComponentType(), 0).getClass(),
            length);
    for (int i = 0; i < length; i++) {
      Object v = Array.get(srcArray, i);
      Array.set(destArray, i, convertToObjectArray(v));
    }
    return (Object[]) destArray;
  }

  public static Class<?> getInternalComponentType(Class<?> compType) {
    Class<?> ret = compType;
    while (ret.isArray()) {
      ret = ret.getComponentType();
    }
    return ret;
  }
}
