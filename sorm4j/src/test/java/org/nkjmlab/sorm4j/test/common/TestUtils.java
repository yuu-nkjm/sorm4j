package org.nkjmlab.sorm4j.test.common;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

public class TestUtils {

  public static final boolean[] PRIMITIVE_BOOLEAN_ARRAY = new boolean[] {true, false};
  public static final Boolean[] BOOLEAN_ARRAY = new Boolean[] {true, false};

  public static final char[] PRIMITIVE_CHAR_ARRAY = new char[] {'a', 'b'};
  public static final Character[] CHARRACTER_ARRAY = new Character[] {'a', 'b'};

  public static final byte[] PRIMITIVE_BYTE_ARRAY = new byte[] {'a', 'b'};
  public static final Byte[] BYTE_ARRAY = new Byte[] {'a', 'b'};

  public static final short[] PRIMITIVE_SHORT_ARRAY = new short[] {'a', 'b'};

  public static final Short[] SHORT_ARRAY = new Short[] {'a', 'b'};

  public static final int[] PRIMITIVE_INT_ARRAY = new int[] {1, 2};
  public static final Integer[] INTEGER_ARRAY = new Integer[] {1, 2};

  public static final long[] PRIMITIVE_LONG_ARRAY = new long[] {1, 2};
  public static final Long[] LONG_ARRAY = new Long[] {1l, 2l};

  public static final float[] PRIMITIVE_FLOAT_ARRAY = new float[] {1, 2};
  public static final Float[] FLOAT_ARRAY = new Float[] {1f, 2f};

  public static final double[] PRIMITIVE_DOUBLE_ARRAY = new double[] {1d, 2d};
  public static final Double[] DOUBLE_ARRAY = new Double[] {1d, 2d};

  public static final String[] STRING_ARRAY = new String[] {"a", "b"};
  public static final BigDecimal[] BIG_DECIMAL_ARRAY = {new BigDecimal("1")};
  public static final Date[] DATE_ARRAY = new Date[] {Date.valueOf(LocalDate.now())};
  public static final Time[] TIME_ARRAY = new Time[] {Time.valueOf(LocalTime.now())};
  public static final Timestamp[] TIMESTAMP_ARRAY =
      new Timestamp[] {Timestamp.valueOf(LocalDateTime.now())};

  public static final LocalDate[] LOCAL_DATE_ARRAY = {LocalDate.now()};
  public static final LocalTime[] LOCAL_TIME_ARRAY = {LocalTime.now()};
  public static final LocalDateTime[] LOCAL_DATE_TIME_ARRAY = {LocalDateTime.now()};
  public static final Instant[] INSTANT_ARRAY = {Instant.now()};

  public static final OffsetTime[] OFFSET_TIME_ARRAY = {OffsetTime.now()};
  public static final OffsetDateTime[] OFFSET_DATE_TIME_ARRAY = {OffsetDateTime.now()};

  public static final Object[] OBJECT_ARRAY = new Object[] {new File("./")};
}
