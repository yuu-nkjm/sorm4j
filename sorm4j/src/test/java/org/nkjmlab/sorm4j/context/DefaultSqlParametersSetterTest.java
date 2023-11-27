package org.nkjmlab.sorm4j.context;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.test.common.TestUtils;

class DefaultSqlParametersSetterTest {

  @Test
  void testArray() {
    Sorm sorm = createSormWithNewDatabaseAndCreateTables();
    try (Connection conn = sorm.getJdbcConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from guests where id=?")) {

      assertThat(toSqlArray("boolean", conn, TestUtils.PRIMITIVE_BOOLEAN_ARRAY).getBaseType())
          .isEqualTo(JDBCType.BOOLEAN.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Boolean", conn, TestUtils.BOOLEAN_ARRAY).getBaseType())
          .isEqualTo(JDBCType.BOOLEAN.getVendorTypeNumber());

      assertThat(toSqlArray("char", conn, TestUtils.PRIMITIVE_CHAR_ARRAY).getBaseType())
          .isEqualTo(JDBCType.CHAR.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Character", conn, TestUtils.CHARRACTER_ARRAY).getBaseType())
          .isEqualTo(JDBCType.CHAR.getVendorTypeNumber());

      assertThat(toSqlArray("byte", conn, TestUtils.PRIMITIVE_BYTE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TINYINT.getVendorTypeNumber());
      assertThat(toSqlArray("java.lang.Byte", conn, TestUtils.BYTE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TINYINT.getVendorTypeNumber());

      assertThat(toSqlArray("short", conn, TestUtils.PRIMITIVE_SHORT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.SMALLINT.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Short", conn, TestUtils.SHORT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.SMALLINT.getVendorTypeNumber());

      assertThat(toSqlArray("int", conn, TestUtils.PRIMITIVE_INT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.INTEGER.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Integer", conn, TestUtils.INTEGER_ARRAY).getBaseType())
          .isEqualTo(JDBCType.INTEGER.getVendorTypeNumber());

      assertThat(toSqlArray("long", conn, TestUtils.PRIMITIVE_LONG_ARRAY).getBaseType())
          .isEqualTo(JDBCType.BIGINT.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Long", conn, TestUtils.LONG_ARRAY).getBaseType())
          .isEqualTo(JDBCType.BIGINT.getVendorTypeNumber());

      assertThat(toSqlArray("float", conn, TestUtils.PRIMITIVE_FLOAT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.REAL.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Float", conn, TestUtils.FLOAT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.REAL.getVendorTypeNumber());

      assertThat(toSqlArray("double", conn, TestUtils.PRIMITIVE_DOUBLE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.DOUBLE.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Double", conn, TestUtils.DOUBLE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.DOUBLE.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.String", conn, TestUtils.STRING_ARRAY).getBaseType())
          .isEqualTo(JDBCType.VARCHAR.getVendorTypeNumber());

      assertThat(
              toSqlArray("java.math.BigDecimal", conn, TestUtils.BIG_DECIMAL_ARRAY).getBaseType())
          .isEqualTo(JDBCType.NUMERIC.getVendorTypeNumber());

      assertThat(toSqlArray("java.sql.Date", conn, TestUtils.DATE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.DATE.getVendorTypeNumber());
      assertThat(toSqlArray("java.sql.Time", conn, TestUtils.TIME_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIME.getVendorTypeNumber());
      assertThat(toSqlArray("java.sql.Timestamp", conn, TestUtils.TIMESTAMP_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIMESTAMP.getVendorTypeNumber());

      assertThat(toSqlArray("java.time.Instant", conn, TestUtils.INSTANT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIMESTAMP_WITH_TIMEZONE.getVendorTypeNumber());

      assertThat(toSqlArray("java.time.Instant", conn, TestUtils.INSTANT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIMESTAMP_WITH_TIMEZONE.getVendorTypeNumber());

      assertThat(
              toSqlArray("java.time.OffsetTime", conn, TestUtils.OFFSET_TIME_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIME_WITH_TIMEZONE.getVendorTypeNumber());

      assertThat(
              toSqlArray("java.time.OffsetDateTime", conn, TestUtils.OFFSET_DATE_TIME_ARRAY)
                  .getBaseType())
          .isEqualTo(JDBCType.TIMESTAMP_WITH_TIMEZONE.getVendorTypeNumber());

      assertThat(toSqlArray("java.time.LocalDate", conn, TestUtils.LOCAL_DATE_ARRAY).getBaseType())
          .isEqualTo(JDBCType.DATE.getVendorTypeNumber());

      assertThat(toSqlArray("java.time.LocalTime", conn, TestUtils.LOCAL_TIME_ARRAY).getBaseType())
          .isEqualTo(JDBCType.TIME.getVendorTypeNumber());

      assertThat(
              toSqlArray("java.time.LocalDateTime", conn, TestUtils.LOCAL_DATE_TIME_ARRAY)
                  .getBaseType())
          .isEqualTo(JDBCType.TIMESTAMP.getVendorTypeNumber());

      assertThat(toSqlArray("java.lang.Object", conn, TestUtils.OBJECT_ARRAY).getBaseType())
          .isEqualTo(JDBCType.JAVA_OBJECT.getVendorTypeNumber());

    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Test
  void testSetParameters() {
    Sorm sorm = createSormWithNewDatabaseAndCreateTables();

    sorm.executeUpdate(
        "CREATE TABLE TA (id int auto_increment primary key, arry " + "INTEGER" + " ARRAY[10])");

    sorm.readFirst(RowMap.class, "select * from TA where arry=?", new boolean[] {true, false});
    sorm.readFirst(
        RowMap.class, "select * from TA where arry=?", (Object) new Boolean[] {true, false});
    sorm.readFirst(RowMap.class, "select * from TA where arry=?", new double[] {0.1d});
    sorm.readFirst(RowMap.class, "select * from TA where arry=?", (Object) new Double[] {0.1d});

    DefaultSqlParametersSetter setter = new DefaultSqlParametersSetter();
    try (Connection conn = sorm.getJdbcConnection();
        PreparedStatement pstmt = conn.prepareStatement("select * from guests where id=?")) {

      setter.setParameters(pstmt, (Object[]) null);
      setter.setParameters(pstmt, new Object[] {});

      setter.setParameters(pstmt, Instant.now());
      setter.setParameters(
          pstmt, DefaultSqlParametersSetterTest.class.getResourceAsStream("log4j2.xml"));
      setter.setParameters(pstmt, new StringReader("a"));

    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }
}
