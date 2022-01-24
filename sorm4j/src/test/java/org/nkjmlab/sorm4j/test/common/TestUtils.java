package org.nkjmlab.sorm4j.test.common;

import java.util.Map;

public class TestUtils {

  public static final boolean[] PRIMITIVE_BOOLEAN_ARRAY = new boolean[] {true, false};
  public static final char[] PRIMITIVE_CHAR_ARRAY = new char[] {'a', 'b'};
  public static final byte[] PRIMITIVE_BYTE_ARRAY = new byte[] {'a', 'b'};
  public static final short[] PRIMITIVE_SHORT_ARRAY = new short[] {'a', 'b'};
  public static final int[] PRIMITIVE_INT_ARRAY = new int[] {'a', 'b'};

  public static final Map<String, Object> PRIMITIVE_ARRAYS_MAP = Map.of("boolean", PRIMITIVE_BOOLEAN_ARRAY);

}
