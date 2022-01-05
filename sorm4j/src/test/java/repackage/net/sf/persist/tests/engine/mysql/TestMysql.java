

package repackage.net.sf.persist.tests.engine.mysql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;


public class TestMysql {

  private static DataSource dataSource;

  @BeforeAll
  static void beforAll() {
    dataSource = DbEngineTestUtils.getDataSource(TestMysql.class,
        "jdbc:h2:mem:mysql;MODE=MySQL;DATABASE_TO_LOWER=TRUE");
    DbEngineTestUtils.executeSql(dataSource, TestMysql.class, "schema.sql");
  }

  public static void main(String[] args) {
    beforAll();
  }


  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn);

      Class<?>[] binaryTypes = new Class<?>[] {byte[].class, Blob.class};
      Class<?>[] blobTypes = new Class<?>[] {Blob.class};

      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("binaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("varbinaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("tinyblobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(255))
          .addField(new FieldMap("mediumblobCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("longblobCol").setTypes(binaryTypes).setSize(16384));

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
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn);

      BeanMap beanMap = new BeanMap("DatetimeTypes")
          .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class))
          .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class))
          .addField(
              new FieldMap("datetimeCol").setTypes(java.sql.Timestamp.class, java.util.Date.class))
          .addField(new FieldMap("year4Col").setTypes(Short.class, short.class)
              .setBoundaries(1901, 1999).setSupportsCompareMapValue(false));

      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        // BeanTest.testSelectByFields(ormConn, obj, beanMap);
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
      OrmConnection ormConn = Sorm.toOrmConnection(conn);
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
      OrmConnection ormConn = Sorm.toOrmConnection(conn);

      Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
      Class<?>[] stringTypes = new Class<?>[] {String.class};
      // Class<?>[] clobTypes =
      // new Class<?>[] {String.class, char[].class, Character[].class, Reader.class, Clob.class};
      Class<?>[] clobTypes = new Class<?>[] {String.class};

      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("charCol").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("tinytextCol").setTypes(clobTypes).setSize(255))
          .addField(new FieldMap("mediumtextCol").setTypes(clobTypes).setSize(1024))
          .addField(new FieldMap("longtextCol").setTypes(clobTypes).setSize(16384))
          .addField(new FieldMap("textCol").setTypes(clobTypes).setSize(16384))
          .addField(new FieldMap("enumCol").setTypes(characterTypes).setSize(1));
      // .addField(new FieldMap("setCol").setTypes(characterTypes).setSize(1));

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
