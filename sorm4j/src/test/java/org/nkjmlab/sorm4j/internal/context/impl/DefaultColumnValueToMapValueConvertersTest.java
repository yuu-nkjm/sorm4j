package org.nkjmlab.sorm4j.internal.context.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultColumnValueToMapValueConvertersTest {

  private DefaultColumnValueToMapValueConverters converter;
  private ResultSet mockResultSet;

  @BeforeEach
  void setUp() {
    converter = new DefaultColumnValueToMapValueConverters();
    mockResultSet = mock(ResultSet.class);
  }

  @Test
  void testConvertToValue_Null() throws SQLException {
    when(mockResultSet.getObject(1)).thenReturn(null);
    assertNull(converter.convertToValue(mockResultSet, 1, Types.NULL));
  }

  @Test
  void testConvertToValue_Integer() throws SQLException {
    when(mockResultSet.getInt(1)).thenReturn(42);
    when(mockResultSet.wasNull()).thenReturn(false);
    assertEquals(42, converter.convertToValue(mockResultSet, 1, Types.INTEGER));

    when(mockResultSet.getInt(1)).thenReturn(0);
    when(mockResultSet.wasNull()).thenReturn(true);
    assertNull(converter.convertToValue(mockResultSet, 1, Types.INTEGER));
  }

  @Test
  void testConvertToValue_String() throws SQLException {
    when(mockResultSet.getString(1)).thenReturn("test");
    assertEquals("test", converter.convertToValue(mockResultSet, 1, Types.VARCHAR));
  }

  @Test
  void testConvertToValue_Boolean() throws SQLException {
    when(mockResultSet.getBoolean(1)).thenReturn(true);
    when(mockResultSet.wasNull()).thenReturn(false);
    assertEquals(true, converter.convertToValue(mockResultSet, 1, Types.BOOLEAN));
  }

  @Test
  void testConvertToValue_Decimal() throws SQLException {
    when(mockResultSet.getBigDecimal(1)).thenReturn(new BigDecimal("123.45"));
    assertEquals(
        new BigDecimal("123.45"), converter.convertToValue(mockResultSet, 1, Types.DECIMAL));
  }

  @Test
  void testConvertToValue_Date() throws SQLException {
    Date date = new Date(System.currentTimeMillis());
    when(mockResultSet.getDate(1)).thenReturn(date);
    assertEquals(date, converter.convertToValue(mockResultSet, 1, Types.DATE));
  }

  @Test
  void testConvertToValue_Time() throws SQLException {
    Time time = new Time(System.currentTimeMillis());
    when(mockResultSet.getTime(1)).thenReturn(time);
    assertEquals(time, converter.convertToValue(mockResultSet, 1, Types.TIME));
  }

  @Test
  void testConvertToValue_Timestamp() throws SQLException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    when(mockResultSet.getTimestamp(1)).thenReturn(timestamp);
    assertEquals(timestamp, converter.convertToValue(mockResultSet, 1, Types.TIMESTAMP));
  }

  @Test
  void testConvertToValue_URL() throws SQLException, MalformedURLException {
    URL url = URI.create("http://example.com").toURL();
    when(mockResultSet.getURL(1)).thenReturn(url);
    assertEquals(url, converter.convertToValue(mockResultSet, 1, Types.DATALINK));
  }

  @Test
  void testConvertToValue_Byte() throws SQLException {
    when(mockResultSet.getByte(1)).thenReturn((byte) 5);
    when(mockResultSet.wasNull()).thenReturn(false);
    assertEquals((byte) 5, converter.convertToValue(mockResultSet, 1, Types.TINYINT));
  }

  @Test
  void testConvertToValue_Short() throws SQLException {
    when(mockResultSet.getShort(1)).thenReturn((short) 10);
    when(mockResultSet.wasNull()).thenReturn(false);
    assertEquals((short) 10, converter.convertToValue(mockResultSet, 1, Types.SMALLINT));
  }

  @Test
  void testConvertToValue_OtherTypes() throws SQLException {
    when(mockResultSet.getObject(1)).thenReturn("customObject");
    assertEquals("customObject", converter.convertToValue(mockResultSet, 1, Types.OTHER));
  }
}
