package org.nkjmlab.sorm4j.util.table;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.util.List;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

@Experimental
public interface Table<T> {

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

  /**
   * Gets Sorm objects
   *
   * @return
   */
  Sorm getSorm();


  /**
   *
   * @param streamGenerator
   * @param streamHandler
   */
  @Experimental
  default void acceptHandler(FunctionHandler<TypedOrmStreamGenerator<T>, Stream<T>> streamGenerator,
      ConsumerHandler<Stream<T>> streamHandler) {
    getSorm().acceptHandler(
        conn -> streamGenerator.apply(new TypedOrmStreamGenerator<T>(getValueType(), conn)),
        streamHandler);
  }

  /**
   *
   * @param <R>
   * @param streamGenerator
   * @param streamHandler
   * @return
   */
  @Experimental
  default <R> R applyHandler(FunctionHandler<TypedOrmStreamGenerator<T>, Stream<T>> streamGenerator,
      FunctionHandler<Stream<T>, R> streamHandler) {
    return getSorm().applyHandler(
        conn -> streamGenerator.apply(new TypedOrmStreamGenerator<T>(getValueType(), conn)),
        streamHandler);
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


  default TableMetaData getTableMetaData() {
    return getSorm().getTableMetaData(getTableName());
  }

  default boolean exists(T object) {
    return getSorm().exists(object);
  }


  default int[] deleteIn(List<T> objects) {
    return getSorm().deleteIn(getTableName(), objects);
  }


  default int deleteIn(T object) {
    return getSorm().deleteIn(getTableName(), object);
  }


  default int[] deleteIn(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().deleteIn(getTableName(), objects);
  }


  default int deleteAllIn() {
    return getSorm().deleteAllIn(getTableName());
  }


  default int[] insertIn(List<T> objects) {
    return getSorm().insertIn(getTableName(), objects);
  }


  default int insertIn(T object) {
    return getSorm().insertIn(getTableName(), object);
  }


  default int[] insertIn(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().insertIn(getTableName(), objects);
  }


  default InsertResult<T> insertAndGetIn(List<T> objects) {
    return getSorm().insertAndGetIn(getTableName(), objects);
  }


  default InsertResult<T> insertAndGetIn(T object) {
    return getSorm().insertAndGetIn(getTableName(), object);
  }


  default InsertResult<T> insertAndGetIn(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().insertAndGetIn(getTableName(), objects);
  }


  default int[] mergeIn(List<T> objects) {
    return getSorm().mergeIn(getTableName(), objects);
  }


  default int mergeIn(T object) {
    return getSorm().mergeIn(getTableName(), object);
  }


  default int[] mergeIn(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().mergeIn(getTableName(), objects);
  }


  default int[] updateIn(List<T> objects) {
    return getSorm().updateIn(getTableName(), objects);
  }


  default int updateIn(T object) {
    return getSorm().updateIn(getTableName(), object);
  }


  default int[] updateIn(@SuppressWarnings("unchecked") T... objects) {
    return getSorm().updateIn(getTableName(), objects);
  }

  default <S> List<Tuple2<T, S>> join(TableWithSchema<S> other, String onCondition) {
    return getSorm().join(getValueType(), other.getValueType(), onCondition);
  }

  default <S> List<Tuple2<T, S>> leftJoin(TableWithSchema<S> other, String onCondition) {
    return getSorm().leftJoin(getValueType(), other.getValueType(), onCondition);
  }

  default List<T> selectAll() {
    return getSorm().selectAll(getValueType());
  }

  default T selectByPrimaryKey(Object... primaryKeyValues) {
    return getSorm().selectByPrimaryKey(getValueType(), primaryKeyValues);
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default List<T> selectListAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getSorm().readList(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectFirstAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getSorm().readFirst(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
  }

  /**
   * @see {@link #getAllEqualSql(Tuple2...)}
   *
   * @param tupplesOfNameAndValue
   * @return
   */
  default T selectOneAllEqual(Tuple2<?, ?>... tupplesOfNameAndValue) {
    return getSorm().readOne(getValueType(), getAllEqualSql(tupplesOfNameAndValue));
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

  static <T> Table<T> create(Sorm sorm, Class<T> objectClass) {
    return new BasicTable<>(sorm, objectClass);
  }

}
