
// $Id$

package repackage.net.sf.persist.tests.product.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;


public class BeanTest {

  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.core.util.LoggerFactory.getLogger();


  public static void test(Class<?> caller, OrmConnection persist, BeanMap beanMap) {
    Connection connection = persist.getJdbcConnection();
    Class<?> cls = DynamicBean.createBeanClass(caller, beanMap);

    String tableName = dbName(beanMap.getClassName());

    try {
      connection.createStatement().execute("delete from " + tableName);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    BeanTest.testAll(persist, cls, beanMap);


    try {
      connection.createStatement().execute("delete from " + tableName);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    BeanTest.testAllNull(persist, cls, beanMap);


  }

  /**
   * Perform tests using a bean with non-null values
   */
  public static void testAll(OrmConnection persist, Class<?> cls, BeanMap beanMap) {
    Object obj = DynamicBean.createInstance(cls, beanMap, false);
    BeanTest.testInsert(persist, obj, beanMap);
    BeanTest.testSelectByFields(persist, obj, beanMap);
    BeanTest.testSelectFields(persist, obj, beanMap, false);
    BeanTest.testSelectMap(persist, obj, beanMap);
  }

  /**
   * Perform tests using a bean with null values
   */
  public static void testAllNull(OrmConnection persist, Class<?> cls, BeanMap beanMap) {
    Object objNull = DynamicBean.createInstance(cls, beanMap, true);
    BeanTest.testInsert(persist, objNull, beanMap);
    BeanTest.testSelectByFieldsNull(persist, objNull, beanMap);
    BeanTest.testSelectFields(persist, objNull, beanMap, true);
    BeanTest.testSelectMap(persist, objNull, beanMap);
  }

  /**
   * tests insertion of a bean
   */
  public static void testInsert(OrmConnection persist, Object obj, BeanMap beanMap) {
    Class<?> cls = obj.getClass();
    String tableName = dbName(obj.getClass().getSimpleName());

    // perform insert
    persist.insert(obj);

    // check if a single result exists in the table (expects the table to be clean by the beginning
    // of the process)
    String sql = "select * from " + tableName;
    List<?> read = persist.readList(cls, sql);
    if (read.size() != 1) {
      throw new AssertionError(
          "Expected 1 result but got [" + read.size() + "] as result of sql [" + sql + "]");
    }

    // check if the bean read is the same as inserted
    if (!obj.equals(read.get(0))) {
      throw new AssertionError("Expected [" + DynamicBean.toString(obj) + "] but got ["
          + DynamicBean.toString(read.get(0)) + "] as result of [" + sql + "]");
    }
  }

  /**
   * tests reading with NoTable class
   */
  public static void testReadNoTable(OrmConnection persist, Object obj, Class<?> clsNoTable) {

    String tableName = dbName(obj.getClass().getSimpleName());

    // now read with NoTable (assumes previously inserted data)
    Object objNoTable = persist.readFirst(clsNoTable, "select * from " + tableName);

    // compare values
    if (!DynamicBean.compareBeansFromDifferentClasses(obj, objNoTable)) {
      throw new AssertionError("Expected [" + DynamicBean.toString(obj) + "] but got ["
          + DynamicBean.toString(objNoTable) + "]");
    }
  }

  /**
   * For each field and each field type, execute a query in the format select * from tableName where
   * columnName=?
   */
  public static void testSelectByFields(OrmConnection persist, Object obj, BeanMap beanMap) {
    Class<?> cls = obj.getClass();
    String tableName = dbName(obj.getClass().getSimpleName());

    // for each field in the bean
    for (FieldMap fieldMap : beanMap.getFields()) {

      // only perform tests if the field supports queries by value (blobs in oracle, for instance,
      // don't)
      if (fieldMap.isSupportsQueryByValue()) {
        String columnName = dbName(fieldMap.getFieldName());
        String sql = "select * from " + tableName + " where " + columnName + "=?";
        Object fieldValue = DynamicBean.getFieldValue(obj, fieldMap.getFieldName());

        // for each type supported by the field, test if a query using an object of that type
        // returns data correctly
        for (Class<?> fieldType : fieldMap.getTypes()) {

          // use value converted to the type being tested
          Object fieldValueConverted = DynamicBean.convertToType(fieldType, fieldValue);

          // query using the type being tested
          Object ret = persist.readFirst(cls, sql, fieldValueConverted);

          // check if the result is not null and has the same data as the object being tested
          if (ret == null) {
            log.error("{}, {}", fieldValueConverted, persist.readAll(obj.getClass()));
            throw new AssertionError("Expected not null value but got null as result of [" + sql
                + "] with parameter [" + fieldValue + "]");
          }
          if (!obj.equals(ret)) {
            throw new AssertionError("Expected [" + DynamicBean.toString(obj) + "] but got ["
                + DynamicBean.toString(ret) + "] as result of [" + sql + "]");
          }
        }
      }
    }
  }

  /**
   * For each field, execute a query in the format select * from tableName where columnName is null
   */
  public static void testSelectByFieldsNull(OrmConnection persist, Object obj, BeanMap beanMap) {
    Class<?> cls = obj.getClass();
    String tableName = dbName(obj.getClass().getSimpleName());

    // for each field in the bean
    for (FieldMap fieldMap : beanMap.getFields()) {

      // only perform tests if the field supports queries by value (blobs in oracle, for instance,
      // don't)
      if (fieldMap.isSupportsQueryByValue()) {
        String columnName = dbName(fieldMap.getFieldName());

        // test if query for null value in the column related with the field return the object
        // correctly
        String sql = "select * from " + tableName + " where " + columnName + " is null";
        Object ret = persist.readFirst(cls, sql);
        if (ret == null)
          throw new AssertionError(
              "Expected not null value but got null as result of [" + sql + "]");
        if (!obj.equals(ret)) {
          throw new AssertionError("Expected [" + DynamicBean.toString(obj) + "] but got ["
              + DynamicBean.toString(ret) + "] as result of [" + sql + "]");
        }
      }
    }
  }

  /**
   * for each field, and for each field type perform a query in the form select columnName from
   * tableName
   */
  public static void testSelectFields(OrmConnection persist, Object obj, BeanMap beanMap,
      boolean useNulls) {
    String tableName = dbName(obj.getClass().getSimpleName());

    // for each field in the bean
    for (FieldMap fieldMap : beanMap.getFields()) {
      String columnName = dbName(fieldMap.getFieldName());
      String sql = "select " + columnName + " from " + tableName;

      // get field value from the bean
      Object fieldValue = DynamicBean.getFieldValue(obj, fieldMap.getFieldName());

      // for each supported type
      for (Class<?> fieldType : fieldMap.getTypes()) {

        // query for a single column data using the field type (eg byte, Byte, String, InputStream,
        // etc.)
        Object ret = persist.readFirst(fieldType, sql);

        if (useNulls) {
          // check if "null", which means 0 for numeric values type as primitive (byte, short, int,
          // etc.)
          if (!DynamicBean.isNull(fieldType, ret))
            throw new AssertionError(
                "Expected null value but got [" + ret + "] as result of [" + sql + "]");
        } else {
          if (ret == null) {
            throw new AssertionError(
                "Expected not null value but got null as result of [" + sql + "]");
          }

          // TODO: maybe test compatibility of return type with field type?

          // compare values using a method that takes into consideration "compatible" types
          // (eg char[]-String, double-BigDecimal, etc)
          Object retConverted = DynamicBean.convertToType(fieldValue.getClass(), ret);
          if (!DynamicBean.compareValues(fieldValue, retConverted)) {
            throw new AssertionError(
                "Expected [" + fieldValue + "] but got [" + ret + "] as result of [" + sql + "]");
          }
        }
      }
    }
  }

  /**
   * perform [select * from tableName] and get the results as a map
   */
  public static void testSelectMap(OrmConnection persist, Object obj, BeanMap beanMap) {
    String tableName = dbName(obj.getClass().getSimpleName());

    // read list of all data in the table as a map
    String sql = "select * from " + tableName;
    List<Map<String, Object>> mapList = persist.readMapList(sql);

    // asserts there's only one entry (added during the insert test)
    if (mapList.size() != 1) {
      throw new AssertionError(
          "Expected 1 result but got [" + mapList.size() + "] as result of sql [" + sql + "]");
    }

    // use the first (or single) map returned
    Map<?, ?> m = mapList.get(0);

    // for each field in the bean
    for (FieldMap fieldMap : beanMap.getFields()) {
      String columnName = dbName(fieldMap.getFieldName());

      // get the field value
      Object fieldValue = DynamicBean.getFieldValue(obj, fieldMap.getFieldName());

      // get the corresponding map value
      Object mapValueU = m.get(columnName);
      Object mapValueL = m.get(columnName.toLowerCase());

      // if field supports comparisons on the map values (mysql's year2 and year4, for instance,
      // have Date objects here, but must be inserted as short's, therefore can't be compared
      // properly)
      if (fieldMap.isSupportsCompareMapValue()) {

        // compare values using a method that takes into consideration "compatible" types
        // (eg char[]-String, double-BigDecimal, etc)
        if (!DynamicBean.compareValues(fieldValue, mapValueU)
            && !DynamicBean.compareValues(fieldValue, mapValueL)) {
          throw new AssertionError("Map entry [" + columnName + "]=[" + mapValueU
              + "] does not match field [" + fieldMap.getFieldName() + "]=[" + fieldValue
              + "] as result of sql [" + sql + "]");
        }
      }
    }
  }



  // ---------- helpers ----------

  /**
   * Returned the database table/column name related with a given bean/field name
   */
  private static String dbName(String s) {
    String name = s.replaceAll("([A-Z])", "_$1").toLowerCase();
    return (name.charAt(0) == '_' ? name.substring(1) : name).toUpperCase();
  }

}
