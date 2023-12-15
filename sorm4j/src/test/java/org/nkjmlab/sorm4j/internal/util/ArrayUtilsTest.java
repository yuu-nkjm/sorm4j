package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest {

  @Test
  void testToObjectArrayBooleanArray() {
    assertThat(SystemPropertyUtils.getJavaProperties().toString()).contains("java");
    assertThat(ArrayUtils.toObjectArray(new boolean[] {true, false}))
        .isEqualTo(new Boolean[] {true, false});
  }

  @Test
  void testToObjectArrayByteArray() {
    assertThat(ArrayUtils.toObjectArray(new byte[] {(byte) 1, (byte) 2}))
        .isEqualTo(new Byte[] {(byte) 1, (byte) 2});
  }

  @Test
  void testToObjectArrayCharArray() {
    assertThat(ArrayUtils.toObjectArray(new char[] {'1', '2'}))
        .isEqualTo(new Character[] {'1', '2'});
  }

  @Test
  void testToObjectArrayDoubleArray() {
    assertThat(ArrayUtils.toObjectArray(new double[] {0.1, 0.2}))
        .isEqualTo(new Double[] {0.1, 0.2});
  }

  @Test
  void testToObjectArrayFloatArray() {
    assertThat(ArrayUtils.toObjectArray(new float[] {0.1f, 0.2f}))
        .isEqualTo(new Float[] {0.1f, 0.2f});
  }

  @Test
  void testToObjectArrayIntArray() {
    assertThat(ArrayUtils.toObjectArray(new int[] {1, 2})).isEqualTo(new Integer[] {1, 2});
  }

  @Test
  void testToObjectArrayLongArray() {
    assertThat(ArrayUtils.toObjectArray(new long[] {1L, 2L})).isEqualTo(new Long[] {1L, 2L});
  }

  @Test
  void testToObjectArrayShortArray() {
    assertThat(ArrayUtils.toObjectArray(new short[] {(short) 1, (short) 2}))
        .isEqualTo(new Short[] {(short) 1, (short) 2});
  }

  @Test
  void testConvertToObjectArray() {
    assertThat(ArrayUtils.convertToObjectArray(new boolean[] {true}))
        .isEqualTo(new Boolean[] {true});
    assertThat(ArrayUtils.convertToObjectArray(new byte[] {1})).isEqualTo(new Byte[] {1});
    assertThat(ArrayUtils.convertToObjectArray(new char[] {1})).isEqualTo(new Character[] {1});
    assertThat(ArrayUtils.convertToObjectArray(new short[] {1})).isEqualTo(new Short[] {1});

    assertThat(ArrayUtils.convertToObjectArray(new int[] {1})).isEqualTo(new Integer[] {1});
    assertThat(ArrayUtils.convertToObjectArray(new long[] {1})).isEqualTo(new Long[] {1L});
    assertThat(ArrayUtils.convertToObjectArray(new float[] {1})).isEqualTo(new Float[] {1f});
    assertThat(ArrayUtils.convertToObjectArray(new double[] {1})).isEqualTo(new Double[] {1d});

    assertThat(ArrayUtils.convertToObjectArray(new int[][] {{1, 2, 3}, {4, 5, 6}}))
        .isEqualTo(new Integer[][] {{1, 2, 3}, {4, 5, 6}});
    assertThat(
            ArrayUtils.convertToObjectArray(
                new int[][][] {{{1, 2, 3}, {4, 5, 6}}, {{7, 8, 9}, {10, 11, 12}}}))
        .isEqualTo(new Integer[][][] {{{1, 2, 3}, {4, 5, 6}}, {{7, 8, 9}, {10, 11, 12}}});
  }

  @Test
  void testToObjectArrayBooleanArray1() {
    assertThat(ArrayUtils.toObjectArray(new boolean[] {true, false}))
        .isEqualTo(new Boolean[] {true, false});
  }

  @Test
  void testToObjectArrayByteArray1() {
    assertThat(ArrayUtils.toObjectArray(new byte[] {(byte) 1, (byte) 2}))
        .isEqualTo(new Byte[] {(byte) 1, (byte) 2});
  }

  @Test
  void testToObjectArrayCharArray2() {
    assertThat(ArrayUtils.toObjectArray(new char[] {'1', '2'}))
        .isEqualTo(new Character[] {'1', '2'});
  }

  @Test
  void testToObjectArrayDoubleArray1() {
    assertThat(ArrayUtils.toObjectArray(new double[] {0.1, 0.2}))
        .isEqualTo(new Double[] {0.1, 0.2});
  }

  @Test
  void testToObjectArrayFloatArray2() {
    assertThat(ArrayUtils.toObjectArray(new float[] {0.1f, 0.2f}))
        .isEqualTo(new Float[] {0.1f, 0.2f});
  }

  @Test
  void testToObjectArrayIntArray1() {
    assertThat(ArrayUtils.toObjectArray(new int[] {1, 2})).isEqualTo(new Integer[] {1, 2});
  }

  @Test
  void testToObjectArrayLongArray2() {
    assertThat(ArrayUtils.toObjectArray(new long[] {1L, 2L})).isEqualTo(new Long[] {1L, 2L});
  }

  @Test
  void testToObjectArrayShortArray1() {
    assertThat(ArrayUtils.toObjectArray(new short[] {(short) 1, (short) 2}))
        .isEqualTo(new Short[] {(short) 1, (short) 2});
  }

  @Test
  void testConvertToObjectArray2() {
    assertThat(ArrayUtils.convertToObjectArray(new boolean[] {true}))
        .isEqualTo(new Boolean[] {true});
    assertThat(ArrayUtils.convertToObjectArray(new int[][] {{1, 2, 3}, {4, 5, 6}}))
        .isEqualTo(new Integer[][] {{1, 2, 3}, {4, 5, 6}});
  }

  @Test
  void testToObjectArrayWithNull() {
    assertThat(ArrayUtils.toObjectArray((boolean[]) null)).isNull();
  }

  @Test
  void testConvertToObjectArrayWithInvalidType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ArrayUtils.convertToObjectArray(new Object());
        });
  }

  @Test
  void testToObjectArrayEmptyArrays() {
    assertThat(ArrayUtils.toObjectArray(new boolean[0])).isEqualTo(new Boolean[0]);
    assertThat(ArrayUtils.toObjectArray(new byte[0])).isEqualTo(new Byte[0]);
    assertThat(ArrayUtils.toObjectArray(new char[0])).isEqualTo(new Character[0]);
    assertThat(ArrayUtils.toObjectArray(new double[0])).isEqualTo(new Double[0]);
    assertThat(ArrayUtils.toObjectArray(new float[0])).isEqualTo(new Float[0]);
    assertThat(ArrayUtils.toObjectArray(new int[0])).isEqualTo(new Integer[0]);
    assertThat(ArrayUtils.toObjectArray(new long[0])).isEqualTo(new Long[0]);
    assertThat(ArrayUtils.toObjectArray(new short[0])).isEqualTo(new Short[0]);
  }

  @Test
  void testToObjectArrayExtremeValues() {
    assertThat(ArrayUtils.toObjectArray(new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE}))
        .isEqualTo(new Integer[] {Integer.MAX_VALUE, Integer.MIN_VALUE});
    assertThat(ArrayUtils.toObjectArray(new double[] {Double.MAX_VALUE, Double.MIN_VALUE}))
        .isEqualTo(new Double[] {Double.MAX_VALUE, Double.MIN_VALUE});
  }

  @Test
  void testConvertToObjectArrayWithNullInput() {
    assertThrows(NullPointerException.class, () -> ArrayUtils.convertToObjectArray(null));
  }
}
