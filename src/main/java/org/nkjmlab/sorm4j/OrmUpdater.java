package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.sql.InsertResult;

/**
 * <p>
 * A interface for updating database.
 * <p>
 *
 * <p>
 * Merge methods execute a SQL sentence as MERGE INTO of the H2 grammar. This operation may be not
 * working the other database system.
 *
 * See, <a href="http://www.h2database.com/html/commands.html#merge_into">MERGE INTO - Commands</a>
 * </p>
 *
 * @author nkjm
 *
 */
public interface OrmUpdater {

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
   * Deletes objects on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, List<T> objects);

  /**
   * Deletes object on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int deleteOn(String tableName, T object);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);

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
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> InsertResult<T> insertAndGetOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int insertOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] merge(List<T> objects);

  /**
   * Merges by an object on the table corresponding to the class of the given object.
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
   */
  <T> int[] merge(@SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeOn(String tableName, List<T> objects);

  /**
   * Merges by an object on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int mergeOn(String tableName, T object);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects);

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

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateOn(String tableName, List<T> objects);

  /**
   * Updates by an object on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param object
   * @return
   */
  <T> int updateOn(String tableName, T object);

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param <T>
   * @param tableName
   * @param objects
   * @return
   */
  <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects);

}
