package org.nkjmlab.sorm4j.table.orm;

import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.AND;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.WHERE;

import java.sql.PreparedStatement;
import java.util.List;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.common.container.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.container.Tuple.Tuple3;
import org.nkjmlab.sorm4j.internal.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.sql.metadata.OrmTableMetaData;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.ResultSetStream;
import org.nkjmlab.sorm4j.sql.statement.SelectSql;

public interface TableOrm<T> {

  /**
   * Gets {@link Orm} object
   *
   * @return
   */
  Orm getOrm();

  /**
   * Gets the table name.
   *
   * @return
   */
  String getTableName();

  /**
   * Gets parameter type <T> as object class.
   *
   * @return
   */
  Class<T> getValueType();

  default T readFirst(ParameterizedSql sql) {
    return getOrm().readFirst(getValueType(), sql);
  }

  default T readFirst(String sql, Object... parameters) {
    return getOrm().readFirst(getValueType(), sql, parameters);
  }

  default List<T> readList(ParameterizedSql sql) {
    return getOrm().readList(getValueType(), sql);
  }

  default List<T> readList(String sql, Object... parameters) {
    return getOrm().readList(getValueType(), sql, parameters);
  }

  default T readOne(ParameterizedSql sql) {
    return getOrm().readOne(getValueType(), sql);
  }

  default T readOne(String sql, Object... parameters) {
    return getOrm().readOne(getValueType(), sql, parameters);
  }

  default RowMapper<T> getRowMapper() {
    return getOrm().getRowMapper(getValueType());
  }

  default ResultSetTraverser<List<T>> getResultSetTraverser() {
    return getOrm().getResultSetTraverser(getValueType());
  }

  default OrmTableMetaData getOrmTableMetaData() {
    return getOrm().getOrmTableMetaData(getValueType());
  }

  default boolean exists(T object) {
    return getOrm().exists(object);
  }

  default boolean exists(Object... primaryKeyValues) {
    return getOrm().existsByPrimaryKey(getValueType(), primaryKeyValues);
  }

  default int[] delete(List<T> objects) {
    return getOrm().deleteIn(getTableName(), objects);
  }

  default int delete(T object) {
    return getOrm().deleteIn(getTableName(), object);
  }

  @SuppressWarnings("unchecked")
  default int[] delete(T... objects) {
    return getOrm().deleteIn(getTableName(), objects);
  }

  default int deleteAll() {
    return getOrm().deleteAllIn(getTableName());
  }

  default int deleteByPrimaryKey(Object... primaryKeyValues) {
    return getOrm().deleteByPrimaryKey(getValueType(), primaryKeyValues);
  }

  default int deleteByPrimaryKeyIn(String tableName, Object... primaryKeyValues) {
    return getOrm().deleteByPrimaryKeyIn(tableName, primaryKeyValues);
  }

  default int[] insert(List<T> objects) {
    return getOrm().insertInto(getTableName(), objects);
  }

  default int insert(T object) {
    return getOrm().insertInto(getTableName(), object);
  }

  @SuppressWarnings("unchecked")
  default int[] insert(T... objects) {
    return getOrm().insertInto(getTableName(), objects);
  }

  default int[] insertMapIn(RowMap... objects) {
    return getOrm().insertMapInto(getTableName(), objects);
  }

  default int[] insertMapIn(List<RowMap> objects) {
    return getOrm().insertMapInto(getTableName(), objects);
  }

  default InsertResult insertAndGet(List<T> objects) {
    return getOrm().insertAndGetIn(getTableName(), objects);
  }

  default InsertResult insertAndGet(T object) {
    return getOrm().insertAndGetIn(getTableName(), object);
  }

  @SuppressWarnings("unchecked")
  default InsertResult insertAndGet(T... objects) {
    return getOrm().insertAndGetIn(getTableName(), objects);
  }

  default int[] merge(List<T> objects) {
    return getOrm().mergeIn(getTableName(), objects);
  }

  default int merge(T object) {
    return getOrm().mergeIn(getTableName(), object);
  }

  @SuppressWarnings("unchecked")
  default int[] merge(T... objects) {
    return getOrm().mergeIn(getTableName(), objects);
  }

  default int[] update(List<T> objects) {
    return getOrm().updateWith(getTableName(), objects);
  }

  default int update(T object) {
    return getOrm().updateWith(getTableName(), object);
  }

  @SuppressWarnings("unchecked")
  default int[] update(T... objects) {
    return getOrm().updateWith(getTableName(), objects);
  }

