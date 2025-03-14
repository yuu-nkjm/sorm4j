package org.nkjmlab.sorm4j.internal.context.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultColumnValueToMapValueConvertersTest {

  private DefaultColumnValueToMapValueConverters converter;
  private ResultSet resultSet;

  @BeforeEach
  void setUp() {
    converter = new DefaultColumnValueToMapValueConverters();
    resultSet = mock(ResultSet.class);
  }

  @Test
  void testConvertToValue_Null() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    assertNull(converter.convertToValue(resultSet, 1, Types.NULL));
  }

  @Test
  void testConvertToValue_Integer() throws SQLException {
    when(resultSet.getInt(1)).thenReturn(42);
    when(resultSet.wasNull()).thenReturn(false);
    assertEquals(42, converter.convertToValue(resultSet, 1, Types.INTEGER));

    when(resultSet.getInt(1)).thenReturn(0);
    when(resultSet.wasNull()).thenReturn(true);
    assertNull(converter.convertToValue(resultSet, 1, Types.INTEGER));
  }

  @Test
  void testConvertToValue_String() throws SQLException {
    when(resultSet.getString(1)).thenReturn("test");
    assertEquals("test", converter.convertToValue(resultSet, 1, Types.VARCHAR));
  }

  @Test
  void testConvertToValue_Boolean() throws SQLException {
    when(resultSet.getBoolean(1)).thenReturn(true);
    when(resultSet.wasNull()).thenReturn(false);
    assertEquals(true, converter.convertToValue(resultSet, 1, Types.BOOLEAN));
  }

  @Test
  void testConvertToValue_Decimal() throws SQLException {
    when(resultSet.getBigDecimal(1)).thenReturn(new BigDecimal("123.45"));
    assertEquals(new BigDecimal("123.45"), converter.convertToValue(resultSet, 1, Types.DECIMAL));
  }

  @Test
  void testConvertToValue_Date() throws SQLException {
    Date date = new Date(System.currentTimeMillis());
    when(resultSet.getDate(1)).thenReturn(date);
    assertEquals(date, converter.convertToValue(resultSet, 1, Types.DATE));
  }

  @Test
  void testConvertToValue_Time() throws SQLException {
    Time time = new Time(System.currentTimeMillis());
    when(resultSet.getTime(1)).thenReturn(time);
    assertEquals(time, converter.convertToValue(resultSet, 1, Types.TIME));
  }

  @Test
  void testConvertToValue_Timestamp() throws SQLException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    when(resultSet.getTimestamp(1)).thenReturn(timestamp);
    assertEquals(timestamp, converter.convertToValue(resultSet, 1, Types.TIMESTAMP));
  }

  @Test
  void testConvertToValue_URL() throws SQLException, MalformedURLException {
    URL url = URI.create("http://example.com").toURL();
    when(resultSet.getURL(1)).thenReturn(url);
    assertEquals(url, converter.convertToValue(resultSet, 1, Types.DATALINK));
  }

  @Test
  void testConvertToValue_Byte() throws SQLException {
    when(resultSet.getByte(1)).thenReturn((byte) 5);
    when(resultSet.wasNull()).thenReturn(false);
    assertEquals((byte) 5, converter.convertToValue(resultSet, 1, Types.TINYINT));
  }

  @Test
  void testConvertToValue_Short() throws SQLException {
    when(resultSet.getShort(1)).thenReturn((short) 10);
    when(resultSet.wasNull()).thenReturn(false);
    assertEquals((short) 10, converter.convertToValue(resultSet, 1, Types.SMALLINT));
  }

  @Test
  void testConvertToValue_OtherTypes() throws SQLException {
    when(resultSet.getObject(1)).thenReturn("customObject");
    assertEquals("customObject", converter.convertToValue(resultSet, 1, Types.OTHER));
  }

  @Test
  void testConvertSqlRef() throws SQLException {
    Ref mockRef = mock(Ref.class);
    when(resultSet.getRef(1)).thenReturn(mockRef);
    Ref result = (Ref) converter.convertToValue(resultSet, 1, Types.REF);
    assertThat(result).isEqualTo(mockRef);
  }

  @Test
  void testConvertSqlRowId() throws SQLException {
    RowId mockRowId = mock(RowId.class);
    when(resultSet.getRowId(1)).thenReturn(mockRowId);
    RowId result = (RowId) converter.convertToValue(resultSet, 1, Types.ROWID);
    assertThat(result).isEqualTo(mockRowId);
  }
}
