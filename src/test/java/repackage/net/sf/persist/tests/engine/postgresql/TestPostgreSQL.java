package repackage.net.sf.persist.tests.engine.postgresql;


import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormFactory;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;

public class TestPostgreSQL {
  private static DataSource dataSource;

  @BeforeAll
  static void beforAll() {
    dataSource =
        DbEngineTestUtils.getDataSource(TestPostgreSQL.class, "jdbc:h2:mem:postgre;MODE=PostgreSQL");
    DbEngineTestUtils.executeTableSchema(TestPostgreSQL.class, dataSource);
  }

  public static void main(String[] args) {
    beforAll();
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
      // for Blob columns
      // therefore we won't test comparing the blobCol value returned from a map with it
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
      // for Clob columns
      // therefore we won't test comparing the clobCol value returned from a map with it
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
