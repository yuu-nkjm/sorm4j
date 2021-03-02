

package repackage.net.sf.persist.tests.product.mysql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmMapper;
import org.nkjmlab.sorm4j.Sorm;
import repackage.net.sf.persist.tests.product.framework.BeanMap;
import repackage.net.sf.persist.tests.product.framework.BeanTest;
import repackage.net.sf.persist.tests.product.framework.FieldMap;


public class TestMysql {

  private static final JdbcConnectionPool connectionPool = JdbcConnectionPool
      .create("jdbc:h2:mem:mysql;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "persist", "persist");

  @BeforeAll
  static void beforAll() {
    try (Connection conn = connectionPool.getConnection()) {
      Statement st = conn.createStatement();
      String sql = String.join(System.lineSeparator(),
          Files.readAllLines(new File(TestMysql.class.getResource("mysql.sql").toURI()).toPath()));
      st.executeUpdate(sql);
    } catch (SQLException | URISyntaxException | IOException e) {
      e.printStackTrace();
    }

  }

  // several tests are inherited from TestSimple

  @Test
  public void testStringTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper persist = Sorm.toOrmConnection(conn);

      Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
      Class<?>[] stringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
      // Class<?>[] clobTypes =
      // new Class<?>[] {String.class, char[].class, Character[].class, Reader.class, Clob.class};
      Class<?>[] clobTypes = new Class<?>[] {String.class, char[].class, Character[].class};

      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("charCol").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("tinytextCol").setTypes(clobTypes).setSize(255))
          .addField(new FieldMap("mediumtextCol").setTypes(clobTypes).setSize(1024))
          .addField(new FieldMap("longtextCol").setTypes(clobTypes).setSize(16384))
          .addField(new FieldMap("textCol").setTypes(clobTypes).setSize(16384))
          .addField(new FieldMap("enumCol").setTypes(characterTypes).setSize(1));
      // .addField(new FieldMap("setCol").setTypes(characterTypes).setSize(1));

      BeanTest.test(getClass(), persist, beanMap);
    }
  }

  @Test
  public void testNumericTypes() throws SQLException {

    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper persist = Sorm.toOrmConnection(conn);
      Class<?>[] integerTypes = new Class<?>[] {Integer.class, int.class};
      Class<?>[] booleanTypes = new Class<?>[] {Boolean.class, boolean.class};
      Class<?>[] byteTypes = new Class<?>[] {Byte.class, byte.class};
      Class<?>[] shortTypes = new Class<?>[] {Short.class, short.class};
      Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
      Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
      Class<?>[] floatTypes =
          new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

      BeanMap beanMap =
          new BeanMap("NumericTypes").addField(new FieldMap("bitCol").setTypes(booleanTypes))
              .addField(new FieldMap("booleanCol").setTypes(booleanTypes))
              .addField(new FieldMap("tinyintCol").setTypes(byteTypes))
              .addField(new FieldMap("smallintCol").setTypes(shortTypes))
              .addField(new FieldMap("mediumintCol").setTypes(shortTypes))
              .addField(new FieldMap("intCol").setTypes(integerTypes))
              .addField(new FieldMap("bigintCol").setTypes(longTypes))
              .addField(new FieldMap("floatCol").setTypes(floatTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("doubleCol").setTypes(doubleTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("decimalCol").setTypes(integerTypes));

      BeanTest.test(getClass(), persist, beanMap);
    }
  }

  @Test
  public void testDatetimeTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper persist = Sorm.toOrmConnection(conn);

      // not testing timestamp here, it doesn't support null values
      BeanMap beanMap = new BeanMap("DatetimeTypes")
          .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class))
          .addField(
              new FieldMap("datetimeCol").setTypes(java.sql.Timestamp.class, java.util.Date.class))
          .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class))
          // .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class, java.util.Date.class))
          .addField(new FieldMap("year2Col").setTypes(Short.class, short.class)
              .setBoundaries(01, 99).setSupportsCompareMapValue(false))
          .addField(new FieldMap("year4Col").setTypes(Short.class, short.class)
              .setBoundaries(1901, 1999).setSupportsCompareMapValue(false));

      BeanTest.test(getClass(), persist, beanMap);
    }
  }

  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = connectionPool.getConnection()) {
      OrmMapper persist = Sorm.toOrmConnection(conn);

      Class<?>[] binaryTypes =
          new Class<?>[] {byte[].class, Byte[].class, InputStream.class, Blob.class};

      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("binaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("varbinaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("tinyblobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("blobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("mediumblobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("longblobCol").setTypes(binaryTypes).setSize(16384));

      BeanTest.test(getClass(), persist, beanMap);
    }
  }


}
