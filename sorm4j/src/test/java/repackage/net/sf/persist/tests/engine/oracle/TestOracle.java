package repackage.net.sf.persist.tests.engine.oracle;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.context.SormContext;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;


public class TestOracle {

  private static DataSource dataSource;


  @BeforeAll
  static void beforAll() {
    dataSource =
        DbEngineTestUtils.getDataSource(TestOracle.class, "jdbc:h2:mem:oracle;MODE=Oracle");
    DbEngineTestUtils.executeSql(dataSource, TestOracle.class, "schema.sql");
  }

  public static void main(String[] args) {
    beforAll();
  }


  @Test
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Class<?>[] binaryTypes = new Class<?>[] {byte[].class};
      Class<?>[] blobTypes = new Class<?>[] {Blob.class};

      // by default, oracle can't bind more than 4000 bytes to lob/long columns in a single
      // statement
      // oracle doesn't support queries by long_raw or blob columns
      // only blobs support InputStream types
      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("rawCol").setTypes(binaryTypes).setSize(1024))
          .addField(new FieldMap("longRawCol").setTypes(binaryTypes).setSize(1024)
              .setSupportsQueryByValue(false))
          .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(1024)
              .setSupportsQueryByValue(false));

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
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      BeanMap beanMap = new BeanMap("DatetimeTypes")
          // .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class)) // oracle is picky
          // about
          // Date when used in
          // queries
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
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
      // Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
      // Class<?>[] floatTypes =
      // new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

      BeanMap beanMap =
          new BeanMap("NumericTypes").addField(new FieldMap("numberCol").setTypes(longTypes));

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
    try (Connection conn = dataSource.getConnection();
        OrmConnection ormConn = OrmConnection.of(conn, SormContext.builder().build())) {

      Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
      Class<?>[] stringTypes = new Class<?>[] {String.class};
      // Class<?>[] clobTypes = new Class<?>[] {String.class, char[].class, Character[].class};

      // oracle doesn't support queries by clob, long or nclob
      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("char1Col").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("nchar1Col").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("charCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("ncharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("nvarchar2Col").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("varchar2Col").setTypes(stringTypes).setSize(255));

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
