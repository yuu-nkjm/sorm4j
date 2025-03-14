package org.nkjmlab.sorm4j.internal.context.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.datatype.container.GeometryText;
import org.nkjmlab.sorm4j.extension.datatype.container.JsonByte;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DefaultColumnValueToJavaObjectConvertersTest {

  @Test
  void testConvertTo() {
    ColumnValueToJavaObjectConverter columnValueConverter =
        new ColumnValueToJavaObjectConverter() {

          @Override
          public boolean test(Class<?> toType) {
            return toType.equals(String.class);
          }

          @Override
          public <T> T convertTo(
              ResultSet resultSet, int columnIndex, int columnType, Class<T> toType)
              throws SQLException {
            return toType.cast(resultSet.getString(columnIndex).toString());
          }
        };

    SqlParameterSetter parameterSetter =
        new SqlParameterSetter() {

          @Override
          public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
              throws SQLException {
            return parameter.getClass().equals(java.net.Inet4Address.class);
          }

          @Override
          public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
              throws SQLException {
            stmt.setString(parameterIndex, parameter.toString());
          }
        };
    SormContext context =
        SormContext.builder()
            .addColumnValueToJavaObjectConverter(columnValueConverter)
            .addSqlParameterSetter(parameterSetter)
            .build();

    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables(context);

    sorm.insert(SormTestUtils.GUEST_ALICE);
    sorm.selectAll(Guest.class);

    try {
      sorm.readFirst(Guest.class, "select * from guests where name=?", Inet4Address.getLocalHost());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testConvertToWithSQLException() throws SQLException {
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.getString(anyInt())).thenThrow(new SQLException("Test exception"));

    DefaultColumnValueToJavaObjectConverters converters =
        new DefaultColumnValueToJavaObjectConverters();

    assertThrows(
        SormException.class,
        () ->
            converters.convertTo(
                mockResultSet, 1, JDBCType.VARCHAR.getVendorTypeNumber(), String.class));
  }

  public enum TestEnum {
    VALUE1,
    VALUE2
  }

  @Test
  void testUnsupportedTypeConversion() throws SQLException {
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.getObject(anyInt(), eq(TestEnum.class))).thenReturn(null);

    DefaultColumnValueToJavaObjectConverters converters =
        new DefaultColumnValueToJavaObjectConverters();

    assertThrows(
        SormException.class,
        () ->
            converters.convertTo(
                mockResultSet, 1, JDBCType.OTHER.getVendorTypeNumber(), TestEnum.class));
  }

  @Test
  void testArrayConversionException() throws SQLException {
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.getArray(anyInt())).thenThrow(new SQLException("Test exception"));

    DefaultColumnValueToJavaObjectConverters converters =
        new DefaultColumnValueToJavaObjectConverters();

    assertThrows(
        SormException.class,
        () ->
            converters.convertTo(
                mockResultSet, 1, JDBCType.ARRAY.getVendorTypeNumber(), int[].class));
  }

  private DefaultColumnValueToJavaObjectConverter converter;
  private ResultSet resultSet;

  @BeforeEach
  void setUp() {
    converter = new DefaultColumnValueToJavaObjectConverter();
    resultSet = mock(ResultSet.class);
  }

  @Test
  void testConvertChar() throws SQLException {
    when(resultSet.getString(1)).thenReturn("A");
    char result = converter.convertTo(resultSet, 1, 0, char.class);
    assertThat(result).isEqualTo('A');
  }

  @Test
  void testConvertCharEmptyString() throws SQLException {
    when(resultSet.getString(1)).thenReturn("");
    char result = converter.convertTo(resultSet, 1, 0, char.class);
    assertThat(result).isEqualTo('\0');
  }

  @Test
  void testConvertCharNull() throws SQLException {
    when(resultSet.getString(1)).thenReturn(null);
    char result = converter.convertTo(resultSet, 1, 0, char.class);
    assertThat(result).isEqualTo('\0');
  }

  @Test
  void testConvertCharacter() throws SQLException {
    when(resultSet.getString(1)).thenReturn("B");
    Character result = converter.convertTo(resultSet, 1, 0, Character.class);
    assertThat(result).isEqualTo('B');
  }

  @Test
  void testConvertCharacterEmptyString() throws SQLException {
    when(resultSet.getString(1)).thenReturn("");
    Character result = converter.convertTo(resultSet, 1, 0, Character.class);
    assertThat(result).isNull();
  }

  @Test
  void testConvertCharacterNull() throws SQLException {
    when(resultSet.getString(1)).thenReturn(null);
    Character result = converter.convertTo(resultSet, 1, 0, Character.class);
    assertThat(result).isNull();
  }

  @Test
  void testConvertInteger() throws SQLException {
    when(resultSet.getInt(1)).thenReturn(42);
    int result = converter.convertTo(resultSet, 1, 0, int.class);
    assertThat(result).isEqualTo(42);
  }

  @Test
  void testConvertLong() throws SQLException {
    when(resultSet.getLong(1)).thenReturn(123456789L);
    long result = converter.convertTo(resultSet, 1, 0, long.class);
    assertThat(result).isEqualTo(123456789L);
  }

  @Test
  void testConvertDouble() throws SQLException {
    when(resultSet.getDouble(1)).thenReturn(3.14);
    double result = converter.convertTo(resultSet, 1, 0, double.class);
    assertThat(result).isEqualTo(3.14);
  }

  @Test
  void testConvertBooleanTrue() throws SQLException {
    when(resultSet.getBoolean(1)).thenReturn(true);
    boolean result = converter.convertTo(resultSet, 1, 0, boolean.class);
    assertThat(result).isTrue();
  }

  @Test
  void testConvertBooleanFalse() throws SQLException {
    when(resultSet.getBoolean(1)).thenReturn(false);
    boolean result = converter.convertTo(resultSet, 1, 0, boolean.class);
    assertThat(result).isFalse();
  }

  @Test
  void testConvertSqlDate() throws SQLException {
    Date date = Date.valueOf("2024-01-01");
    when(resultSet.getDate(1)).thenReturn(date);
    Date result = converter.convertTo(resultSet, 1, 0, Date.class);
    assertThat(result).isEqualTo(date);
  }

  @Test
  void testConvertSqlTimestamp() throws SQLException {
    Timestamp timestamp = Timestamp.valueOf("2024-01-01 12:34:56");
    when(resultSet.getTimestamp(1)).thenReturn(timestamp);
    Timestamp result = converter.convertTo(resultSet, 1, 0, Timestamp.class);
    assertThat(result).isEqualTo(timestamp);
  }

  @Test
  void testConvertInputStream() throws SQLException {
    InputStream stream = new ByteArrayInputStream("test".getBytes());
    when(resultSet.getBinaryStream(1)).thenReturn(stream);
    InputStream result = converter.convertTo(resultSet, 1, 0, InputStream.class);
    assertThat(result).isNotNull();
  }

  @Test
  void testConvertReader() throws SQLException {
    Reader reader = new StringReader("test");
    when(resultSet.getCharacterStream(1)).thenReturn(reader);
    Reader result = converter.convertTo(resultSet, 1, 0, Reader.class);
    assertThat(result).isNotNull();
  }

  @Test
  void testConvertBlob() throws SQLException {
    Blob blob = mock(Blob.class);
    when(resultSet.getBlob(1)).thenReturn(blob);
    Blob result = converter.convertTo(resultSet, 1, 0, Blob.class);
    assertThat(result).isEqualTo(blob);
  }

  @Test
  void testConvertClob() throws SQLException {
    Clob clob = mock(Clob.class);
    when(resultSet.getClob(1)).thenReturn(clob);
    Clob result = converter.convertTo(resultSet, 1, 0, Clob.class);
    assertThat(result).isEqualTo(clob);
  }

  @Test
  void testConvertUUID() throws SQLException {
    UUID uuid = UUID.randomUUID();
    when(resultSet.getObject(1, UUID.class)).thenReturn(uuid);
    UUID result = converter.convertTo(resultSet, 1, 0, UUID.class);
    assertThat(result).isEqualTo(uuid);
  }

  @Test
  void testConvertEnum() throws SQLException {
    when(resultSet.getString(1)).thenReturn("VALUE1");
    TestEnum result = converter.convertTo(resultSet, 1, 0, TestEnum.class);
    assertThat(result).isEqualTo(TestEnum.VALUE1);
  }

  @Test
  void testConvertInvalidEnum() throws SQLException {
    when(resultSet.getString(1)).thenReturn("INVALID");
    assertThatThrownBy(() -> converter.convertTo(resultSet, 1, 0, TestEnum.class))
        .isInstanceOf(SormException.class);
  }

  @Test
  void testConvertByteArray() throws SQLException {
    byte[] data = {1, 2, 3, 4, 5};
    when(resultSet.getBytes(1)).thenReturn(data);
    byte[] result = converter.convertTo(resultSet, 1, 0, byte[].class);
    assertThat(result).isEqualTo(data);
  }

  @Test
  void testConvertObject() throws SQLException {
    Object obj = "test";
    when(resultSet.getObject(1)).thenReturn(obj);
    Object result = converter.convertTo(resultSet, 1, 0, Object.class);
    assertThat(result).isEqualTo(obj);
  }

  @Test
  void testConvertByte() throws SQLException {
    when(resultSet.getByte(1)).thenReturn((byte) 100);
    byte result = converter.convertTo(resultSet, 1, 0, byte.class);
    assertThat(result).isEqualTo((byte) 100);
  }

  @Test
  void testConvertShort() throws SQLException {
    when(resultSet.getShort(1)).thenReturn((short) 32000);
    short result = converter.convertTo(resultSet, 1, 0, short.class);
    assertThat(result).isEqualTo((short) 32000);
  }

  @Test
  void testConvertFloat() throws SQLException {
    when(resultSet.getFloat(1)).thenReturn(3.14f);
    float result = converter.convertTo(resultSet, 1, 0, float.class);
    assertThat(result).isEqualTo(3.14f);
  }

  @Test
  void testConvertByteWithString() throws SQLException {
    when(resultSet.getObject(1)).thenReturn("invalid");
    assertThat(converter.convertTo(resultSet, 1, 0, byte.class));
  }

  @Test
  void testConvertShortWithInvalidType() throws SQLException {
    when(resultSet.getObject(1)).thenReturn("invalid");
    assertEquals(converter.convertTo(resultSet, 1, 0, short.class), (short) 0);
  }

  @Test
  void testConvertFloatWithInvalidType() throws SQLException {
    when(resultSet.getObject(1)).thenReturn("invalid");
    assertThat(converter.convertTo(resultSet, 1, 0, float.class));
  }

  @Test
  void testConvertByteWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    byte result = converter.convertTo(resultSet, 1, 0, byte.class);
    assertEquals((byte) 0, result);
  }

  @Test
  void testConvertShortWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    short result = converter.convertTo(resultSet, 1, 0, short.class);
    assertEquals((short) 0, result);
  }

  @Test
  void testConvertFloatWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    float result = converter.convertTo(resultSet, 1, 0, float.class);
    assertEquals(0.0f, result);
  }

  @Test
  void testConvertIntWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    int result = converter.convertTo(resultSet, 1, 0, int.class);
    assertEquals(0, result);
  }

  @Test
  void testConvertLongWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    long result = converter.convertTo(resultSet, 1, 0, long.class);
    assertEquals(0L, result);
  }

  @Test
  void testConvertDoubleWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    double result = converter.convertTo(resultSet, 1, 0, double.class);
    assertEquals(0.0, result);
  }

  @Test
  void testConvertBooleanWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    boolean result = converter.convertTo(resultSet, 1, 0, boolean.class);
    assertEquals(false, result);
  }

  @Test
  void testConvertCharWithNull() throws SQLException {
    when(resultSet.getObject(1)).thenReturn(null);
    char result = converter.convertTo(resultSet, 1, 0, char.class);
    assertEquals('\0', result);
  }

  @Test
  void testConvertString() throws SQLException {
    when(resultSet.getString(1)).thenReturn("Test String");
    String result = converter.convertTo(resultSet, 1, 0, String.class);
    assertThat(result).isEqualTo("Test String");
  }

  @Test
  void testConvertStringWithNull() throws SQLException {
    when(resultSet.getString(1)).thenReturn(null);
    String result = converter.convertTo(resultSet, 1, 0, String.class);
    assertThat(result).isNull();
  }

  @Test
  void testConvertBigDecimal() throws SQLException {
    BigDecimal decimal = new BigDecimal("123.45");
    when(resultSet.getBigDecimal(1)).thenReturn(decimal);
    BigDecimal result = converter.convertTo(resultSet, 1, 0, BigDecimal.class);
    assertThat(result).isEqualTo(decimal);
  }

  @Test
  void testConvertBigDecimalWithNull() throws SQLException {
    when(resultSet.getBigDecimal(1)).thenReturn(null);
    BigDecimal result = converter.convertTo(resultSet, 1, 0, BigDecimal.class);
    assertThat(result).isNull();
  }

  @Test
  void testConvertSqlTime() throws SQLException {
    Time time = Time.valueOf("12:34:56");
    when(resultSet.getTime(1)).thenReturn(time);
    Time result = converter.convertTo(resultSet, 1, 0, Time.class);
    assertThat(result).isEqualTo(time);
  }

  @Test
  void testConvertSqlTimeWithNull() throws SQLException {
    when(resultSet.getTime(1)).thenReturn(null);
    Time result = converter.convertTo(resultSet, 1, 0, Time.class);
    assertThat(result).isNull();
  }

  @Test
  void testConvertJsonByte() throws SQLException {
    byte[] jsonData = {123, 34, 110, 97, 109, 101, 34, 58, 34, 65, 108, 105, 99, 101, 34, 125};
    when(resultSet.getBytes(1)).thenReturn(jsonData);
    JsonByte result = converter.convertTo(resultSet, 1, 0, JsonByte.class);
    assertThat(result.bytes()).isEqualTo(jsonData);
  }

  @Test
  void testConvertJsonByteWithNull() throws SQLException {
    when(resultSet.getBytes(1)).thenReturn(null);
    JsonByte result = converter.convertTo(resultSet, 1, 0, JsonByte.class);
    assertThat(result.bytes()).isNull();
  }

  @Test
  void testConvertGeometryString() throws SQLException {
    when(resultSet.getString(1)).thenReturn("POINT(30 10)");
    GeometryText result = converter.convertTo(resultSet, 1, 0, GeometryText.class);
    assertThat(result.text()).isEqualTo("POINT(30 10)");
  }

  @Test
  void testConvertGeometryStringWithNull() throws SQLException {
    when(resultSet.getString(1)).thenReturn(null);
    GeometryText result = converter.convertTo(resultSet, 1, 0, GeometryText.class);
    assertThat(result.text()).isNull();
  }
}
