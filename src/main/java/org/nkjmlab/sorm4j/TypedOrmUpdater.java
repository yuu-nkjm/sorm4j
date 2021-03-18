package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.sql.InsertResult;

/**
 * A typed interface for updating database.
 *
 * @author nkjm
 *
 */

public interface TypedOrmUpdater<T> {

  /**
   * Deletes objects from the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] delete(List<T> objects);

  /**
   * Deletes an object from the table corresponding to the class of the given objects.
   *
   * @param object
   * @return
   */
  int delete(T object);

  /**
   * Deletes objects.
   *
   * @param objects
   * @return
   */
  int[] delete(@SuppressWarnings("unchecked") T... objects);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] deleteOn(String tableName, List<T> objects);

  /**
   * Deletes object on the table of the given table name.
   *
   * @param tableName
   * @param object
   * @return
   */
  int deleteOn(String tableName, T object);

  /**
   * Deletes objects on the table of the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Deletes all objects on the table corresponding to the given class.
   *
   *
   * @return
   */
  int deleteAll();

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
   * @param objects
   * @return
   */
  int[] insert(List<T> objects);

  /**
   * Inserts object on the table corresponding to the class of the given object.
   *
   * @param object
   * @return
   */
  int insert(T object);

  /**
   * Insert objects on the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] insert(@SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param objects
   * @return
   */
  InsertResult<T> insertAndGet(List<T> objects);

  /**
   * Inserts an object and get the result.
   *
   * @param object
   * @return
   */
  InsertResult<T> insertAndGet(T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param objects
   * @return
   */
  InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param tableName
   * @param objects
   * @return
   */
  InsertResult<T> insertAndGetOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param tableName
   * @param object
   * @return
   */
  InsertResult<T> insertAndGetOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param tableName
   * @param objects
   * @return
   */
  InsertResult<T> insertAndGetOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] insertOn(String tableName, List<T> objects);

  /**
   * Inserts an object and get the insert result.
   *
   * @param tableName
   * @param object
   * @return
   */
  int insertOn(String tableName, T object);

  /**
   * Inserts objects and get the last insert result.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] merge(List<T> objects);

  /**
   * Merges by an object on the table corresponding to the class of the given object.
   *
   * @param object
   * @return
   */
  int merge(T object);

  /**
   * Merges by objects on the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] merge(@SuppressWarnings("unchecked") T... objects);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] mergeOn(String tableName, List<T> objects);

  /**
   * Merges by an object on the table corresponding to the given table name.
   *
   * @param tableName
   * @param object
   * @return
   */
  int mergeOn(String tableName, T object);

  /**
   * Merges by objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  /**
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] update(List<T> objects);

  /**
   * Updates by an object on the table corresponding to the class of the given object.
   *
   * @param object
   * @return
   */
  int update(T object);

  /**
   * Updates by objects on the table corresponding to the class of the given objects.
   *
   * @param objects
   * @return
   */
  int[] update(@SuppressWarnings("unchecked") T... objects);

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] updateOn(String tableName, List<T> objects);

  /**
   * Updates by an object on the table corresponding to the given table name.
   *
   * @param tableName
   * @param object
   * @return
   */
  int updateOn(String tableName, T object);

  /**
   * Updates by objects on the table corresponding to the given table name.
   *
   * @param tableName
   * @param objects
   * @return
   */
  int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects);


}
