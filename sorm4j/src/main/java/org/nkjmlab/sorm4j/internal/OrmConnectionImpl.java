package org.nkjmlab.sorm4j.internal;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.FROM;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.JOIN;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.LEFT;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.ON;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.SELECT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.common.JdbcTableMetaData;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.common.Tuple;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.ColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableSql;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.internal.result.InsertResultImpl;
import org.nkjmlab.sorm4j.internal.result.ResultSetStreamOrmConnection;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.BasicRowMap;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.JdbcDatabaseMetaData;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.table.TableMappedOrmConnection;
import org.nkjmlab.sorm4j.util.logger.LogPoint;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.LoggerContext.Category;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

/**
 * A database connection with object-relation mapping function.
 *
 * This instance wraps a {@link java.sql.Connection} object. Instances of this class are not thread
 * safe.
 *
 * @author nkjm
 *
 */
public class OrmConnectionImpl implements OrmConnection {

  private static final Supplier<int[]> EMPTY_INT_SUPPLIER = () -> new int[0];

  private final SormContextImpl sormContext;

  private final Connection connection;

  /**
   * Creates a instance that will use the default cache for table-object and column-object
   * sormConfig.
   *
   * @param connection {@link java.sql.Connection} object to be used
   * @param sormContext
   */
  public OrmConnectionImpl(Connection connection, SormContextImpl sormContext) {
    this.connection = connection;
    this.sormContext = sormContext;
  }

  @Override
  public void close() {
    try {
      getJdbcConnection().close();
    } catch (SQLException e) {
      sormContext.getLoggerContext().getLogger(OrmConnectionImpl.class)
          .warn("jdbc connection close error");
    }
  }

  @Override
  public void commit() {
    Try.runOrElseThrow(() -> getJdbcConnection().commit(), Try::rethrow);
  }


  // private String createInsertSql(String tableName, List<String> cols) {
  // String ps = String.join(",",
  // Stream.generate(() -> "?").limit(cols.size()).collect(Collectors.toList()));
  // String sql =
  // "insert into " + tableName + " (" + String.join(",", cols) + ") VALUES (" + ps + ")";
  // return sql;
  // }

  @Override
  public <T> int[] delete(List<T> objects) {
    return applytoArray(objects, array -> delete(array));
  }


  /**
   * Deletes an object in the database. The object will be identified using its mapped table's
   * primary key.
   *
   */
  @Override
  public <T> int delete(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return executeUpdate(mapping.getSql().getDeleteSql(), mapping.getDeleteParameters(object));
  }