  default int updateByPrimaryKey(RowMap object, Object... primaryKeyValues) {
    return getOrm().updateByPrimaryKeyIn(getTableName(), object, primaryKeyValues);
  }

  default <S> List<Tuple2<T, S>> joinUsing(TableOrm<S> other, String... columns) {
    return getOrm().joinUsing(getValueType(), other.getValueType(), columns);
  }

  default <S> List<Tuple2<T, S>> joinOn(TableOrm<S> other, String onCondition) {
    return getOrm().joinOn(getValueType(), other.getValueType(), onCondition);
  }

  default <S> List<Tuple2<T, S>> leftJoinOn(TableOrm<S> other, String onCondition) {
    return getOrm().leftJoinOn(getValueType(), other.getValueType(), onCondition);
  }

  default List<T> selectAll() {
    return getOrm().selectAll(getValueType());
  }

  default T selectByPrimaryKey(Object... primaryKeyValues) {
    return getOrm().selectByPrimaryKey(getValueType(), primaryKeyValues);
  }

  /**
   * @see {@link #getAllEqualSql(List)}
   * @param tupplesOfNameAndValue
   * @return
   */
  default List<T> selectListAllEqual(Object... tupplesOfNameAndValue) {
    return getOrm().readList(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Object...)}
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectFirstAllEqual(Object... tupplesOfNameAndValue) {
    return getOrm().readFirst(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Object...))}
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectOneAllEqual(Object... tupplesOfNameAndValue) {
    return getOrm().readOne(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * Creates a SQL statement selecting rows which are satisfied all equal condition corresponding to
   * the given arguments.
   *
   * <p><strong>Note:</strong> All the rows will be selected, if length of arguments is zero
   *
   * <p>Example
   *
   * <pre>
   * getAllEqualSql("address", "Tokyo", "age", 20)
   * generates
   * ParameterizedSql("select * from [TABLE_NAME] where address=? and age=?", "Tokyo", 20)
   * </pre>
   *
   * @param tupplesOfNameAndValue is [colum1, value1, colum2, value2,... ]
   * @return
   */
  default ParameterizedSql getAllEqualSql(Object... tupplesOfNameAndValue) {
    int argLength = tupplesOfNameAndValue.length;
    if (argLength == 0) {
      return ParameterizedSql.of(SelectSql.selectStarFrom(getTableName()));
    }
    int pairLength = argLength / 2;
    String[] conditions = new String[pairLength];
    Object[] parameters = new Object[pairLength];

    for (int i = 0; i < pairLength; i++) {
      conditions[i] = tupplesOfNameAndValue[i * 2] + "=?";
      parameters[i] = tupplesOfNameAndValue[i * 2 + 1];
    }

    return ParameterizedSql.withOrderedParameters(
        SelectSql.selectStarFrom(getTableName()) + WHERE + String.join(AND, conditions),
        parameters);
  }

  /**
   * Counts all rows.
   *
   * @return
   */
  default int count() {
    return getOrm().readOne(int.class, "select count(*) from " + getTableName());
  }

  /**
   * Returns {@link ResultSetStream} contains all rows from the table indicated by object class.
   *
   * @return
   */
  default ResultSetStream<T> streamAll() {
    return getOrm().streamAll(getValueType());
  }

  /**
   * Returns an {@link ResultSetStream}.
   *
   * @param sql
   * @return
   */
  default ResultSetStream<T> stream(ParameterizedSql sql) {
    return stream(sql.getSql(), sql.getParameters());
  }

  /**
   * Returns an {@link ResultSetStream}.
   *
   * <p>Parameters will be set according with the correspondence defined in {@link
   * SqlParametersSetter#setParameters(PreparedStatement,Object[])}
   *
   * @param sql
   * @param parameters
   * @return
   */
  default ResultSetStream<T> stream(String sql, Object... parameters) {
    return getOrm().stream(getValueType(), sql, parameters);
  }

  default <S> List<Tuple2<T, S>> join(TableOrm<S> second, String sql, Object... parameters) {
    return getOrm().readTupleList(getValueType(), second.getValueType(), sql, parameters);
  }

  default <S, U> List<Tuple3<T, S, U>> join(
      TableOrm<S> second, TableOrm<U> third, String sql, Object... parameters) {
    return getOrm()
        .readTupleList(
            getValueType(), second.getValueType(), third.getValueType(), sql, parameters);
  }
}
