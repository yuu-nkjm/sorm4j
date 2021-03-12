package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.result.InsertResult;

/**
 * A interface for updating database.
 *
 * @author nkjm
 *
 */
public interface OrmUpdater {

  /**
   * Deletes objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] delete(List<T> objects);

  /**
   * Delete object.
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
   * Inserts objects.
   *
   * @param <T>
   * @param objects
   * @return
   */
  <T> int[] insert(List<T> objects);

  <T> int insert(T object);

  <T> int[] insert(@SuppressWarnings("unchecked") T... objects);

  <T> InsertResult<T> insertAndGet(List<T> objects);

  <T> InsertResult<T> insertAndGet(T object);

  <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects);

  <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects);

  <T> InsertResult<T> insertAndGetOn(String tableName, T object);

  <T> InsertResult<T> insertAndGetOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  <T> int[] insertOn(String tableName, List<T> objects);

  <T> int insertOn(String tableName, T object);

  <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  <T> int[] merge(List<T> objects);

  <T> int merge(T object);

  <T> int[] merge(@SuppressWarnings("unchecked") T... objects);

  <T> int[] mergeOn(String tableName, List<T> objects);

  <T> int mergeOn(String tableName, T object);

  <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  <T> int[] update(List<T> objects);

  <T> int update(T object);

  <T> int[] update(@SuppressWarnings("unchecked") T... objects);

  <T> int[] updateOn(String tableName, List<T> objects);

  <T> int updateOn(String tableName, T object);

  <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects);

}