  /**
   * Updates a batch of objects in the database. The objects will be identified using their matched
   * table's primary keys. If no primary keys are defined in a given object, a RuntimeException will
   * be thrown.
   *
   */
  @Override
  public <T> int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.batch(connection,
        mapping.getSql().getDeleteSql(), obj -> mapping.getDeleteParameters(obj), objects),
        EMPTY_INT_SUPPLIER);
  }



  @Override
  public <T> int deleteAll(Class<T> objectClass) {
    return deleteAllIn(getTableName(objectClass));
  }

  @Override
  public int deleteAllIn(String tableName) {
    return executeUpdate("DELETE FROM " + tableName);
  }

  @Override
  public <T> int deleteByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    final String sql = getTableSql(objectClass).getDeleteSql();
    return executeUpdate(sql, primaryKeyValues);
  }

  @Override
  public <T> int deleteByPrimaryKeyIn(String tableName, Object... primaryKeyValues) {
    final String sql = getTableSql(tableName).getDeleteSql();
    return executeUpdate(sql, primaryKeyValues);
  }

  @Override
  public <T> int[] deleteIn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> deleteIn(tableName, array));
  }

  @Override
  public <T> int deleteIn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return executeUpdate(mapping.getSql().getDeleteSql(), mapping.getDeleteParameters(object));
  }

  @Override
  public <T> int[] deleteIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects, mapping -> mapping.batch(connection,
        mapping.getSql().getDeleteSql(), obj -> mapping.getDeleteParameters(obj), objects),
        EMPTY_INT_SUPPLIER);
  }


  /**
   * Execute sql function with table name. objects when objects[0] is null,
   * {@code NullPointerException} are throw.
   */
  private final <T, R> R execSqlIfParameterExists(String tableName, T[] objects,
      Function<SqlParametersToTableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    SqlParametersToTableMapping<T> mapping =
        getCastedTableMapping(tableName, objects[0].getClass());
    return sqlFunction.apply(mapping);
  }

  /**
   * Execute sql function. objects when objects[0] is null, {@code NullPointerException} are throw.
   */
  private final <T, R> R execSqlIfParameterExists(T[] objects,
      Function<SqlParametersToTableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(objects[0].getClass());
    return sqlFunction.apply(mapping);
  }

  @Override
  public <T> T executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      ResultSetTraverser<T> traverser) {
    return executeQueryAndClose(connection, traverser, statementSupplier);
  }


  @Override
  public <T> List<T> executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      RowMapper<T> rowMapper) {
    return executeQueryAndClose(connection, ResultSetTraverser.of(rowMapper), statementSupplier);
  }

  @Override
  public <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> resultSetTraverser) {
    return executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql.getSql(), sql.getParameters(),
        resultSetTraverser);
  }

  @Override
  public <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> rowMapper) {
    return executeQuery(sql, ResultSetTraverser.of(rowMapper));
  }

  @Override
  public int executeUpdate(ParameterizedSql sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    final int ret = executeUpdateAndClose(getLoggerContext(), connection, getSqlParametersSetter(),
        getPreparedStatementSupplier(), sql, parameters);
    return ret;
  }


  @Override
  public <T> boolean exists(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return existsHelper(mapping.getSql(), mapping.getPrimaryKeyParameters(object));
  }


  @Override
  public <T> boolean exists(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return existsHelper(mapping.getSql(), mapping.getPrimaryKeyParameters(object));
  }

  @Override
  public <T> boolean exists(String tableName, Object... primaryKeyValues) {
    return existsHelper(getTableSql(tableName), primaryKeyValues);
  }

  @Override
  public <T> boolean exists(Class<T> type, Object... primaryKeyValues) {
    return existsHelper(getTableSql(type), primaryKeyValues);
  }


  private <T> boolean existsHelper(TableSql tableSql, Object... primaryKeyValues) {
    final String sql = tableSql.getExistsSql();
    return readFirst(Integer.class, sql, primaryKeyValues) != null;
  }



  private <T> SqlParametersToTableMapping<T> getCastedTableMapping(String tableName,
      Class<?> objectClass) {
    return sormContext.getCastedTableMapping(connection, tableName, objectClass);
  }


  private <T> SqlParametersToTableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return sormContext.getCastedTableMapping(connection, objectClass);
  }

  <T> SqlResultToColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    return sormContext.getColumnsMapping(objectClass);
  }

  private ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return sormContext.getColumnValueToJavaObjectConverter();
  }

  private ColumnValueToMapValueConverters getColumnValueToMapValueConverter() {
    return sormContext.getColumnValueToMapValueConverter();
  }


  @Override
  public SormContext getContext() {
    return sormContext;
  }


  @Override
  public Connection getJdbcConnection() {
    return connection;
  }


  @Override
  public JdbcDatabaseMetaData getJdbcDatabaseMetaData() {
    try {
      java.sql.DatabaseMetaData metaData = connection.getMetaData();
      return JdbcDatabaseMetaData.of(metaData);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  private LoggerContext getLoggerContext() {
    return sormContext.getLoggerContext();
  }


  private PreparedStatementSupplier getPreparedStatementSupplier() {
    return sormContext.getPreparedStatementSupplier();
  }

  @Override
  public <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> objectClass) {
    return resultSet -> traverseAndMapToList(objectClass, resultSet);
  }

  @Override
  public <T> RowMapper<T> getRowMapper(Class<T> objectClass) {
    return (resultSet, rowNum) -> mapRowToObject(objectClass, resultSet);
  }

  private SqlParametersSetter getSqlParametersSetter() {
    return sormContext.getSqlParametersSetter();
  }


  /**
   * Gets {@link SqlParametersToTableMapping}. This method is for internal use.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  public <T> SqlParametersToTableMapping<T> getTableMapping(Class<T> objectClass) {
    return sormContext.getTableMapping(connection, objectClass);
  }

  @Override
  public TableSql getTableSql(Class<?> objectClass) {
    return getTableMapping(objectClass).getSql();
  }

  @Override
  public TableSql getTableSql(String tableName) {
    return sormContext.getTableSql(connection, getJdbcTableMetaData(tableName));
  }

  @Override
  public TableMetaData getTableMetaData(Class<?> objectClass) {
    return getTableMapping(objectClass).getTableMetaData();
  }

  @Override
  public JdbcTableMetaData getJdbcTableMetaData(String tableName) {
    return sormContext.getJdbcTableMetaData(connection, tableName);
  }



  @Override
  public String getTableName(Class<?> objectClass) {
    return sormContext.getTableName(connection, objectClass);
  }



  @Override
  public <T> int[] insert(List<T> objects) {
    return applytoArray(objects, array -> insert(array));
  }


  @Override
  public <T> int insert(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return executeUpdate(mapping.getSql().getInsertSql(), mapping.getInsertParameters(object));
  }

  @Override
  public <T> int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> InsertResult insertAndGet(List<T> objects) {
    return applytoArray(objects, array -> insertAndGet(array));
  }


  @Override
  public <T> InsertResult insertAndGet(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }

  @Override
  public <T> InsertResult insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.EMPTY_INSERT_RESULT);
  }


  @Override
  public <T> InsertResult insertAndGetIn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertAndGetIn(tableName, array));
  }

  @Override
  public <T> InsertResult insertAndGetIn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }

  @Override
  public <T> InsertResult insertAndGetIn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.EMPTY_INSERT_RESULT);
  }


  @Override
  public <T> int[] insertIn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertIn(tableName, array));
  }


  @Override
  public <T> int insertIn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return executeUpdate(mapping.getSql().getInsertSql(), mapping.getInsertParameters(object));
  }


  @Override
  public <T> int[] insertIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public int[] insertMapIn(String tableName, List<RowMap> objects) {
    boolean origAutoCommit = getAutoCommit(connection);
    try {
      setAutoCommit(connection, false);
      int[] ret = objects.stream().mapToInt(o -> insertMapIn(tableName, o)).toArray();
      setAutoCommit(connection, true);
      commit();
      return ret;
    } finally {
      commitOrRollback(connection, origAutoCommit);
      setAutoCommit(origAutoCommit);
    }
  }


  @Override
  public int insertMapIn(String tableName, RowMap object) {
    String sql = getTableSql(tableName).getInsertSql();
    return executeUpdate(sql, toInsertParameters(tableName, object));
  }

  private Object[] toInsertParameters(String tableName, RowMap object) {
    List<String> cols = getJdbcTableMetaData(tableName).getNotAutoGeneratedColumns();
    return cols.stream().map(col -> object.get(col)).toArray();
  }

  @Override
  public int[] insertMapIn(String tableName, RowMap... objects) {
    return insertMapIn(tableName, Arrays.asList(objects));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> join(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    return readTupleList(t1, t2, sql, parameters);
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> join(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String sql, Object... parameters) {
    return readTupleList(t1, t2, t3, sql, parameters);
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> joinOn(Class<T1> t1, Class<T2> t2, String onCondition) {
    return join(t1, t2, joinSql(JOIN, t1, t2, ON + onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> joinOn(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String t1T2OnCondition, String t2T3OnCondition) {
    return join(t1, t2, t3, joinSql(JOIN, t1, t2, t1T2OnCondition, t3, t2T3OnCondition));
  }


  @Override
  public <T1, T2> List<Tuple2<T1, T2>> joinUsing(Class<T1> t1, Class<T2> t2, String... columns) {
    return join(t1, t2,
        joinSql(JOIN, t1, t2, " using (" + SelectSql.joinCommaAndSpace(columns) + ")"));
  }


  private <T1, T2, T3> String joinSql(String joinType, Class<T1> t1, Class<T2> t2,
      String joinCondition) {
    TableMetaData t1m = getTableMapping(t1).getTableMetaData();
    TableMetaData t2m = getTableMapping(t2).getTableMetaData();
    String sql = SELECT + String.join(",", t1m.getColumnAliases()) + ", "
        + String.join(",", t2m.getColumnAliases()) + FROM + t1m.getTableName() + joinType
        + t2m.getTableName() + joinCondition;
    return sql;
  }

  private <T1, T2, T3> String joinSql(String joinType, Class<T1> t1, Class<T2> t2,
      String t1T2OnCondition, Class<T3> t3, String t2T3OnCondition) {
    TableMetaData t2m = getTableMapping(t2).getTableMetaData();
    TableMetaData t3m = getTableMapping(t3).getTableMetaData();
    String joinCondition = joinType + t2m.getTableName() + ON + t1T2OnCondition + joinType
        + t3m.getTableName() + ON + t2T3OnCondition;
    return joinSql(joinType, t1, t2, t3, joinCondition);
  }

  private <T1, T2, T3> String joinSql(String joinType, Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String joinCondition) {
    TableMetaData t1m = getTableMapping(t1).getTableMetaData();
    TableMetaData t2m = getTableMapping(t2).getTableMetaData();
    TableMetaData t3m = getTableMapping(t3).getTableMetaData();
    String sql = SELECT + String.join(",", t1m.getColumnAliases()) + ", "
        + String.join(",", t2m.getColumnAliases()) + ", " + String.join(",", t3m.getColumnAliases())
        + FROM + t1m.getTableName() + joinCondition;
    return sql;
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> leftJoinOn(Class<T1> t1, Class<T2> t2, String onCondition) {
    return join(t1, t2, joinSql(LEFT + JOIN, t1, t2, ON + onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoinOn(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String t1T2OnCondition, String t2T3OnCondition) {
    return join(t1, t2, t3, joinSql(LEFT + JOIN, t1, t2, t1T2OnCondition, t3, t2T3OnCondition));
  }

  public <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return resultSet.next() ? mapRowToObject(objectClass, resultSet) : null;
  }

  private final <T> T loadResultContainerObject(final Class<T> objectClass,
      final ResultSet resultSet) throws SQLException {
    return getColumnsMapping(objectClass).loadResultContainerObject(resultSet);
  }

  public final <T> List<T> loadResultContainerObjectList(final Class<T> objectClass,
      final ResultSet resultSet) throws SQLException {
    return getColumnsMapping(objectClass).traverseAndMap(resultSet);
  }


  private final <T> List<T> loadSupportedReturnedTypeList(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    final List<T> ret = new ArrayList<>();
    final int sqlType = getOneSqlType(objectClass, resultSet);
    while (resultSet.next()) {
      ret.add(toSupportedReturnedTypeObject(resultSet, sqlType, objectClass));
    }
    return ret;

  }

  private RowMap mapRowToMap(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
    return toSingleRowMap(resultSet, ct.getColumns(), ct.getColumnTypes());
  }

  @SuppressWarnings("unchecked")
  public <T> T mapRowToObject(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    if (objectClass.equals(RowMap.class)) {
      return (T) mapRowToMap(resultSet);
    } else if (getColumnValueToJavaObjectConverter().isSupportedReturnedType(objectClass)) {
      return toSupportedReturnedTypeObject(resultSet, getOneSqlType(objectClass, resultSet),
          objectClass);
    } else {
      return loadResultContainerObject(objectClass, resultSet);
    }
  }

  @Override
  public <T> int[] merge(List<T> objects) {
    return applytoArray(objects, array -> merge(array));
  }

  @Override
  public <T> int merge(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return executeUpdate(mapping.getSql().getMergeSql(), mapping.getMergeParameters(object));
  }


  @Override
  public <T> int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.merge(getJdbcConnection(), objects),
        EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> int[] mergeIn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> mergeIn(tableName, array));
  }

  @Override
  public <T> int mergeIn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return executeUpdate(mapping.getSql().getMergeSql(), mapping.getMergeParameters(object));
  }

  @Override
  public <T> int[] mergeIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.merge(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> ResultSetStream<T> stream(Class<T> objectClass, ParameterizedSql sql) {
    return stream(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> ResultSetStream<T> stream(Class<T> objectClass, String sql, Object... parameters) {
    return new ResultSetStreamOrmConnection<T>(this, objectClass, sql, parameters);
  }

  @Override
  public <T> ResultSetStream<T> streamAll(Class<T> type) {
    return stream(type, getTableMapping(type).getSql().getSelectAllSql());
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, ParameterizedSql sql) {
    return readFirst(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, parameters,
        resultSet -> loadFirst(objectClass, resultSet));
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, ParameterizedSql sql) {
    return readList(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, parameters,
        resultSet -> traverseAndMapToList(objectClass, resultSet));
  }

  @Override
  public <T> T readOne(Class<T> objectClass, ParameterizedSql sql) {
    return readOne(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readOne(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, parameters, resultSet -> {
          T ret = null;
          if (resultSet.next()) {
            ret = mapRowToObject(objectClass, resultSet);
          } else {
            throw new SormException(ParameterizedStringFormatter.LENGTH_256.format(
                "Try to read an unique [{}] object but no result returned. sql=[{}],params=[{}]",
                objectClass.getName(), sql, parameters));
          }
          if (resultSet.next()) {
            throw new SormException(ParameterizedStringFormatter.LENGTH_256.format(
                "Try to read an unique [{}] object but non-unique result returned. sql=[{}],params=[{}]",
                objectClass.getName(), sql, parameters));
          }
          return ret;
        });
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, ParameterizedSql sql) {
    return readTupleList(t1, t2, t3, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, String sql, Object... parameters) {
    List<Tuple3<T1, T2, T3>> ret = executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, parameters, resultSet -> {
          final List<Tuple3<T1, T2, T3>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuple.of(loadResultContainerObject(t1, resultSet),
                loadResultContainerObject(t2, resultSet),
                loadResultContainerObject(t3, resultSet)));
          }
          return ret1;
        });
    return ret;
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2,
      ParameterizedSql sql) {
    return readTupleList(t1, t2, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    List<Tuple2<T1, T2>> ret = executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, parameters, resultSet -> {
          final List<Tuple2<T1, T2>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuple.of(loadResultContainerObject(t1, resultSet),
                loadResultContainerObject(t2, resultSet)));
          }
          return ret1;
        });
    return ret;
  }

  @Override
  public void rollback() {
    try {
      getJdbcConnection().rollback();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }

  }

  @Override
  public final <T> List<T> selectAll(Class<T> objectClass) {
    return readList(objectClass, getCastedTableMapping(objectClass).getSql().getSelectAllSql());
  }

  @Override
  public <T> T selectByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    final String sql = getTableMapping(objectClass).getSql().getSelectByPrimaryKeySql();
    return executeQueryAndClose(getLoggerContext(), getJdbcConnection(),
        getPreparedStatementSupplier(), getSqlParametersSetter(), sql, primaryKeyValues,
        resultSet -> {
          return resultSet.next() ? getColumnsMapping(objectClass)
              .loadResultContainerObjectByPrimaryKey(objectClass, resultSet) : null;
        });
  }

  @Override
  public void setAutoCommit(boolean autoCommit) {
    try {
      getJdbcConnection().setAutoCommit(autoCommit);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }

  }

  /**
   * Converts the result from database to a {@link RowMap} objects. The data of the column is
   * extracted by corresponding column types.
   *
   * <p>
   * Keys in the results is depending on {@link ColumnValueToMapKeyConverter#convertToKey(String)}.
   *
   * @param resultSet
   * @param columns
   * @param columnTypes SQL types from {@link java.sql.Types}
   *
   * @return
   * @throws SQLException
   */

  private RowMap toSingleRowMap(ResultSet resultSet, String[] columns, int[] columnTypes)
      throws SQLException {
    final int colsNum = columns.length;
    final RowMap ret = new BasicRowMap(colsNum + 1, 1.0f);
    for (int i = 1; i <= colsNum; i++) {
      ret.put(columns[i - 1],
          getColumnValueToMapValueConverter().convertToValue(resultSet, i, columnTypes[i - 1]));
    }
    return ret;
  }

  /**
   * Converts to a single native object of the given object class.
   *
   * @param resultSet
   * @param columnType
   * @param objectClass
   *
   * @param <T>
   * @return
   * @throws SQLException
   */

  private <T> T toSupportedReturnedTypeObject(ResultSet resultSet, int sqlType,
      Class<T> objectClass) throws SQLException {
    return getColumnValueToJavaObjectConverter().convertTo(resultSet, 1, sqlType, objectClass);
  }


  @SuppressWarnings("unchecked")
  private <T> List<T> traverseAndMapToList(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    return objectClass.equals(RowMap.class) ? (List<T>) traverseAndMapToRowMapList(resultSet)
        : (getColumnValueToJavaObjectConverter().isSupportedReturnedType(objectClass)
            ? loadSupportedReturnedTypeList(objectClass, resultSet)
            : loadResultContainerObjectList(objectClass, resultSet));
  }


  private List<RowMap> traverseAndMapToRowMapList(ResultSet resultSet) throws SQLException {
    final List<RowMap> ret = new ArrayList<>();
    final ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
    while (resultSet.next()) {
      ret.add(toSingleRowMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
    }
    return ret;
  }

  @Override
  public <T> int[] update(List<T> objects) {
    return applytoArray(objects, array -> update(array));
  }


  /**
   * Updates an object in the database. The object will be identified using its mapped table's
   * primary key. If no primary keys are defined in the mapped table, a {@link RuntimeException}
   * will be thrown.
   */
  @Override
  public <T> int update(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return executeUpdate(mapping.getSql().getUpdateSql(), mapping.getUpdateParameters(object));
  }

  @Override
  public <T> int updateByPrimaryKey(Class<T> clazz, RowMap object, Object... primaryKeyValues) {
    final String sql = getTableSql(clazz).getUpdateSql(object);
    List<Object> params = new ArrayList<>(object.values());
    params.addAll(Arrays.asList(primaryKeyValues));
    return executeUpdate(sql, params.toArray());
  }

  @Override
  public int updateByPrimaryKeyIn(String tableName, RowMap object, Object... primaryKeyValues) {
    final String sql = getTableSql(tableName).getUpdateSql(object);
    List<Object> params = new ArrayList<>(object.values());
    params.addAll(Arrays.asList(primaryKeyValues));
    return executeUpdate(sql, params.toArray());
  }


  @Override
  public <T> int[] update(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.batch(connection,
        mapping.getSql().getUpdateSql(), obj -> mapping.getUpdateParameters(obj), objects),
        EMPTY_INT_SUPPLIER);
  }


  @Override
  public <T> int[] updateIn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> updateIn(tableName, array));
  }


  @Override
  public <T> int updateIn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
    return executeUpdate(mapping.getSql().getUpdateSql(), mapping.getUpdateParameters(object));
  }

  @Override
  public <T> int[] updateIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects, mapping -> mapping.batch(connection,
        mapping.getSql().getUpdateSql(), obj -> mapping.getUpdateParameters(obj), objects),
        EMPTY_INT_SUPPLIER);
  }

  @SuppressWarnings("unchecked")
  private static <T, R> R applytoArray(List<T> objects, Function<T[], R> sqlFunc) {
    return sqlFunc.apply((T[]) objects.toArray(Object[]::new));
  }

  private static Optional<LogPoint> createLogPointAndLogBeforeSql(LoggerContext loggerContext,
      LoggerContext.Category category, Class<?> clazz, Connection connection, String sql,
      Object... parameters) {
    Optional<LogPoint> lp = loggerContext.createLogPoint(category, clazz);
    lp.ifPresent(_lp -> _lp.logBeforeSql(connection, sql, parameters));
    return lp;
  }

  private static <R> R executeQueryAndClose(Connection connection,
      ResultSetTraverser<R> resultSetTraverser,
      FunctionHandler<Connection, PreparedStatement> statementSupplier) {
    try (PreparedStatement stmt = statementSupplier.apply(connection);
        ResultSet resultSet = stmt.executeQuery()) {
      return resultSetTraverser.traverseAndMap(resultSet);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  private static <R> R executeQueryAndClose(LoggerContext loggerContext, Connection connection,
      PreparedStatementSupplier statementSupplier, SqlParametersSetter sqlParametersSetter,
      String sql, Object[] parameters, ResultSetTraverser<R> resultSetTraverser) {
    Optional<LogPoint> lp = createLogPointAndLogBeforeSql(loggerContext, Category.EXECUTE_QUERY,
        OrmConnectionImpl.class, connection, sql, parameters);
    try (PreparedStatement stmt = statementSupplier.prepareStatement(connection, sql)) {
      sqlParametersSetter.setParameters(stmt, parameters);
      ResultSet resultSet = stmt.executeQuery();
      R ret = resultSetTraverser.traverseAndMap(resultSet);
      lp.ifPresent(_lp -> _lp.logAfterQuery(ret));
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  private static int executeUpdateAndClose(LoggerContext loggerContext, Connection connection,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier statementSupplier,
      String sql, Object[] parameters) {
    Optional<LogPoint> lp = createLogPointAndLogBeforeSql(loggerContext, Category.EXECUTE_QUERY,
        OrmConnectionImpl.class, connection, sql, parameters);
    try (PreparedStatement stmt = statementSupplier.prepareStatement(connection, sql)) {
      sqlParametersSetter.setParameters(stmt, parameters);
      int ret = stmt.executeUpdate();
      lp.ifPresent(_lp -> _lp.logAfterUpdate(ret));
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  private static int getOneSqlType(Class<?> objectClass, ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    if (metaData.getColumnCount() != 1) {
      throw new SormException("ResultSet returned [" + metaData.getColumnCount()
          + "] columns but 1 column was expected to load data into an instance of ["
          + objectClass.getName() + "]");
    }
    return metaData.getColumnType(1);
  }

  public static boolean getAutoCommit(Connection connection) {
    try {
      return connection.getAutoCommit();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public static void commitOrRollback(Connection connection, boolean origAutoCommit) {
    try {
      if (origAutoCommit) {
        connection.commit();
      } else {
        connection.rollback();
      }
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public static void setAutoCommit(Connection connection, boolean autoCommit) {
    try {
      connection.setAutoCommit(autoCommit);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public static class ColumnsAndTypes {

    public static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) throws SQLException {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int colNum = metaData.getColumnCount();
      String[] columns = new String[colNum];
      int[] columnTypes = new int[colNum];
      for (int i = 1; i <= colNum; i++) {
        columns[i - 1] = metaData.getColumnLabel(i);
        columnTypes[i - 1] = metaData.getColumnType(i);
      }
      return new ColumnsAndTypes(columns, columnTypes);
    }

    private final String[] columns;

    private final int[] columnTypes;

    public ColumnsAndTypes(String[] columns, int[] columnTypes) {
      this.columns = columns;
      this.columnTypes = columnTypes;
    }

    public String[] getColumns() {
      return columns;
    }

    public int[] getColumnTypes() {
      return columnTypes;
    }


  }

  @Override
  public <T> TableMappedOrmConnection<T> mapToTable(Class<T> type) {
    return new TableMappedOrmConnectionImpl<>(this, type);
  }

  @Override
  public <T> TableMappedOrmConnection<T> mapToTable(Class<T> type, String tableName) {
    return new TableMappedOrmConnectionImpl<>(this, type, tableName);
  }

}
