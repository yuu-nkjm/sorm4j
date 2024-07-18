package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Array;
import java.sql.SQLException;

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
    assertNull(ArrayUtils.convertToObjectArray(null));
  }

  @Test
  public void testConvertSqlArrayToArray_WithSqlArray() throws SQLException {
    Array sqlArrayMock = mock(Array.class);
    Object[] expectedArray = {1, 2, 3};
    when(sqlArrayMock.getArray()).thenReturn(expectedArray);

    assertThrows(
        IllegalArgumentException.class,
        () -> ArrayUtils.convertSqlArrayToArray(Integer[].class, sqlArrayMock));
  }

  public void testConvertSqlArrayToArray_WithSqlException() throws SQLException {
    Array sqlArrayMock = mock(Array.class);
    when(sqlArrayMock.getArray()).thenThrow(new SQLException("Database error"));
    ArrayUtils.convertSqlArrayToArray(Integer[].class, sqlArrayMock);
  }

  @Test
  public void testConvertSqlArrayToArray_WithNullInput() {
    Object result = ArrayUtils.convertSqlArrayToArray(Integer[].class, null);
    assertNull(result);
  }

  @Test
  public void testConvertToObjectArray_WithIntegerArray() {
    Integer[] srcArray = {1, 2, 3};
    Integer[] result = ArrayUtils.convertToObjectArray(Integer.class, srcArray);

    assertNotNull(result);
    assertTrue(result instanceof Integer[]);
    assertArrayEquals(srcArray, result);
  }

  @Test
  public void testConvertToObjectArray_WithDoubleArray() {
    Double[] srcArray = {1.0, 2.0, 3.0};
    Double[] result = ArrayUtils.convertToObjectArray(Double.class, srcArray);

    assertNotNull(result);
    assertTrue(result instanceof Double[]);
    assertArrayEquals(srcArray, result);
  }

  @Test
  public void testConvertToObjectArray_WithNullArray() {
    assertThat(ArrayUtils.convertToObjectArray(Integer.class, null)).isNull();
    assertThat(ArrayUtils.convertToObjectArray(Integer.class, new int[] {1, 2}))
        .isEqualTo(new Integer[] {1, 2});
  }

  @Test
  public void testConvertToObjectArray_WithTypeMismatch() {
    Object[] srcArray = new Object[] {1, 2, 3.0};
    assertThrows(
        IllegalArgumentException.class,
        () -> ArrayUtils.convertToObjectArray(Integer.class, srcArray));
  }

  @Test
  public void testToObjectArrayWithNullInputs() {
    assertNull(
        ArrayUtils.toObjectArray((boolean[]) null), "Should return null for null boolean array");
    assertNull(ArrayUtils.toObjectArray((byte[]) null), "Should return null for null byte array");
    assertNull(ArrayUtils.toObjectArray((char[]) null), "Should return null for null char array");
    assertNull(
        ArrayUtils.toObjectArray((double[]) null), "Should return null for null double array");
    assertNull(ArrayUtils.toObjectArray((float[]) null), "Should return null for null float array");
    assertNull(ArrayUtils.toObjectArray((int[]) null), "Should return null for null int array");
    assertNull(ArrayUtils.toObjectArray((long[]) null), "Should return null for null long array");
    assertNull(ArrayUtils.toObjectArray((short[]) null), "Should return null for null short array");
  }

  @Test
  public void testConvertSqlArrayToArrayWithNull() {
    assertNull(
        ArrayUtils.convertSqlArrayToArray(Object.class, null),
        "Should return null for null SQL array");
  }

  @Test
  public void testConvertToObjectArrayWithNullObjectArray() {
    assertNull(ArrayUtils.convertToObjectArray(null), "Should return null");
  }

  @Test
  public void testSplitWithNull() {
    assertThrows(
        NullPointerException.class,
        () -> ArrayUtils.split(1, (Object[]) null),
        "Should throw NullPointerException for null object split");
  }

  @Test
  public void testAddWithNullIntArray() {
    assertThrows(
        NullPointerException.class,
        () -> ArrayUtils.add(null, 1),
        "Should throw NullPointerException for null int array addition");
  }
}
