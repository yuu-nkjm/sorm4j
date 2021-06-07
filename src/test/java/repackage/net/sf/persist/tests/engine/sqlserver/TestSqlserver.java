package repackage.net.sf.persist.tests.engine.sqlserver;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.SormConfig;
import org.nkjmlab.sorm4j.internal.SormContext;
import repackage.net.sf.persist.tests.engine.framework.BeanMap;
import repackage.net.sf.persist.tests.engine.framework.BeanTest;
import repackage.net.sf.persist.tests.engine.framework.DbEngineTestUtils;
import repackage.net.sf.persist.tests.engine.framework.FieldMap;

public class TestSqlserver {
  private static DataSource dataSource;
  private static SormContext conf;

  public static void main(String[] args) {
    beforAll();
  }

  @BeforeAll
  static void beforAll() {
    dataSource = DbEngineTestUtils.getDataSource(TestSqlserver.class,
        "jdbc:h2:mem:sqlserver;MODE=MSSQLServer");
    DbEngineTestUtils.executeSql(dataSource, TestSqlserver.class, "schema.sql");
    conf = new SormContext(SormConfig.newBuilder().setOption("dbEngine", "MSSQLServer").build());

  }


  @Test
  public void testStringTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn, conf);
      ormConn.setAutoCommit(false);
      Class<?>[] characterTypes = new Class<?>[] {String.class};
      Class<?>[] stringTypes = new Class<?>[] {String.class};

      BeanMap beanMap = new BeanMap("StringTypes")
          .addField(new FieldMap("charCol").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
          .addField(new FieldMap("ncharCol").setTypes(characterTypes).setSize(1))
          .addField(new FieldMap("nvarcharCol").setTypes(stringTypes).setSize(255));

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
  public void testNumericTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn, conf);

      Class<?>[] integerTypes = new Class<?>[] {Integer.class, int.class};
      Class<?>[] booleanTypes = new Class<?>[] {Boolean.class, boolean.class};
      Class<?>[] byteTypes = new Class<?>[] {Byte.class, byte.class};
      Class<?>[] shortTypes = new Class<?>[] {Short.class, short.class};
      Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
      Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
      Class<?>[] floatTypes =
          new Class<?>[] {float.class, Double.class, double.class, BigDecimal.class};

      BeanMap beanMap =
          new BeanMap("NumericTypes").addField(new FieldMap("bitCol").setTypes(booleanTypes))
              .addField(new FieldMap("tinyintCol").setTypes(byteTypes))
              .addField(new FieldMap("smallintCol").setTypes(shortTypes))
              .addField(new FieldMap("intCol").setTypes(integerTypes))
              .addField(new FieldMap("bigintCol").setTypes(longTypes))
              .addField(new FieldMap("smallmoneyCol").setTypes(doubleTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("moneyCol").setTypes(doubleTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("decimalCol").setTypes(integerTypes))
              .addField(new FieldMap("numericCol").setTypes(integerTypes))
              .addField(new FieldMap("floatCol").setTypes(floatTypes).setBoundaries(0, 9999))
              .addField(new FieldMap("realCol").setTypes(floatTypes).setBoundaries(0, 9999));
      BeanTest.test(getClass(), ormConn, beanMap, obj -> {
        BeanTest.testInsert(ormConn, obj, beanMap);
        BeanTest.testSelectByFields(ormConn, obj, beanMap);
        BeanTest.testSelectFields(ormConn, obj, beanMap, false);
        BeanTest.testSelectMap(ormConn, obj, beanMap);
      });
      // BeanTest.testNull(getClass(), ormConn, beanMap,objNull->{
      // BeanTest.testInsert(ormConn, objNull, beanMap);
      // BeanTest.testSelectByFieldsNull(ormConn, objNull, beanMap);
      // BeanTest.testSelectFields(ormConn, objNull, beanMap, true);
      // BeanTest.testSelectMap(ormConn, objNull, beanMap);
      // });

    }
  }

  @Test
  public void testDatetimeTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn, conf);

      BeanMap beanMap = new BeanMap("DatetimeTypes")
          .addField(new FieldMap("datetimeCol").setTypes(java.sql.Timestamp.class));

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
  public void testBinaryTypes() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      OrmConnection ormConn = Sorm.toOrmConnection(conn, conf);
      ormConn.setAutoCommit(false);

      Class<?>[] binaryTypes = new Class<?>[] {byte[].class};

      BeanMap beanMap = new BeanMap("BinaryTypes")
          .addField(new FieldMap("binaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("varbinaryCol").setTypes(binaryTypes).setSize(255))
          .addField(new FieldMap("imageCol").setTypes(binaryTypes).setSize(16384)
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

      ormConn.commit();
    }
  }


}
