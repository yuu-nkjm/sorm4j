package org.nkjmlab.sorm4j;

import java.util.List;


public interface OrmUpdater {

  <T> int[] delete(List<T> objects);

  <T> int delete(T object);

  <T> int[] delete(@SuppressWarnings("unchecked") T... objects);

  <T> int[] deleteOn(String tableName, List<T> objects);

  <T> int deleteOn(String tableName, T object);

  <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects);

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
