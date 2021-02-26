package repackage.net.sf.persist.tests.product.db2;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.OrmService;
import org.nkjmlab.sorm4j.util.Try;
import repackage.net.sf.persist.tests.product.framework.BeanMap;
import repackage.net.sf.persist.tests.product.framework.BeanTest;
import repackage.net.sf.persist.tests.product.framework.FieldMap;

@Disabled
public class TestDB2 {

  private static final OrmService ormSrv = OrmService
      .of(JdbcConnectionPool.create("jdbc:h2:mem:persist;MODE=DB2", "persist", "persist"));

  @BeforeAll
  static void beforAll() {
    try {
      ormSrv.run(Try.createConsumerWithThrow(conn -> {
        String sql = String.join(System.lineSeparator(),
            Files.readAllLines(new File(TestDB2.class.getResource("db2.sql").toURI()).toPath()));
        conn.executeUpdate(sql);
      }, OrmException::new));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testStringTypes() throws SQLException {

    Class<?>[] characterTypes = new Class<?>[] {Character.class, char.class, String.class};
    Class<?>[] stringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
    Class<?>[] longStringTypes = new Class<?>[] {String.class, char[].class, Character[].class};
    Class<?>[] clobTypes =
        new Class<?>[] {String.class, char[].class, Character[].class, Clob.class};

    BeanMap beanMap = new BeanMap("StringTypes")
        .addField(new FieldMap("charCol").setTypes(characterTypes).setSize(1))
        .addField(new FieldMap("varcharCol").setTypes(stringTypes).setSize(255))
        .addField(new FieldMap("longVarcharCol").setTypes(longStringTypes).setSize(16384)
            .setSupportsQueryByValue(false));
    // .addField(new FieldMap("clobCol").setTypes(clobTypes).setSize(16384)
    // .setSupportsQueryByValue(false));

    BeanTest.test(ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testNumericTypes() throws SQLException {

    Class<?>[] integerTypes = new Class<?>[] {Integer.class, int.class};
    Class<?>[] shortTypes = new Class<?>[] {Short.class, short.class};
    Class<?>[] longTypes = new Class<?>[] {Long.class, long.class};
    Class<?>[] doubleTypes = new Class<?>[] {Double.class, double.class, BigDecimal.class};
    Class<?>[] floatTypes =
        new Class<?>[] {Float.class, float.class, Double.class, double.class, BigDecimal.class};

    BeanMap beanMap =
        new BeanMap("NumericTypes").addField(new FieldMap("smallintCol").setTypes(shortTypes))
            .addField(new FieldMap("integerCol").setTypes(integerTypes))
            .addField(new FieldMap("bigintCol").setTypes(longTypes))
            .addField(new FieldMap("realCol").setTypes(floatTypes).setBoundaries(0, 9999))
            .addField(new FieldMap("doublePrecisionCol").setTypes(doubleTypes)
                .setBoundaries(0, 9999).setSupportsQueryByValue(false))
            .addField(new FieldMap("floatCol").setTypes(floatTypes).setBoundaries(0, 9999))
            .addField(new FieldMap("decimalCol").setTypes(integerTypes).setBoundaries(0, 9999))
            .addField(new FieldMap("numericCol").setTypes(integerTypes).setBoundaries(0, 9999));

    BeanTest.test(ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testDatetimeTypes() throws SQLException {

    BeanMap beanMap = new BeanMap("DatetimeTypes")
        .addField(new FieldMap("dateCol").setTypes(java.sql.Date.class))
        .addField(new FieldMap("timeCol").setTypes(java.sql.Time.class)).addField(
            new FieldMap("timestampCol").setTypes(java.sql.Timestamp.class, java.util.Date.class));

    BeanTest.test(ormSrv.getConnection(), beanMap);
  }

  @Test
  public void testBinaryTypes() throws SQLException {

    Class<?>[] byteTypes = new Class<?>[] {char[].class,};
    Class<?>[] binaryTypes = new Class<?>[] {byte[].class};
    // Class<?>[] blobTypes = new Class<?>[] {byte[].class, InputStream.class, Blob.class};
    Class<?>[] blobTypes = new Class<?>[] {byte[].class, Blob.class};
    // TODO: check if short[] works for graphic types (which are double byte)

    BeanMap beanMap = new BeanMap("BinaryTypes")
        .addField(new FieldMap("charBitCol").setTypes(byteTypes).setSize(1))
        .addField(new FieldMap("varcharBitCol").setTypes(binaryTypes).setSize(255))
        .addField(new FieldMap("longVarcharBitCol").setTypes(binaryTypes).setSize(255)
            .setSupportsQueryByValue(false))
        // .addField( new FieldMap("graphicCol").setTypes(binaryTypes).setSize(1) )
        // .addField( new FieldMap("vargraphicCol").setTypes(binaryTypes).setSize(255) )
        // .addField( new
        // FieldMap("longVargraphicCol").setTypes(binaryTypes).setSize(16384).setSupportsQueryByValue(false))
        .addField(new FieldMap("blobCol").setTypes(blobTypes).setSize(16384)
            .setSupportsQueryByValue(false));

    BeanTest.test(ormSrv.getConnection(), beanMap);
  }

}
