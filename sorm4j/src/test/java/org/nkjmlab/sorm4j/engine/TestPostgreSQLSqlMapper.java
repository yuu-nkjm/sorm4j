package org.nkjmlab.sorm4j.engine;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverter;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.OrderedParameterSqlParser;
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



    ColumnValueToJavaObjectConverter columnValueConverter = new ColumnValueToJavaObjectConverter() {

      @Override
      public boolean test(Class<?> toType) {
        return toType.equals(PGobject.class);
      }

      @Override
      public Object convertTo(ResultSet resultSet, int columnIndex, int columnType, Class<?> toType)
          throws SQLException {
        return PGobject.class.cast(resultSet.getObject(columnIndex));
      }

    };



    SqlParameterSetter parameterSetter = new SqlParameterSetter() {

      @Override
      public boolean test(PreparedStatement stmt, int parameterIndex, Object parameter)
          throws SQLException {
        return parameter.getClass().equals(java.net.InetAddress.class);
      }

      @Override
      public void setParameter(PreparedStatement stmt, int parameterIndex, Object parameter)
          throws SQLException {
        PGobject pg = new PGobject();
        pg.setType("inet");
        pg.setValue(((InetAddress) parameter).getHostAddress());
        stmt.setObject(parameterIndex, pg);
      }
    };



    context = SormContext.builder()
        .setColumnValueToJavaObjectConverters(
            new DefaultColumnValueToJavaObjectConverters(columnValueConverter))
        .setSqlParametersSetter(new DefaultSqlParametersSetter(parameterSetter)).build();

  }


  /**
   * @see https://github.com/orangain/compare-sql-mappers/
   *
   * @throws SQLException
   */
  private static final ZoneOffset JST_OFFSET = ZoneOffset.of("+09:00");
  private static final LocalDate LOCAL_DATE = LocalDate.of(2019, 9, 27);
  private static final LocalTime LOCAL_TIME = LocalTime.of(13, 23);
  private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(LOCAL_DATE, LOCAL_TIME);
  private static final OffsetTime OFFSET_TIME = OffsetTime.of(LOCAL_TIME, JST_OFFSET);
  private static final OffsetDateTime OFFSET_DATE_TIME =
      OffsetDateTime.of(LOCAL_DATE, LOCAL_TIME, JST_OFFSET);
  private static final Instant INSTANT = OFFSET_DATE_TIME.toInstant();

  @Test
  public void testMapTest() throws SQLException, MalformedURLException, UnknownHostException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection c = OrmConnection.of(conn, context);) {

      // log.debug(c.readFirst(RowMap.class, "select * from sql_mapper_test"));
      doTest(c, "c_boolean by boolean", "c_boolean", true);
      doTest(c, "c_integer by int", "c_integer", 1);
      doTest(c, "c_integer by BigDecimal", "c_integer", new BigDecimal("1"));
      doTest(c, "c_decimal by BigDecimal", "c_decimal", new BigDecimal("0.500"));
      doTest(c, "c_double by double", "c_double", 0.5);
      doTest(c, "c_double by BigDecimal", "c_double", new BigDecimal("0.5"));
      doTest(c, "testName", "c_varchar", "varchar");
      doTest(c, "testName", "c_text", "long long text");
      doTest(c, "testName", "c_text", new StringReader("long long text"));
      doTest(c, "c_bytea by byte[]", "c_bytea",
          new byte[] {(byte) 0xde, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF});
      doTest(c, "c_bytea by byte[]", "c_bytea", new ByteArrayInputStream(
          new byte[] {(byte) 0xde, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}));
      doTest(c, "testName", "c_uuid", UUID.fromString("33ee757a-19b3-45dc-be79-f1d65ac5d1a4"));
      doTest(c, "testName", "c_date", LOCAL_DATE);
      doTest(c, "testName", "c_date", Date.valueOf(LOCAL_DATE));
      doTest(c, "testName", "c_time", LOCAL_TIME);
      doTest(c, "testName", "c_time", Time.valueOf(LOCAL_TIME));
      doTest(c, "c_timez by offsetTime", "c_timetz", OFFSET_TIME);
      doTest(c, "testName", "c_timestamp", LOCAL_DATE_TIME);
      doTest(c, "testName", "c_timestamp", Timestamp.valueOf(LOCAL_DATE_TIME));
      doTest(c, "c_timestamptz by OffsetDateTime", "c_timestamptz", OFFSET_DATE_TIME);
      doTest(c, "c_timestamptz by Instant", "c_timestamptz", INSTANT);
      // doTest(c, "testName", "c_inet_ipv4", InetAddress.getByName("192.168.1.1"));
      // doTest(c, "testName","c_inet_ipv6", InetAddress.getByName("::1"));
      // doTest(c, "testName", "c_url", new URL("https://example.com"));

      doTest(c, "c_integer_array by Integer[]", "c_integer_array", new Integer[] {1, 2, 3});
      doTest(c, "c_integer_array by int[]", "c_integer_array", new int[] {1, 2, 3});

      // doTest(c, "c_integer_array by List", "c_integer_array", List.of(1, 2, 3));

      doTest(c, "c_varchar_array by String[]", "c_varchar_array", new String[] {"A", "B", "C"});
      // doTest(c, "c_varchar_array by List", "c_varchar_array", List.of("A", "B", "C"));


      log.debug("Array test --------");
      // doTestInClause(c, "c integer by List", "c_integer", List.of(1, 2, 3));
      doTestInClause(c, "c integer by Integer[]", "c_integer", new Integer[] {1, 2, 3});
      doTestInClause(c, "c integer by int[]", "c_integer", new int[] {1, 2, 3});
      // doTestInClause(c, "c_varchar by List", "c_varchar", List.of("integer", "varchar", "text"));
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
      Class<?> clazz = toClass(param.getClass());
      Object retFromDb = c.readFirst(clazz, "SELECT " + column + " FROM sql_mapper_test");
      if (equals(retFromDb, param)) {
        // log.debug("[" + testName + "] " + messagePrefix + "success ret =>" + retFromDb);
      } else {
        log.error("[" + testName + "] " + messagePrefix + "fail ret => " + retFromDb + ", param => "
            + param);
      }
    } catch (Exception e) {
      log.error("Exception [" + testName + "] " + messagePrefix + "fail => " + e.getMessage());
      log.error(e, e);
    }
  }

  private Class<?> toClass(Class<?> clazz) {
    if (List.class.isAssignableFrom(clazz)) {
      return List.class;
    } else if (Reader.class.isAssignableFrom(clazz)) {
      return Reader.class;
    } else if (InputStream.class.isAssignableFrom(clazz)) {
      return InputStream.class;
    } else {
      return clazz;
    }
  }

  private boolean equals(Object retFromDb, Object param) throws Exception {

    if (retFromDb.getClass().isArray() && param.getClass().isArray()) {
      return equalsArray(retFromDb, param);
    } else if (param instanceof Instant && retFromDb instanceof OffsetDateTime) {
      OffsetDateTime odt = (OffsetDateTime) retFromDb;
      return odt.toInstant().equals(param);
    } else if (retFromDb instanceof Reader) {
      Reader r = (Reader) param;
      r.reset();
      return compare((Reader) retFromDb, (Reader) r);
    } else if (retFromDb instanceof InputStream) {
      InputStream is = (InputStream) param;
      is.reset();
      return compare((InputStream) retFromDb, is);
    } else {
      return param.equals(retFromDb);
    }
  }

  private static boolean compare(Reader r1, Reader r2) {
    char[] buf1 = new char[65535];
    char[] buf2 = new char[65535];
    try {
      int n1 = r1.read(buf1);
      int n2 = r2.read(buf2);
      return (n1 == n2 && Arrays.equals(buf1, buf2));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean compare(InputStream i1, InputStream i2) {
    byte[] buf1 = new byte[65535];
    byte[] buf2 = new byte[65535];
    try {
      int n1 = i1.read(buf1);
      int n2 = i2.read(buf2);
      return (n1 == n2 && Arrays.equals(buf1, buf2));
    } catch (IOException e) {
      throw new RuntimeException(e);
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
        // log.debug("[" + testName + "] " + messagePrefix + "success => " + ret);
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
      ParameterizedSql statement = OrderedParameterSqlParser
          .of("SELECT " + column + " FROM sql_mapper_test WHERE " + column + " in(<?>)")
          .addParameter(param).parse();
      Map<String, Object> ret = c.readFirst(RowMap.class, statement);
      if (ret != null) {
        // log.debug("[" + testName + "] " + messagePrefix + "success => " + ret);
      } else {
        log.error("[" + testName + "] " + messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      log.error("[" + testName + "] " + messagePrefix + "fail => " + e.getMessage());
    }
  }



}
