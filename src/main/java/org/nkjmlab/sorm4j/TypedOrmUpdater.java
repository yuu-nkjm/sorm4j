package org.nkjmlab.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.result.InsertResult;

/**
 * A typed interface for updating database.
 *
 * @author nkjm
 *
 */

public interface TypedOrmUpdater<T> {

  int[] delete(List<T> objects);

  int[] delete(@SuppressWarnings("unchecked") T... objects);

  int delete(T object);

  int deleteAll();

  int[] deleteOn(String tableName, List<T> objects);

  int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  int deleteOn(String tableName, T object);

  int deleteAllOn(String tableName);

  int[] insert(List<T> objects);

  int[] insert(@SuppressWarnings("unchecked") T... objects);

  int insert(T object);

  InsertResult<T> insertAndGet(List<T> objects);

  InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects);

  InsertResult<T> insertAndGet(T object);

  InsertResult<T> insertAndGetOn(String tableName, List<T> objects);

  InsertResult<T> insertAndGetOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  InsertResult<T> insertAndGetOn(String tableName, T object);

  int[] insertOn(String tableName, List<T> objects);

  int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  int insertOn(String tableName, T object);

  int[] merge(List<T> objects);

  int[] merge(@SuppressWarnings("unchecked") T... objects);

  int merge(T object);

  int[] mergeOn(String tableName, List<T> objects);

  int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  int mergeOn(String tableName, T object);

  int[] update(List<T> objects);

  int[] update(@SuppressWarnings("unchecked") T... objects);

  int update(T object);

  int[] updateOn(String tableName, List<T> objects);

  int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects);

  int updateOn(String tableName, T object);



}
