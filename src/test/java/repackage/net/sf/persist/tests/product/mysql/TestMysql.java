

package repackage.net.sf.persist.tests.product.mysql;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import repackage.net.sf.persist.tests.product.framework.BeanMap;
import repackage.net.sf.persist.tests.product.framework.BeanTest;
import repackage.net.sf.persist.tests.product.framework.FieldMap;


public class TestMysql {


  private static DataSource dataSource;

  public static void main(String[] args) {
    beforAll();
  }

  @BeforeAll
  static void beforAll() {
    dataSource = SormTestUtils.getDataSource(TestMysql.class,
        "jdbc:h2:mem:mysql;MODE=MySQL;DATABASE_TO_LOWER=TRUE");
    SormTestUtils.executeTableSchema(TestMysql.class, dataSource);

  }

  // several tests are inherited from TestSimple

  @Test
  public void testStringTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection persist = SormFactory.toOrmConnection(conn);

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

    try (Connection conn = dataSource.getConnection()) {
      OrmConnection persist = SormFactory.toOrmConnection(conn);
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
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection persist = SormFactory.toOrmConnection(conn);

      BeanMap beanMap = new BeanMap("DatetimeTypes")
          .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class))
          .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class))
          .addField(new FieldMap("year4Col").setTypes(Short.class, short.class)
              .setBoundaries(1901, 1999).setSupportsCompareMapValue(false));

      BeanTest.test(getClass(), persist, beanMap);
    }
  }

  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection persist = SormFactory.toOrmConnection(conn);

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
