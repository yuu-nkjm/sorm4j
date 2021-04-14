package repackage.net.sf.persist.tests.engine.postgresql;


import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.extension.ColumnValueConverter;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;
import org.nkjmlab.sorm4j.extension.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.extension.ParameterSetter;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.postgresql.util.PGobject;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;

public class TestPostgreSQLSqlMapper {
  private static DataSource dataSource;

  @BeforeAll
  static void beforAll() {
    dataSource = DbEngineTestUtils.getDataSource(TestPostgreSQLSqlMapper.class,
        "jdbc:h2:mem:postgre;MODE=PostgreSQL");
    DbEngineTestUtils.executeSql(dataSource, TestPostgreSQLSqlMapper.class, "sql-mapper-test.sql");

    SqlParametersSetter ps = new DefaultSqlParametersSetter(new ParameterSetter() {

      @Override
      public boolean isApplicable(SormOptions options, PreparedStatement stmt, int parameterIndex,
          Class<?> parameterClass, Object parameter) {
        return java.net.InetAddress.class.isAssignableFrom(parameterClass);
      }

      @Override
      public void setParameter(SormOptions options, PreparedStatement stmt, int parameterIndex,
          Class<?> parameterClass, Object parameter) throws SQLException {
        PGobject pg = new PGobject();
        pg.setType("inet");
        pg.setValue(((InetAddress) parameter).getHostAddress());
        stmt.setObject(parameterIndex, pg);

      }

    });

    ResultSetConverter rsc = new DefaultResultSetConverter(new ColumnValueConverter() {

      @Override
      public boolean isApplicable(SormOptions options, ResultSet resultSet, int column,
          int columnType, Class<?> toType) {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public Object convertTo(SormOptions options, ResultSet resultSet, int column, int columnType,
          Class<?> toType) {
        // TODO Auto-generated method stub
        return null;
      }
    });
    SormFactory
        .updateDefaultConfig(conf -> conf.setSqlParametersSetter(ps).setResultSetConverter(rsc));
  }


  /**
   * @see https://github.com/orangain/compare-sql-mappers/
   *
   * @throws SQLException
   */
  private static final ZoneOffset JST_OFFSET = ZoneOffset.of("+09:00");
  private static final LocalDate localDate = LocalDate.of(2019, 9, 27);
  private static final LocalTime localTime = LocalTime.of(13, 23);
  private static final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
  private static final OffsetTime offsetTime = OffsetTime.of(localTime, JST_OFFSET);
  private static final OffsetDateTime offsetDateTime =
      OffsetDateTime.of(localDate, localTime, JST_OFFSET);


  @Test
  public void testMapTest() throws SQLException, MalformedURLException, UnknownHostException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection c = SormFactory.toOrmConnection(conn);
      doTest(c, "c_boolean", true);
      doTest(c, "c_integer", 1);
      doTest(c, "c_integer", new BigDecimal("1"));
      doTest(c, "c_decimal", new BigDecimal("0.5"));
      doTest(c, "c_double", 0.5);
      doTest(c, "c_double", new BigDecimal("0.5"));
      doTest(c, "c_varchar", "varchar");
      doTest(c, "c_text", "long long text");
      doTest(c, "c_bytea", new byte[] {(byte) 0xde, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
      doTest(c, "c_uuid", UUID.fromString("33ee757a-19b3-45dc-be79-f1d65ac5d1a4"));
      doTest(c, "c_date", localDate);
      doTest(c, "c_date", Date.valueOf(localDate));
      doTest(c, "c_time", localTime);
      doTest(c, "c_time", Time.valueOf(localTime));
      doTest(c, "c_timetz", offsetTime);
      doTest(c, "c_timestamp", localDateTime);
      doTest(c, "c_timestamp", Timestamp.valueOf(localDateTime));
      doTest(c, "c_timestamptz", offsetDateTime);
      doTest(c, "c_inet_ipv4", InetAddress.getByName("192.168.1.1"));
      doTest(c, "c_inet_ipv6", InetAddress.getByName("::1"));
      doTest(c, "c_url", new URL("https://example.com"));
      doTest(c, "c_integer_array", new Integer[] {1, 2, 3});
      doTest(c, "c_integer_array", new int[] {1, 2, 3});
      doTest(c, "c_varchar_array", new String[] {"A", "B", "C"});

      doTest(c, "c_integer_array", List.of(1, 2, 3));
      doTest(c, "c_varchar_array", List.of("A", "B", "C"));


      System.out.println("--------");
      doTestIn(c, "c_integer", List.of(1, 2, 3));
      doTestIn(c, "c_integer", new Integer[] {1, 2, 3});
      doTestIn(c, "c_integer", new int[] {1, 2, 3});
      doTestIn(c, "c_varchar", List.of("integer", "varchar", "text"));
      doTestIn(c, "c_varchar", new String[] {"integer", "varchar", "text"});
    }
  }

  private void doTest(OrmConnection c, String column, Object param) {
    bindTest(c, column, param);
    mapTest(c, column, param);
  }

  private void mapTest(OrmConnection c, String column, Object param) {
    String messagePrefix = "map: " + column + "(" + param.getClass() + ") ";
    try {
      Object ret = c.readFirst(param.getClass(), "SELECT " + column + " FROM sql_mapper_test");
      if (equals(ret, param)) {
        System.out.println(messagePrefix + "success => " + ret);
      } else {
        System.err.println(messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      System.err.println(messagePrefix + "fail => " + e.getMessage());
    }
  }

  private boolean equals(Object ret, Object param) throws SQLException {
    if (ret.getClass().isArray()) {
      return (param.getClass().isArray() ? Arrays.asList(param) : param).equals(Arrays.asList(ret));
    } else if (java.sql.Array.class.isAssignableFrom(ret.getClass())) {
      java.sql.Array arr = (java.sql.Array) ret;
      return (param.getClass().isArray() ? Arrays.asList(param) : param)
          .equals(Arrays.asList(arr.getArray()));
    } else {
      return param.equals(ret);
    }
  }

  private void bindTest(OrmConnection c, String column, Object param) {
    String messagePrefix = "bind: " + column + "(" + param.getClass() + ") ";
    try {
      Map<String, Object> ret = c
          .readMapFirst("SELECT " + column + " FROM sql_mapper_test WHERE " + column + "=?", param);
      if (ret != null) {
        System.out.println(messagePrefix + "success => " + ret);
      } else {
        System.err.println(messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      System.err.println(messagePrefix + "fail => " + e.getMessage());
      //e.printStackTrace();
    }

  }

  private void doTestIn(OrmConnection c, String column, Object param) {
    bindInTest(c, column, param);
  }

  private void bindInTest(OrmConnection c, String column, Object param) {
    String messagePrefix = "bindIn: " + column + "(" + param.getClass() + ") ";
    try {
      SqlStatement statement = OrderedParameterSql
          .from("SELECT " + column + " FROM sql_mapper_test WHERE " + column + " in(<?>)")
          .addParameter(param).toSqlStatement();
      Map<String, Object> ret = c.readMapFirst(statement);
      if (ret != null) {
        System.out.println(messagePrefix + "success => " + ret);
      } else {
        System.out.println(messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      System.err.println(messagePrefix + "fail => " + e.getMessage());
    }
  }



}
