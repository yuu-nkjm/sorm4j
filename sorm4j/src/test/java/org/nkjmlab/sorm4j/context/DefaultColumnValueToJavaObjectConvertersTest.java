package org.nkjmlab.sorm4j.context;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.Inet4Address;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnValueToJavaObjectConverters;
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
}
