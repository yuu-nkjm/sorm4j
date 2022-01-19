package repackage.net.sf.persist.tests.engine.postgresql;


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
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
import java.util.Objects;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.mapping.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.SqlParameterSetter;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.postgresql.util.PGobject;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;

public class TestPostgreSQLSqlMapper {
  private static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private static DataSource dataSource;
  private static SormContext context;

  @BeforeAll
  static void beforAll() {
    dataSource = DbEngineTestUtils.getDataSource(TestPostgreSQLSqlMapper.class,
        "jdbc:h2:mem:postgre;MODE=PostgreSQL");
    DbEngineTestUtils.executeSql(dataSource, TestPostgreSQLSqlMapper.class, "sql-mapper-test.sql");



    ColumnValueToJavaObjectConverter<PGobject> columnValueConverter =
        ((resultSet, columnIndex, columnType, toType) -> {
          return PGobject.class.cast(resultSet.getObject(columnIndex));
        });



    SqlParameterSetter parameterSetter = ((stmt, parameterIndex, parameter) -> {
      PGobject pg = new PGobject();
      pg.setType("inet");
      pg.setValue(((InetAddress) parameter).getHostAddress());
      stmt.setObject(parameterIndex, pg);
    });

    context = SormContext.builder()
        .setColumnValueToJavaObjectConverter(new DefaultColumnValueToJavaObjectConverters(
            Map.of(PGobject.class, columnValueConverter)))
        .setSqlParametersSetter(
            new DefaultSqlParametersSetter(Map.of(java.net.InetAddress.class, parameterSetter)))
        .build();

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
    try (Connection conn = dataSource.getConnection();
        OrmConnection c = OrmConnection.of(conn, context);) {

      log.info(c.readFirst(RowMap.class, "select * from sql_mapper_test"));
      doTest(c, "c_boolean by boolean", "c_boolean", true);
      doTest(c, "c_integer by int", "c_integer", 1);
      doTest(c, "c_integer by BigDecimal", "c_integer", new BigDecimal("1"));
      doTest(c, "c_decimal by BigDecimal", "c_decimal", new BigDecimal("0.500"));
      doTest(c, "c_double by double", "c_double", 0.5);
      doTest(c, "c_double by BigDecimal", "c_double", new BigDecimal("0.5"));
      doTest(c, "testName", "c_varchar", "varchar");
      doTest(c, "testName", "c_text", "long long text");
      doTest(c, "c_bytea by byte[]", "c_bytea",
          new byte[] {(byte) 0xde, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
      doTest(c, "testName", "c_uuid", UUID.fromString("33ee757a-19b3-45dc-be79-f1d65ac5d1a4"));
      doTest(c, "testName", "c_date", localDate);
      doTest(c, "testName", "c_date", Date.valueOf(localDate));
      doTest(c, "testName", "c_time", localTime);
      doTest(c, "testName", "c_time", Time.valueOf(localTime));
      doTest(c, "c_timez by offsetTime", "c_timetz", offsetTime);
      doTest(c, "testName", "c_timestamp", localDateTime);
      doTest(c, "testName", "c_timestamp", Timestamp.valueOf(localDateTime));
      doTest(c, "c_timestamptz by OffsetDateTime", "c_timestamptz", offsetDateTime);
      // doTest(c, "testName", "c_inet_ipv4", InetAddress.getByName("192.168.1.1"));
      // doTest(c, "testName","c_inet_ipv6", InetAddress.getByName("::1"));
      // doTest(c, "testName", "c_url", new URL("https://example.com"));

      doTest(c, "c_integer_array by Integer[]", "c_integer_array", new Integer[] {1, 2, 3});
      doTest(c, "c_integer_array by int[]", "c_integer_array", new int[] {1, 2, 3});

      doTest(c, "c_integer_array by List", "c_integer_array", List.of(1, 2, 3));

      doTest(c, "c_varchar_array by String[]", "c_varchar_array", new String[] {"A", "B", "C"});
      doTest(c, "c_varchar_array by List", "c_varchar_array", List.of("A", "B", "C"));


      log.debug("Array test --------");
      doTestInClause(c, "c integer by List", "c_integer", List.of(1, 2, 3));
      doTestInClause(c, "c integer by Integer[]", "c_integer", new Integer[] {1, 2, 3});
      doTestInClause(c, "c integer by int[]", "c_integer", new int[] {1, 2, 3});
      doTestInClause(c, "c_varchar by List", "c_varchar", List.of("integer", "varchar", "text"));
      doTestInClause(c, "c_varchar by String[]", "c_varchar",
          new String[] {"integer", "varchar", "text"});
    }
  }

  private void doTest(OrmConnection c, String testName, String column, Object param) {
    bindToSqlTest(c, testName, column, param);
    convertResultSetToJavaObjectTest(c, testName, column, param);
  }

  private void convertResultSetToJavaObjectTest(OrmConnection c, String testName, String column,
      Object param) {
    String messagePrefix = "map: " + column + "(" + param.getClass() + ") ";
    try {
      Class<?> clazz =
          List.class.isAssignableFrom(param.getClass()) ? List.class : param.getClass();
      Object retFromDb = c.readFirst(clazz, "SELECT " + column + " FROM sql_mapper_test");
      if (equals(retFromDb, param)) {
        log.debug("[" + testName + "] " + messagePrefix + "success => " + retFromDb);
      } else {
        log.error("[" + testName + "] " + messagePrefix + "fail => " + retFromDb);
      }
    } catch (Exception e) {
      log.error("[" + testName + "] " + messagePrefix + "fail => " + e.getMessage());
      log.error(e, e);
    }
  }

  private boolean equals(Object retFromDb, Object param) throws SQLException {

    if (retFromDb.getClass().isArray() && param.getClass().isArray()) {
      return equalsArray(retFromDb, param);

    } else {
      return param.equals(retFromDb);
    }
  }

  public static boolean equalsArray(Object arr1, Object arr2) {
    if (arr1 == null || arr2 == null) {
      return false;
    }
    if (!arr1.getClass().isArray() || !arr2.getClass().isArray()) {
      throw new IllegalArgumentException(ParameterizedStringUtils
          .newString("args should be array. arr1={}, arr2={}", arr1.getClass(), arr2.getClass()));
    }
    int l1 = Array.getLength(arr1);

    if (l1 != Array.getLength(arr2)) {
      return false;
    }

    for (int i = 0; i < l1; i++) {
      Object e1 = Array.get(arr1, i);
      Object e2 = Array.get(arr2, i);
      if (!Objects.equals(e1, e2)) {
        return false;
      }
    }
    return true;
  }

  private void bindToSqlTest(OrmConnection c, String testName, String column, Object param) {
    String messagePrefix = "bind: " + column + "(" + param.getClass() + ") ";
    try {
      Map<String, Object> ret = c.readFirst(RowMap.class,
          "SELECT " + column + " FROM sql_mapper_test WHERE " + column + "=?", param);
      if (ret != null) {
        log.debug("[" + testName + "] " + messagePrefix + "success => " + ret);
      } else {
        log.error("[" + testName + "] " + messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      log.error("[" + testName + "] " + messagePrefix + "fail => " + e.getMessage());
      // e.printStackTrace();
    }

  }

  private void doTestInClause(OrmConnection c, String testName, String column, Object param) {
    bindInClauseTest(c, testName, column, param);
  }

  private void bindInClauseTest(OrmConnection c, String testName, String column, Object param) {
    String messagePrefix = "bindIn: " + column + "(" + param.getClass() + ") ";
    try {
      ParameterizedSql statement = OrderedParameterSql
          .from("SELECT " + column + " FROM sql_mapper_test WHERE " + column + " in(<?>)")
          .addParameter(param).parse();
      Map<String, Object> ret = c.readFirst(RowMap.class, statement);
      if (ret != null) {
        log.debug("[" + testName + "] " + messagePrefix + "success => " + ret);
      } else {
        log.error("[" + testName + "] " + messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      log.error("[" + testName + "] " + messagePrefix + "fail => " + e.getMessage());
    }
  }



}
