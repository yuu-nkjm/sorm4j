package repackage.net.sf.persist.tests.engine.postgresql;


import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.Clob;
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
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;

public class TestPostgreSQL {
  private static DataSource dataSource;

  @BeforeAll
  static void beforAll() {
    dataSource = DbEngineTestUtils.getDataSource(TestPostgreSQL.class,
        "jdbc:h2:mem:postgre;MODE=PostgreSQL");
    DbEngineTestUtils.executeTableSchema(TestPostgreSQL.class, dataSource);
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
      if (ret.equals(param)) {
        System.out.println(messagePrefix + "success => " + ret);
      } else {
        System.out.println(messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      System.err.println(messagePrefix + "fail => " + e.getMessage());
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
        System.out.println(messagePrefix + "fail => " + ret);
      }
    } catch (Exception e) {
      System.err.println(messagePrefix + "fail => " + e.getMessage());
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



  // several tests inherited from net.sf.persist.tests.common.TestSimple

  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);
      conn.setAutoCommit(false);

      Class<?>[] binaryTypes = new Class<?>[] {byte[].class, Byte[].class, InputStream.class};
      Class<?>[] blobTypes = new Class<?>[] {Blob.class};

      // postgres has a serious bug on returning java.sql.Types.BIGINT type on the ResultSetMetadata
      // for Blob columns therefore we won't test comparing the blobCol value returned from a map
      // with it

      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("byteaCol").setTypes(binaryTypes).setSize(16384))
          .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(8192)
              .setSupportsQueryByValue(false).setSupportsCompareMapValue(false));

      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        BeanTest.testSelectByFields(ormConn, obj, beanMap);
        BeanTest.testSelectFields(ormConn, obj, beanMap, false);
        BeanTest.testSelectMap(ormConn, obj, beanMap);
      });
      BeanTest.testNull(getClass(), ormConn, beanMap, objNull -> {
        BeanTest.testInsert(ormConn, objNull, beanMap);
        BeanTest.testSelectByFieldsNull(ormConn, objNull, beanMap);
        BeanTest.testSelectFields(ormConn, objNull, beanMap, true);
        BeanTest.testSelectMap(ormConn, objNull, beanMap);
      });

      ormConn.commit();
    }
  }

  @Test
  public void testDatetimeTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);
      BeanMap beanMap = new BeanMap("DatetimeTypes")
          .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class))
          .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class))
          .addField(new FieldMap("timestampCol").setTypes(java.sql.Timestamp.class,
              java.util.Date.class));
      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        BeanTest.testSelectByFields(ormConn, obj, beanMap);
        BeanTest.testSelectFields(ormConn, obj, beanMap, false);
        BeanTest.testSelectMap(ormConn, obj, beanMap);
      });
      BeanTest.testNull(getClass(), ormConn, beanMap, objNull -> {
        BeanTest.testInsert(ormConn, objNull, beanMap);
        BeanTest.testSelectByFieldsNull(ormConn, objNull, beanMap);
        BeanTest.testSelectFields(ormConn, objNull, beanMap, true);
        BeanTest.testSelectMap(ormConn, objNull, beanMap);
      });

    }
  }

  @Test
  public void testNumericTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);

      Class<?>[] shortTypes = new Class<?>[] {Short.class, short.class};
      Class<?>[] integerTypes = new Class<?>[] {Integer.class, int.class};
      Class<?>[] booleanTypes = new Class<?>[] {Boolean.class, boolean.class};
      Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
      Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
      Class<?>[] floatTypes =
          new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

      BeanMap beanMap =
          new BeanMap("NumericTypes").addField(new FieldMap("smallintCol").setTypes(shortTypes))
              .addField(new FieldMap("integerCol").setTypes(integerTypes))
              .addField(new FieldMap("bigintCol").setTypes(longTypes))
              .addField(new FieldMap("decimalCol").setTypes(longTypes))
              .addField(new FieldMap("numericCol").setTypes(longTypes))
              .addField(new FieldMap("realCol").setTypes(floatTypes).setBoundaries(0, 9999))
              .addField(
                  new FieldMap("doublePrecisionCol").setTypes(doubleTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("booleanCol").setTypes(booleanTypes));

      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        BeanTest.testSelectByFields(ormConn, obj, beanMap);
        BeanTest.testSelectFields(ormConn, obj, beanMap, false);
        BeanTest.testSelectMap(ormConn, obj, beanMap);
      });
      BeanTest.testNull(getClass(), ormConn, beanMap, objNull -> {
        BeanTest.testInsert(ormConn, objNull, beanMap);
        BeanTest.testSelectByFieldsNull(ormConn, objNull, beanMap);
        BeanTest.testSelectFields(ormConn, objNull, beanMap, true);
        BeanTest.testSelectMap(ormConn, objNull, beanMap);
      });

    }
  }

  @Test
  public void testStringTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);
      conn.setAutoCommit(false);

      Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
      Class<?>[] stringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
      Class<?>[] clobTypes = new Class<?>[] {Clob.class};

      // postgres has a serious bug on returning java.sql.Types.BIGINT type on the ResultSetMetadata
      // for Clob columns therefore we won't test comparing the clobCol value returned from a map
      // with it
      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("char1Col").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("charCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("textCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("clobCol").setTypes(clobTypes).setSize(8192)
              .setSupportsQueryByValue(false).setSupportsCompareMapValue(false));
      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        BeanTest.testSelectByFields(ormConn, obj, beanMap);
        BeanTest.testSelectFields(ormConn, obj, beanMap, false);
        BeanTest.testSelectMap(ormConn, obj, beanMap);
      });
      BeanTest.testNull(getClass(), ormConn, beanMap, objNull -> {
        BeanTest.testInsert(ormConn, objNull, beanMap);
        BeanTest.testSelectByFieldsNull(ormConn, objNull, beanMap);
        BeanTest.testSelectFields(ormConn, objNull, beanMap, true);
        BeanTest.testSelectMap(ormConn, objNull, beanMap);
      });

      ormConn.commit();

    }
  }


}
