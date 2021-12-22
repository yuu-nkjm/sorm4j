package org.nkjmlab.sorm4j.sql.schema;

import static org.nkjmlab.sorm4j.sql.SqlKeyword.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.SelectSql;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;
import org.nkjmlab.sorm4j.sql.result.Tuple2;

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
  Class<T> getObjectClass();

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
    return getSorm().readAll(getObjectClass());
  }


  default T readByPrimaryKey(Object... primaryKeyValues) {
    return getSorm().readByPrimaryKey(getObjectClass(), primaryKeyValues);
  }


  default T readFirst(ParameterizedSql sql) {
    return getSorm().readFirst(getObjectClass(), sql);
  }


  default T readFirst(String sql, Object... parameters) {
    return getSorm().readFirst(getObjectClass(), sql, parameters);
  }



  default List<T> readList(ParameterizedSql sql) {
    return getSorm().readList(getObjectClass(), sql);
  }


  default List<T> readList(String sql, Object... parameters) {
    return getSorm().readList(getObjectClass(), sql, parameters);
  }


  default T readOne(ParameterizedSql sql) {
    return getSorm().readOne(getObjectClass(), sql);
  }


  default T readOne(String sql, Object... parameters) {
    return getSorm().readOne(getObjectClass(), sql, parameters);
  }


  /**
   * @see OrmLazyReader#readAllLazy(Class)
   */
  default LazyResultSet<T> readAllLazy() {
    return getSorm().readAllLazy(getObjectClass());
  }

  /**
   * @see OrmLazyReader#readLazy(Class, ParameterizedSql)
   */
  default LazyResultSet<T> readLazy(ParameterizedSql sql) {
    return getSorm().readLazy(getObjectClass(), sql);
  }

  /**
   * @see OrmLazyReader#readLazy(Class, String, Object...)
   */
  default LazyResultSet<T> readLazy(String sql, Object... parameters) {
    return getSorm().readLazy(getObjectClass(), sql, parameters);
  }

  default RowMapper<T> getRowMapper() {
    return getSorm().getRowMapper(getObjectClass());
  }


  default ResultSetTraverser<List<T>> getResultSetTraverser() {
    return getSorm().getResultSetTraverser(getObjectClass());
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



  default void acceptPreparedStatementHandler(ParameterizedSql sql,
      ConsumerHandler<PreparedStatement> handler) {
    getSorm().acceptPreparedStatementHandler(sql, handler);
  }


  default <S> S applyPreparedStatementHandler(ParameterizedSql sql,
      FunctionHandler<PreparedStatement, S> handler) {
    return getSorm().applyPreparedStatementHandler(sql, handler);
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
    return getSorm().readList(getObjectClass(), getAllMatchSql(tuppleOfNameAndValue));
  }

  default T readFirstAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readFirst(getObjectClass(), getAllMatchSql(tuppleOfNameAndValue));
  }

  default T readOneAllMatch(Tuple2<?, ?>... tuppleOfNameAndValue) {
    return getSorm().readOne(getObjectClass(), getAllMatchSql(tuppleOfNameAndValue));
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

    return ParameterizedSql.of(
        SelectSql.selectStarFrom(getTableSchema().getTableName()) + WHERE + String.join(AND, conditions),
        params);
  }

  default <S> List<Tuple2<T, S>> join(Table<S> other, ParameterizedSql sql) {
    return getSorm().readTupleList(getObjectClass(), other.getObjectClass(), sql);
  }

  default <S> List<Tuple2<T, S>> join(Table<S> other, String sql, Object... parameters) {
    return getSorm().readTupleList(getObjectClass(), other.getObjectClass(), sql, parameters);
  }

  /**
   *
   * @see TableMetaData#getColumnAliases()
   */
  default String getColumnAliases() {
    return getTableMetaData().getColumnAliases();
  }

}
