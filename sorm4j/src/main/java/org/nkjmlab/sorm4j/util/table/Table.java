package org.nkjmlab.sorm4j.util.table;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

@Experimental
public interface Table<T> {

  /**
   * Gets the table schema.
   *
   * @return
   */
  TableSchema getTableSchema();

  /**
   * Gets parameter type <T> as object class.
   *
   * @return
   */
  Class<T> getValueType();

  /**
   * Gets Sorm objects
   *
   * @return
   */
  Sorm getSorm();

  default void createTableAndIndexesIfNotExists() {
    getTableSchema().createTableAndIndexesIfNotExists(getSorm());
  }

  default void createTableIfNotExists() {
    getTableSchema().createTableAndIndexesIfNotExists(getSorm());
  }

  default void createIndexesIfNotExists() {
    getTableSchema().createIndexesIfNotExists(getSorm());
  }

  default void dropTableIfExists() {
    getTableSchema().dropTableIfExists(getSorm());
  }

  default List<T> readAll() {
    return getSorm().readAll(getValueType());
  }


  default T readByPrimaryKey(Object... primaryKeyValues) {
    return getSorm().readByPrimaryKey(getValueType(), primaryKeyValues);
  }


  default T readFirst(ParameterizedSql sql) {
    return getSorm().readFirst(getValueType(), sql);
  }


  default T readFirst(String sql, Object... parameters) {
    return getSorm().readFirst(getValueType(), sql, parameters);
  }



  default List<T> readList(ParameterizedSql sql) {
    return getSorm().readList(getValueType(), sql);
  }


  default List<T> readList(String sql, Object... parameters) {
    return getSorm().readList(getValueType(), sql, parameters);
  }


  default T readOne(ParameterizedSql sql) {
    return getSorm().readOne(getValueType(), sql);
  }


  default T readOne(String sql, Object... parameters) {
    return getSorm().readOne(getValueType(), sql, parameters);
  }

  default RowMapper<T> getRowMapper() {
    return getSorm().getRowMapper(getValueType());
  }


  default ResultSetTraverser<List<T>> getResultSetTraverser() {
    return getSorm().getResultSetTraverser(getValueType());
  }


  default boolean exists(T object) {
    return getSorm().exists(object);
  }


  default int[] delete(List<T> objects) {
    return getSorm().deleteOn(getTableName(), objects);
  }


  default int delete(T object) {
    return getSorm().deleteOn(getTableName(), object);
  }


  default int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().deleteOn(getTableName(), objects);
  }


  default int deleteAll() {
    return getSorm().deleteAllOn(getTableName());
  }


  default int[] insert(List<T> objects) {
    return getSorm().insert(objects);
  }


  default int insert(T object) {
    return getSorm().insert(object);
  }


  default int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().insert(objects);
  }


  default InsertResult<T> insertAndGet(List<T> objects) {
    return getSorm().insertAndGet(objects);
  }


  default InsertResult<T> insertAndGet(T object) {
    return getSorm().insertAndGet(object);
  }


  default InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().insertAndGet(objects);
  }


  default int[] merge(List<T> objects) {
    return getSorm().merge(objects);
  }


  default int merge(T object) {
    return getSorm().merge(object);
  }


  default int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().merge(objects);
  }


  default int[] update(List<T> objects) {
    return getSorm().update(objects);
  }


  default int update(T object) {
    return getSorm().update(object);
  }


  default int[] update(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().update(objects);
  }


  default RowMapper<Map<String, Object>> getRowToMapMapper() {
    return getSorm().getRowToMapMapper();
  }


  default ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser() {
    return getSorm().getResultSetToMapTraverser();
  }


  default Map<String, Object> readMapFirst(ParameterizedSql sql) {
    return getSorm().readMapFirst(sql);
  }


  default Map<String, Object> readMapFirst(String sql, Object... parameters) {
    return getSorm().readMapFirst(sql, parameters);
  }



  default List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    return getSorm().readMapList(sql);
  }


  default List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    return getSorm().readMapList(sql, parameters);
  }


  default Map<String, Object> readMapOne(ParameterizedSql sql) {
    return getSorm().readMapOne(sql);
  }


  default Map<String, Object> readMapOne(String sql, Object... parameters) {
    return getSorm().readMapOne(sql, parameters);
  }


  default String getTableName() {
    return getTableSchema().getTableName();
  }


  default TableMetaData getTableMetaData() {
    return getSorm().getTableMetaData(getTableName());
  }


  default <S> S executeQuery(ParameterizedSql sql, ResultSetTraverser<S> traverser) {
    return getSorm().executeQuery(sql, traverser);
  }


  default <S> List<S> executeQuery(ParameterizedSql sql, RowMapper<S> mapper) {
    return getSorm().executeQuery(sql, mapper);
  }


  default int executeUpdate(String sql, Object... parameters) {
    return getSorm().executeUpdate(sql, parameters);
  }


  default int executeUpdate(ParameterizedSql sql) {
    return getSorm().executeUpdate(sql);
  }


  default List<T> readListAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readList(getValueType(), getAllMatchSql(tuppleOfNameAndValue));
  }

  default T readFirstAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readFirst(getValueType(), getAllMatchSql(tuppleOfNameAndValue));
  }

  default T readOneAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readOne(getValueType(), getAllMatchSql(tuppleOfNameAndValue));
  }

  default List<Map<String, Object>> readMapListAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readMapList(getAllMatchSql(tuppleOfNameAndValue));
  }

  default Map<String, Object> readMapOneAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readMapOne(getAllMatchSql(tuppleOfNameAndValue));
  }

  default Map<String, Object> readMapFirstAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readMapOne(getAllMatchSql(tuppleOfNameAndValue));
  }

  default ParameterizedSql getAllMatchSql(Tuple2<?, ?>... tuppleOfNameAndValue) {
    List<String> conditions = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    Arrays.stream(tuppleOfNameAndValue).forEach(t -> {
      conditions.add(t.getT1() + "=?");
      params.add(t.getT2());
    });

    return ParameterizedSql.of(SelectSql.selectStarFrom(getTableSchema().getTableName()) + WHERE
        + String.join(AND, conditions), params);
  }

  default <S> List<Tuple2<T, S>> join(Table<S> other, String onCondition) {
    return getSorm().readTupleList(getValueType(), other.getValueType(), onCondition);
  }

  default <S> List<Tuple2<T, S>> leftJoin(Table<S> other, String onCondition) {
    return getSorm().readTupleList(getValueType(), other.getValueType(), onCondition);
  }

  /**
   *
   * @see TableMetaData#getColumnAliases()
   */
  default String getColumnAliases() {
    return getTableMetaData().getColumnAliases();
  }

}
