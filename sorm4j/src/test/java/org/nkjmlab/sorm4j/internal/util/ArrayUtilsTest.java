package org.nkjmlab.sorm4j.internal.util;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest {


  @Test
  void testToObjectArrayBooleanArray() {
    ArrayUtils.toObjectArray(new boolean[] {true, false});
  }

  @Test
  void testToObjectArrayByteArray() {
    ArrayUtils.toObjectArray(new byte[] {(byte) 1});
  }

  @Test
  void testToObjectArrayCharArray() {
    ArrayUtils.toObjectArray(new char[] {'1'});
  }

  @Test
  void testToObjectArrayDoubleArray() {
    ArrayUtils.toObjectArray(new double[] {0.1});
  }

  @Test
  void testToObjectArrayFloatArray() {
    ArrayUtils.toObjectArray(new float[] {0.1f});
  }

  @Test
  void testToObjectArrayIntArray() {
    ArrayUtils.toObjectArray(new int[] {1});
  }

  @Test
  void testToObjectArrayLongArray() {
    ArrayUtils.toObjectArray(new long[] {1L});
  }

  @Test
  void testToObjectArrayShortArray() {
    ArrayUtils.toObjectArray(new short[] {(short) 1});
  }

}
