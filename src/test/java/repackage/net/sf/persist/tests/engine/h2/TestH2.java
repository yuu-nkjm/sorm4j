
package repackage.net.sf.persist.tests.engine.h2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormFactory;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;


public class TestH2 {


  private static final JdbcConnectionPool connectionPool =
      JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "sa", "");
  // private static final JdbcConnectionPool connectionPool =
  // JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/db/ormtest", "sa", "");

  @BeforeAll
  static void beforAll() {
    try (Connection conn = connectionPool.getConnection()) {
      Statement st = conn.createStatement();
      String sql = String.join(System.lineSeparator(),
          Files.readAllLines(new File(TestH2.class.getResource("h2.sql").toURI()).toPath()));
      st.executeUpdate(sql);
    } catch (SQLException | URISyntaxException | IOException e) {
      e.printStackTrace();
    }
  }



  @BeforeEach
  void beforeEach() throws SQLException {}

  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);

      Class<?>[] binaryTypes =
          new Class<?>[] {byte[].class, Byte[].class, InputStream.class, Blob.class};
      Class<?>[] otherTypes = new Class<?>[] {Object.class};

      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("binaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("blobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("otherCol").setTypes(otherTypes).setSize(255));

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
  public void testDatetimeTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
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
    try (Connection conn = connectionPool.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);

      Class<?>[] integerTypes = new Class<?>[] {Integer.class, int.class};
      Class<?>[] booleanTypes = new Class<?>[] {Boolean.class, boolean.class};
      Class<?>[] byteTypes = new Class<?>[] {Byte.class, byte.class};
      Class<?>[] shortTypes = new Class<?>[] {Short.class, short.class};
      Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
      Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
      Class<?>[] floatTypes =
          new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

      BeanMap beanMap =
          new BeanMap("NumericTypes").addField(new FieldMap("intCol").setTypes(integerTypes))
              .addField(new FieldMap("booleanCol").setTypes(booleanTypes))
              .addField(new FieldMap("tinyintCol").setTypes(byteTypes))
              .addField(new FieldMap("smallintCol").setTypes(shortTypes))
              .addField(new FieldMap("bigintCol").setTypes(longTypes))
              .addField(new FieldMap("decimalCol").setTypes(longTypes))
              .addField(new FieldMap("doubleCol").setTypes(doubleTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("realCol").setTypes(floatTypes).setBoundaries(0, 9999));

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

    try (Connection conn = connectionPool.getConnection()) {
      OrmConnection ormConn = SormFactory.toOrmConnection(conn);

      Class<?>[] stringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
      Class<?>[] clobTypes =
          new Class<?>[] {String.class, char[].class, Character[].class, Reader.class, Clob.class};

      // uuid not being tested
      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("charCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("varcharIgnorecaseCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("clobCol").setTypes(clobTypes).setSize(8192));

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

}
