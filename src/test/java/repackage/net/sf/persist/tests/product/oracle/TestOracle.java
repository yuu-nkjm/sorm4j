package repackage.net.sf.persist.tests.product.oracle;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import repackage.net.sf.persist.tests.product.framework.BeanMap;
import repackage.net.sf.persist.tests.product.framework.BeanTest;
import repackage.net.sf.persist.tests.product.framework.FieldMap;


public class TestOracle {

  private static final Sorm ormSrv = SormFactory
      .create(JdbcConnectionPool.create("jdbc:h2:mem:oracle;MODE=Oracle", "persist", "persist"));

  @BeforeAll
  static void beforAll() {
    try {

      ormSrv.apply(conn -> {
        String sql = String.join(System.lineSeparator(), Files
            .readAllLines(new File(TestOracle.class.getResource("oracle.sql").toURI()).toPath()));
        conn.executeUpdate(sql);
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @Test
  public void testStringTypes() throws SQLException {

    Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
    Class<?>[] stringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
    // Class<?>[] clobTypes = new Class<?>[] {String.class, char[].class, Character[].class};

    // oracle doesn't support queries by clob, long or nclob
    BeanMap beanMap = new BeanMap("StringTypes")
        .addField(new FieldMap("char1Col").setTypes(characterTypes).setSize(1))
        .addField(new FieldMap("nchar1Col").setTypes(characterTypes).setSize(1))
        .addField(new FieldMap("charCol").setTypes(stringTypes).setSize(255))
        .addField(new FieldMap("ncharCol").setTypes(stringTypes).setSize(255))
        .addField(new FieldMap("nvarchar2Col").setTypes(stringTypes).setSize(255))
        .addField(new FieldMap("varchar2Col").setTypes(stringTypes).setSize(255));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testNumericTypes() throws SQLException {

    Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
    Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
    Class<?>[] floatTypes =
        new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

    BeanMap beanMap =
        new BeanMap("NumericTypes").addField(new FieldMap("numberCol").setTypes(longTypes))
            .addField(new FieldMap("binaryFloatCol").setTypes(floatTypes).setBoundaries(0, 9999))
            .addField(new FieldMap("binaryDoubleCol").setTypes(doubleTypes).setBoundaries(0, 9999));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testDatetimeTypes() throws SQLException {

    BeanMap beanMap = new BeanMap("DatetimeTypes")
        // .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class)) // oracle is picky about
        // Date when used in
        // queries
        .addField(
            new FieldMap("timestampCol").setTypes(java.sql.Timestamp.class, java.util.Date.class));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testBinaryTypes() throws SQLException {

    Class<?>[] binaryTypes = new Class<?>[] {byte[].class, Byte[].class};
    Class<?>[] blobTypes = new Class<?>[] {byte[].class, Byte[].class, InputStream.class};

    // by default, oracle can't bind more than 4000 bytes to lob/long columns in a single statement
    // oracle doesn't support queries by long_raw or blob columns
    // only blobs support InputStream types
    BeanMap beanMap = new BeanMap("BinaryTypes")
        .addField(new FieldMap("rawCol").setTypes(binaryTypes).setSize(1024))
        .addField(new FieldMap("longRawCol").setTypes(binaryTypes).setSize(1024)
            .setSupportsQueryByValue(false))
        .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(1024)
            .setSupportsQueryByValue(false));

    BeanTest.test(getClass(), ormSrv.getConnection(), beanMap);
  }

}
