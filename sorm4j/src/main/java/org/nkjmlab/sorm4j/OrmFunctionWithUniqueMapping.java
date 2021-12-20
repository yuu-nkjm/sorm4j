package org.nkjmlab.sorm4j;

import java.sql.ResultSet;
import java.util.List;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;

/**
 * <p>
 * A interface for orm functions by the unique mapping the object class to the table.
 * <p>
 *
 * @author nkjm
 *
 */
public interface OrmFunctionWithUniqueMapping {

  /**
   * Gets table name corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  String getTableName(Class<?> objectClass);


  /**
   * Gets table metadata corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  TableMetaData getTableMetaData(Class<?> objectClass);


  /**
   * Reads all rows from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> List<T> readAll(Class<T> objectClass);

  /**
   * Reads an object by its primary keys from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @param primaryKeyValues
   * @return
   */
  <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues);

  /**
   * Gets a function which maps one row in the resultSet to an object. The method does not call
   * {@link ResultSet#next()}.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> RowMapper<T> getRowMapper(Class<T> objectClass);

  /**
   * Gets function which traverses and maps the all the rows in the given resultSet to an object
   * list.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> objectClass);

  /**
   * Returns the object which has same primary key exists or not.
   *
   * @param object
   * @return
   */
  <T> boolean exists(T object);

  /**
   * Deletes objects from the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] delete(List<T> objects);

  /**
   * Deletes an object from the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int delete(T object);

  /**
   * Deletes objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] delete(@SuppressWarnings("unchecked") T... objects);


  /**
   * Deletes all objects on the table corresponding to the given class.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> int deleteAll(Class<T> objectClass);

  /**
   * Deletes all objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @return
   */
  int deleteAllOn(String tableName);

  /**
   * Inserts objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(List<T> objects);

  /**
   * Inserts object on the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int insert(T object);

  /**
   * Insert objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(@SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGet(List<T> objects);

  /**
   * Inserts an object and get the result.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> InsertResult<T> insertAndGet(T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects);


  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(List<T> objects);


  /**
   * Merges by an object on the table corresponding to the class of the given object.
   * <p>
   * Merge methods execute a SQL sentence as MERGE INTO of the H2 grammar. This operation may be not
   * working the other database system.
   *
   * See, <a href="http://www.h2database.com/html/commands.html#merge_into">MERGE INTO -
   * Commands</a>
   * </p>
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int merge(T object);


  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   * @see #merge(Object)
   */
  <T> int[] merge(@SuppressWarnings("unchecked") T... objects);

  /**
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(List<T> objects);

  /**
   * Updates by an object on the table corresponding to the class of the given object.
   *
   * @param <T>
   * @param object
   * @return
   */
  <T> int update(T object);

  /**
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] update(@SuppressWarnings("unchecked") T... objects);


}
