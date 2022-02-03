package org.nkjmlab.sorm4j.context;

import static org.junit.jupiter.api.Assertions.*;
import java.net.Inet4Address;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DefaultColumnValueToJavaObjectConvertersTest {

  @Test
  void testConvertTo() {
    ColumnValueToJavaObjectConverter<String> columnValueConverter =
        ((resultSet, columnIndex, columnType, toType) -> {
          return resultSet.getString(columnIndex).toString();
        });

    SqlParameterSetter parameterSetter = ((stmt, parameterIndex, parameter) -> {
      stmt.setString(parameterIndex, parameter.toString());
    });

    PreparedStatementSupplier supplier = new PreparedStatementSupplier() {
      @Override
      public PreparedStatement prepareStatement(Connection connection, String sql,
          String[] autoGeneratedColumnsArray) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedColumnsArray);
      }

      @Override
      public PreparedStatement prepareStatement(Connection connection, String sql)
          throws SQLException {
        return connection.prepareStatement(sql);
      }
    };
    SormContext context = SormContext.builder()
        .setColumnValueToJavaObjectConverter(new DefaultColumnValueToJavaObjectConverters(
            Map.of(String.class, columnValueConverter)))
        .setSqlParametersSetter(
            new DefaultSqlParametersSetter(Map.of(java.net.Inet4Address.class, parameterSetter)))
        .setPreparedStatementSupplier(supplier).build();

    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables(context);

    sorm.insert(SormTestUtils.GUEST_ALICE);
    sorm.selectAll(Guest.class);

    try {
      sorm.readFirst(Guest.class, "select * from guests where name=?", Inet4Address.getLocalHost());
    } catch (Exception e) {
      fail();
    }


  }

}