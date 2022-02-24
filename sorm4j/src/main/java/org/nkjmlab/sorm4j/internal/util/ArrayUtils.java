package org.nkjmlab.sorm4j.internal.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArrayUtils {
  private static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
  private static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
  private static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
  private static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
  private static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
  private static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
  private static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
  private static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

  public static int[] add(int[] array, int i) {
    final int arrayLength = Array.getLength(array);
    final int[] newArray = new int[arrayLength + 1];
    System.arraycopy(array, 0, newArray, 0, arrayLength);
    newArray[arrayLength] = i;
    return newArray;
  }

  @SafeVarargs
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

  private ArrayUtils() {}

  /**
   *
   * @param <T>
   * @param valueType
   * @param srcArray Object[] or primitive array
   * @return
   */
  public static Object convertToArray(Class<?> valueType, Object srcArray) {
    final int length = Array.getLength(srcArray);
    Object destArray = Array.newInstance(valueType, length);
    for (int i = 0; i < length; i++) {
      Object v = Array.get(srcArray, i);
      Array.set(destArray, i, v);
    }
    return destArray;
  }

  public static <T> T[] convertToObjectArray(Class<?> valueType, Object srcArray) {
    final int length = Array.getLength(srcArray);
    Object destArray = Array.newInstance(
        valueType.isPrimitive() ? ClassUtils.primitiveToWrapper(valueType) : valueType, length);
    for (int i = 0; i < length; i++) {
      Object v = Array.get(srcArray, i);
      Array.set(destArray, i, v);
    }
    @SuppressWarnings("unchecked")
    T[] ret = (T[]) destArray;
    return ret;
  }


}
