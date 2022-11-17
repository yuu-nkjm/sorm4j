package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
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
    assertThat(ArrayUtils
        .convertToObjectArray(new int[][][] {{{1, 2, 3}, {4, 5, 6}}, {{7, 8, 9}, {10, 11, 12}}}))
            .isEqualTo(new Integer[][][] {{{1, 2, 3}, {4, 5, 6}}, {{7, 8, 9}, {10, 11, 12}}});

  }


}
