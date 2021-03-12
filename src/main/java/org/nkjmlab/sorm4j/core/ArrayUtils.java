package org.nkjmlab.sorm4j.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class ArrayUtils {

  private ArrayUtils() {}

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

  public static int[] add(int[] array, int i) {
    final int arrayLength = Array.getLength(array);
    final int[] newArray = new int[arrayLength + 1];
    System.arraycopy(array, 0, newArray, 0, arrayLength);
    newArray[arrayLength] = i;
    return newArray;
  }



}
