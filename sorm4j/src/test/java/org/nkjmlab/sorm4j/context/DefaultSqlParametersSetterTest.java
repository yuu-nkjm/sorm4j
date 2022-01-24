package org.nkjmlab.sorm4j.context;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.TestUtils;

class DefaultSqlParametersSetterTest {

  @Test
  void testSetParameters() {
    SORM.executeUpdate(
        "CREATE TABLE TA (id int auto_increment primary key, arry " + "INTEGER" + " ARRAY[10])");

    SORM.readFirst(RowMap.class, "select * from TA where arry=?", new boolean[] {true, false});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?",
        (Object) new Boolean[] {true, false});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?", new double[] {0.1d});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?", (Object) new Double[] {0.1d});

    DefaultSqlParametersSetter setter = new DefaultSqlParametersSetter();
    try (Connection conn = SormTestUtils.createDataSourceH2().getConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from guests where id=?")) {

      setter.setParameters(pstmt, (Object[]) null);
      setter.setParameters(pstmt, new Object[] {});

      assertThat(toSqlArray("boolean", conn, TestUtils.PRIMITIVE_BOOLEAN_ARRAY).getBaseType())
          .isEqualTo(JDBCType.BOOLEAN.getVendorTypeNumber());

      assertThat(toSqlArray("char", conn, TestUtils.PRIMITIVE_CHAR_ARRAY).getBaseType())
          .isEqualTo(JDBCType.CHAR.getVendorTypeNumber());

      assertThat(toSqlArray("byte", conn, TestUtils.PRIMITIVE_BYTE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TINYINT.getVendorTypeNumber());

      assertThat(toSqlArray("short", conn, TestUtils.PRIMITIVE_SHORT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.SMALLINT.getVendorTypeNumber());



    } catch (SQLException e) {
    }
  }
}
