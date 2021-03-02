package repackage.net.sf.persist.tests.product.postgresql;


import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.Try;
import repackage.net.sf.persist.tests.product.framework.BeanMap;
import repackage.net.sf.persist.tests.product.framework.BeanTest;
import repackage.net.sf.persist.tests.product.framework.FieldMap;

public class TestPostgreSQL {
  private static final Sorm ormSrv = Sorm
      .create(JdbcConnectionPool.create("jdbc:h2:mem:postgre;MODE=PostgreSQL", "persist", "persist"));

  @BeforeAll
  static void beforAll() {
    try {
      ormSrv.run(Try.createConsumerWithThrow(conn -> {
        String sql = String.join(System.lineSeparator(), Files.readAllLines(
            new File(TestPostgreSQL.class.getResource("postgresql.sql").toURI()).toPath()));
        conn.executeUpdate(sql);
      }, OrmException::new));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // several tests inherited from net.sf.persist.tests.common.TestSimple

  @Test
  public void testStringTypes() throws SQLException {

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

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testNumericTypes() throws SQLException {

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

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testDatetimeTypes() throws SQLException {

    BeanMap beanMap = new BeanMap("DatetimeTypes")
        .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class))
        .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class)).addField(
            new FieldMap("timestampCol").setTypes(java.sql.Timestamp.class, java.util.Date.class));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testBinaryTypes() throws SQLException {

    Class<?>[] binaryTypes = new Class<?>[] {byte[].class, Byte[].class, InputStream.class};
    Class<?>[] blobTypes = new Class<?>[] {Blob.class};

    // postgres has a serious bug on returning java.sql.Types.BIGINT type on the ResultSetMetadata
    // for Blob columns
    // therefore we won't test comparing the blobCol value returned from a map with it
    BeanMap beanMap = new BeanMap("BinaryTypes")
        .addField(new FieldMap("byteaCol").setTypes(binaryTypes).setSize(16384))
        .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(8192)
            .setSupportsQueryByValue(false).setSupportsCompareMapValue(false));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }


}
