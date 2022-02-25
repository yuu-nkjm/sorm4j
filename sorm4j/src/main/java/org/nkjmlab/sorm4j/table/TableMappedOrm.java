package org.nkjmlab.sorm4j.table;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.sql.PreparedStatement;
import java.util.List;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

@Experimental
public interface TableMappedOrm<T> {

  /**
   * Gets Sorm objects
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


  default TableMetaData getTableMetaData() {
    return getOrm().getTableMetaData(getTableName());
  }

  default boolean exists(T object) {
    return getOrm().exists(object);
  }

  default boolean exists(Object... primaryKeyValues) {
    return getOrm().exists(getValueType(), primaryKeyValues);
  }


  default int[] delete(List<T> objects) {
    return getOrm().deleteIn(getTableName(), objects);
  }


  default int delete(T object) {
    return getOrm().deleteIn(getTableName(), object);
  }


  default int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return getOrm().deleteIn(getTableName(), objects);
  }


  default int deleteAll() {
    return getOrm().deleteAllIn(getTableName());
  }


  default int[] insert(List<T> objects) {
    return getOrm().insertIn(getTableName(), objects);
  }


  default int insert(T object) {
    return getOrm().insertIn(getTableName(), object);
  }


  default int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return getOrm().insertIn(getTableName(), objects);
  }


  default InsertResult<T> insertAndGet(List<T> objects) {
    return getOrm().insertAndGetIn(getTableName(), objects);
  }


  default InsertResult<T> insertAndGet(T object) {
    return getOrm().insertAndGetIn(getTableName(), object);
  }


  default InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return getOrm().insertAndGetIn(getTableName(), objects);
  }


  default int[] merge(List<T> objects) {
    return getOrm().mergeIn(getTableName(), objects);
  }


  default int merge(T object) {
    return getOrm().mergeIn(getTableName(), object);
  }


  default int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return getOrm().mergeIn(getTableName(), objects);
  }


  default int[] update(List<T> objects) {
    return getOrm().updateIn(getTableName(), objects);
  }


  default int update(T object) {
    return getOrm().updateIn(getTableName(), object);
  }


  default int[] update(@SuppressWarnings("unchecked") T... objects) {
    return getOrm().updateIn(getTableName(), objects);
  }

  default <S> List<Tuple2<T, S>> joinUsing(Table<S> other, String... columns) {
    return getOrm().joinUsing(getValueType(), other.getValueType(), columns);
  }

  default <S> List<Tuple2<T, S>> joinOn(Table<S> other, String onCondition) {
    return getOrm().joinOn(getValueType(), other.getValueType(), onCondition);
  }

  default <S> List<Tuple2<T, S>> leftJoinOn(Table<S> other, String onCondition) {
    return getOrm().leftJoinOn(getValueType(), other.getValueType(), onCondition);
  }

  default List<T> selectAll() {
    return getOrm().selectAll(getValueType());
  }

  default T selectByPrimaryKey(Object... primaryKeyValues) {
    return getOrm().selectByPrimaryKey(getValueType(), primaryKeyValues);
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default List<T> selectListAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getOrm().readList(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectFirstAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getOrm().readFirst(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectOneAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getOrm().readOne(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * Creates a SQL statement selecting rows which are satisfied all condition corresponding to the
   * given arguments.
   *
   * <strong>Note:</strong> All the rows will be selected, if length of arguments is zero
   *
   * Example
   *
   * <pre>
   * getAllEqualSql("address", "Tokyo", "age", 20)
   * generates
   * ParameterizedSql("select * from [TABLE_NAME] where address=? and age=?", "Tokyo", 20)
   * </pre>
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default ParameterizedSql getAllEqualSql(Tuple2<?, ?>... tupplesOfNameAndValue) {
    int argLength = tupplesOfNameAndValue.length;
    if (argLength == 0) {
      return ParameterizedSql.of(SelectSql.selectStarFrom(getTableName()));
    }
    String[] conditions = new String[argLength];
    Object[] parameters = new Object[argLength];

    for (int i = 0; i < argLength; i++) {
      Tuple2<?, ?> tuple2 = tupplesOfNameAndValue[i];
      conditions[i] = tuple2.getT1() + "=?";
      parameters[i] = tuple2.getT2();
    }

    return ParameterizedSql.of(
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
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement,Object[])}
   *
   * @param sql
   * @param parameters
   * @return
   */
  default ResultSetStream<T> stream(String sql, Object... parameters) {
    return getOrm().stream(getValueType(), sql, parameters);
  }

}
