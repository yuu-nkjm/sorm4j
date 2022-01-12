package org.nkjmlab.sorm4j.internal;

import static org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor.*;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.internal.mapping.SqlParametersToTableMapping;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.internal.sql.result.InsertResultImpl;
import org.nkjmlab.sorm4j.internal.sql.result.LazyResultSetImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.lowlevel_orm.FunctionHandler;
import org.nkjmlab.sorm4j.lowlevel_orm.ResultSetTraverser;
import org.nkjmlab.sorm4j.lowlevel_orm.RowMapper;
import org.nkjmlab.sorm4j.mapping.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.mapping.ColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.result.Tuple;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.result.Tuple3;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.BasicCommand;
import org.nkjmlab.sorm4j.util.command.Command;
import org.nkjmlab.sorm4j.util.command.NamedParameterCommand;
import org.nkjmlab.sorm4j.util.command.OrderedParameterCommand;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.LoggerContext.LogPoint;

/**
 * A database connection with object-relation mapping function. The main class for the ORMapper
 * engine.
 *
 * This instance wraps a {@link java.sql.Connection} object. OrmMapper instances are not thread
 * safe, in particular because {@link java.sql.Connection} objects are not thread safe.
 *
 * @author nkjm
 *
 */
public class OrmConnectionImpl implements OrmConnection {

  private static final Supplier<int[]> EMPTY_INT_SUPPLIER = () -> new int[0];
  private final SormContextImpl sormContext;
  private final Connection connection;
  private final List<LazyResultSet<?>> lazyResultSets = new ArrayList<>();

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

  private LoggerContext getLoggerConfig() {
    return sormContext.getLoggerContext();
  }

  @Override
  public void begin() {
    begin(getTransactionIsolationLevel());
  }


  @Override
  public void begin(int isolationLevel) {
    setAutoCommit(false);
    setTransactionIsolation(isolationLevel);
  }

  @Override
  public void close() {
    Try.runOrElseThrow(() -> {
      lazyResultSets.forEach(rs -> rs.close());
      lazyResultSets.clear();
      getJdbcConnection().close();
    }, Try::rethrow);
  }


  @Override
  public void commit() {
    Try.runOrElseThrow(() -> getJdbcConnection().commit(), Try::rethrow);
  }

  @Override
  public Command createCommand(ParameterizedSql sql) {
    return BasicCommand.from(this, sql.getSql()).addParameter(sql.getParameters());
  }

  @Override
  public BasicCommand createCommand(String sql) {
    return BasicCommand.from(this, sql);
  }

  @Override
  public NamedParameterCommand createCommand(String sql, Map<String, Object> parameters) {
    return NamedParameterCommand.from(this, sql).bindAll(parameters);
  }

  @Override
  public OrderedParameterCommand createCommand(String sql, Object... parameters) {
    return OrderedParameterCommand.from(this, sql).addParameter(parameters);
  }


  @Override
  public <T> int[] delete(List<T> objects) {
    return applytoArray(objects, array -> delete(array));
  }

  @Override
  public <T> int delete(T object) {
    return getCastedTableMapping(object.getClass()).delete(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> int deleteAll(Class<T> objectClass) {
    return deleteAllOn(getTableName(objectClass));
  }

  @Override
  public int deleteAllOn(String tableName) {
    return executeUpdate("DELETE FROM " + tableName);
  }

  @Override
  public <T> int[] deleteOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> deleteOn(tableName, array));
  }

  @Override
  public <T> int deleteOn(String tableName, T object) {
    return getCastedParameterContainerAndTableMapping(tableName, object.getClass())
        .delete(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
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
        getCastedParameterContainerAndTableMapping(tableName, objects[0].getClass());
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
    return executeQueryAndClose(connection, ResultSetTraverser.from(rowMapper), statementSupplier);
  }

  @Override
  public <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> resultSetTraverser) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql.getSql(), sql.getParameters(), resultSetTraverser);
  }

  @Override
  public <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> rowMapper) {
    return executeQuery(sql, ResultSetTraverser.from(rowMapper));
  }

  @Override
  public int executeUpdate(ParameterizedSql sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }


  @Override
  public int executeUpdate(String sql, Object... parameters) {
    final int ret = executeUpdateAndClose(getLoggerConfig(), connection, getSqlParametersSetter(),
        sql, parameters);
    return ret;
  }

  @Override
  public <T> boolean exists(T object) {
    final SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    mapping.throwExeptionIfPrimaryKeyIsNotExist();
    return existsHelper(mapping, object);
  }

  private <T> boolean existsHelper(SqlParametersToTableMapping<T> mapping, T object) {
    mapping.throwExeptionIfPrimaryKeyIsNotExist();
    final String sql = mapping.getSql().getExistsSql();
    return readFirst(Integer.class, sql, mapping.getPrimaryKeyParameters(object)) != null;
  }


  @Override
  public <T> boolean exists(String tableName, T object) {
    final SqlParametersToTableMapping<T> mapping =
        getCastedParameterContainerAndTableMapping(tableName, object.getClass());
    mapping.throwExeptionIfPrimaryKeyIsNotExist();
    return existsHelper(mapping, object);
  }

  private <T> SqlParametersToTableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return sormContext.getCastedTableMapping(connection, objectClass);
  }

  private <T> SqlParametersToTableMapping<T> getCastedParameterContainerAndTableMapping(
      String tableName, Class<?> objectClass) {
    return sormContext.getCastedTableMapping(connection, tableName, objectClass);
  }

  <T> SqlResultToColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    return sormContext.getColumnsMapping(objectClass);
  }

  @Override
  public Connection getJdbcConnection() {
    return connection;
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


  private ColumnValueToJavaObjectConverters getColumnValueToJavaObjectConverter() {
    return sormContext.getColumnValueToJavaObjectConverter();
  }

  private ColumnValueToMapEntryConverter getColumnValueToMapEntryConverter() {
    return sormContext.getColumnValueToMapEntryConverter();
  }


  @Override
  public ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser() {
    return resultSet -> traverseAndMapToMapList(resultSet);
  }



  @Override
  public <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> objectClass) {
    return resultSet -> traverseAndMapToList(objectClass, resultSet);
  }


  @Override
  public <T> RowMapper<T> getRowMapper(Class<T> objectClass) {
    return (resultSet, rowNum) -> mapRowToObject(objectClass, resultSet);
  }



  @Override
  public RowMapper<Map<String, Object>> getRowToMapMapper() {
    return (resultSet, rowNum) -> mapRowToMap(resultSet);
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
  public TableMetaData getTableMetaData(Class<?> objectClass) {
    return getTableMapping(objectClass).getTableMetaData();
  }


  @Override
  public TableMetaData getTableMetaData(String tableName) {
    return sormContext.getTableMetaData(connection, tableName);
  }


  @Override
  public String getTableName(Class<?> objectClass) {
    return sormContext.getTableName(connection, objectClass);
  }


  private int getTransactionIsolationLevel() {
    return sormContext.getTransactionIsolationLevel();
  }


  @Override
  public <T> int[] insert(List<T> objects) {
    return applytoArray(objects, array -> insert(array));
  }


  @Override
  public <T> int insert(T object) {
    return getCastedTableMapping(object.getClass()).insert(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public int insertMapOn(String tableName, Map<String, Object> object) {
    List<String> cols = new ArrayList<>(object.keySet());
    return executeUpdate(createInsertSql(tableName, cols),
        cols.stream().map(col -> object.get(col)).toArray());
  }

  private String createInsertSql(String tableName, List<String> cols) {
    String ps = String.join(",",
        Stream.generate(() -> "?").limit(cols.size()).collect(Collectors.toList()));
    String sql =
        "insert into " + tableName + " (" + String.join(",", cols) + ") VALUES (" + ps + ")";
    return sql;
  }


  @Override
  public int[] insertMapOn(String tableName,
      @SuppressWarnings("unchecked") Map<String, Object>... objects) {
    return insertMapOn(tableName, Arrays.asList(objects));
  }

  @Override
  public int[] insertMapOn(String tableName, List<Map<String, Object>> objects) {
    boolean origAutoCommit = getAutoCommit(connection);
    try {
      connection.setAutoCommit(false);
      int[] ret = objects.stream().mapToInt(o -> insertMapOn(tableName, o)).toArray();
      connection.setAutoCommit(true);
      return ret;
    } catch (Exception e) {
      rollbackIfRequired(connection, origAutoCommit);
      throw Try.rethrow(e);
    } finally {
      commitIfRequired(connection, origAutoCommit);
      setAutoCommit(origAutoCommit);
    }
  }

  @Override
  public <T> InsertResult<T> insertAndGet(List<T> objects) {
    return applytoArray(objects, array -> insertAndGet(array));
  }


  @Override
  public <T> InsertResult<T> insertAndGet(T object) {
    SqlParametersToTableMapping<T> mapping = getCastedTableMapping(object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }



  @Override
  public <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.emptyInsertResult());
  }


  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertAndGetOn(tableName, array));
  }

  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName, T object) {
    SqlParametersToTableMapping<T> mapping =
        getCastedParameterContainerAndTableMapping(tableName, object.getClass());
    return mapping.insertAndGet(getJdbcConnection(), object);
  }

  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insertAndGet(getJdbcConnection(), objects),
        () -> InsertResultImpl.emptyInsertResult());
  }


  @Override
  public <T> int[] insertOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> insertOn(tableName, array));
  }

  @Override
  public <T> int insertOn(String tableName, T object) {
    return getCastedParameterContainerAndTableMapping(tableName, object.getClass())
        .insert(getJdbcConnection(), object);
  }


  @Override
  public <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  public <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return resultSet.next() ? mapRowToObject(objectClass, resultSet) : null;
  }

  public Map<String, Object> loadFirstMap(ResultSet resultSet) throws SQLException {
    return resultSet.next() ? mapRowToMap(resultSet) : Collections.emptyMap();
  }

  private final <T> List<T> loadNativeObjectList(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    final List<T> ret = new ArrayList<>();
    final int sqlType = getOneSqlType(objectClass, resultSet);
    while (resultSet.next()) {
      ret.add(toSingleStandardObject(resultSet, sqlType, objectClass));
    }
    return ret;

  }

  public <T> T loadOne(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    T ret = null;
    if (resultSet.next()) {
      ret = mapRowToObject(objectClass, resultSet);
    }
    if (resultSet.next()) {
      throw new SormException("Non-unique result returned");
    }
    return ret;
  }


  public Map<String, Object> loadOneMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = mapRowToMap(resultSet);
    }
    if (resultSet.next()) {
      throw new SormException("Non-unique result returned");
    }
    return ret;
  }


  public final <T> List<T> loadContainerObjectList(final Class<T> objectClass,
      final ResultSet resultSet) throws SQLException {
    return getColumnsMapping(objectClass).loadContainerObjectList(resultSet);
  }


  private final <T> T loadSingleContainerObject(final Class<T> objectClass,
      final ResultSet resultSet) throws SQLException {
    return getColumnsMapping(objectClass).loadContainerObject(resultSet);
  }


  public Map<String, Object> mapRowToMap(ResultSet resultSet) {
    try {
      ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
      return toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }


  public <T> T mapRowToObject(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return getColumnValueToJavaObjectConverter().isSupportedType(objectClass)
        ? toSingleStandardObject(resultSet, getOneSqlType(objectClass, resultSet), objectClass)
        : loadSingleContainerObject(objectClass, resultSet);
  }


  /**
   * Converts the result from database to a map objects. The data of the column is extracted by
   * corresponding column types.
   *
   * <p>
   * Keys in the results returned in lower case by default.
   *
   * @param resultSet
   * @param columns
   * @param columnTypes SQL types from {@link java.sql.Types}
   *
   * @return
   * @throws SQLException
   */

  private Map<String, Object> toSingleMap(ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) throws SQLException {
    final int cSize = columns.size();
    final Map<String, Object> ret = new LinkedHashMap<>(cSize);
    for (int i = 1; i <= cSize; i++) {
      ret.put(getColumnValueToMapEntryConverter().convertToKey(columns.get(i - 1)),
          getColumnValueToMapEntryConverter().convertToValue(resultSet, i, columnTypes.get(i - 1)));
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

  private <T> T toSingleStandardObject(ResultSet resultSet, int sqlType, Class<T> objectClass)
      throws SQLException {
    return getColumnValueToJavaObjectConverter().convertTo(resultSet, 1, sqlType, objectClass);
  }

  @Override
  public <T> int[] merge(List<T> objects) {
    return applytoArray(objects, array -> merge(array));
  }

  @Override
  public <T> int merge(T object) {
    return getCastedTableMapping(object.getClass()).merge(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects, mapping -> mapping.merge(getJdbcConnection(), objects),
        EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> int[] mergeOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> mergeOn(tableName, array));
  }

  @Override
  public <T> int mergeOn(String tableName, T object) {
    return getCastedParameterContainerAndTableMapping(tableName, object.getClass())
        .merge(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.merge(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public final <T> List<T> readAll(Class<T> objectClass) {
    return readList(objectClass, getCastedTableMapping(objectClass).getSql().getSelectAllSql());
  }

  @Override
  public <T> LazyResultSet<T> readAllLazy(Class<T> objectClass) {
    return readLazy(objectClass, getTableMapping(objectClass).getSql().getSelectAllSql());
  }

  @Override
  public <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    final SqlParametersToTableMapping<T> mapping = getTableMapping(objectClass);
    mapping.throwExeptionIfPrimaryKeyIsNotExist();
    final String sql = mapping.getSql().getSelectByPrimaryKeySql();
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, primaryKeyValues, resultSet -> {
          return resultSet.next() ? getColumnsMapping(objectClass)
              .loadContainerObjectByPrimaryKey(objectClass, resultSet) : null;
        });
  }


  @Override
  public <T> T readFirst(Class<T> objectClass, ParameterizedSql sql) {
    return readFirst(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> loadFirst(objectClass, resultSet));
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, ParameterizedSql sql) {
    return readLazy(objectClass, sql.getSql(), sql.getParameters());
  }



  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters) {
    try {
      final PreparedStatement stmt = connection.prepareStatement(sql);
      getSqlParametersSetter().setParameters(stmt, parameters);

      getLoggerConfig().createLogPointBeforeSql(LoggerContext.Category.EXECUTE_QUERY,
          OrmConnectionImpl.class, connection, sql, parameters);

      final ResultSet resultSet = stmt.executeQuery();
      LazyResultSetImpl<T> ret = new LazyResultSetImpl<T>(this, objectClass, stmt, resultSet);
      lazyResultSets.add(ret);
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, ParameterizedSql sql) {
    return readList(objectClass, sql.getSql(), sql.getParameters());
  }


  @Override
  public <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> traverseAndMapToList(objectClass, resultSet));
  }

  @Override
  public Map<String, Object> readMapFirst(ParameterizedSql sql) {
    return readMapFirst(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> {
          ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
          if (resultSet.next()) {
            return toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
          }
          return null;
        });
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql) {
    return readMapLazy(sql.getSql(), sql.getParameters());
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    try {
      final PreparedStatement stmt = connection.prepareStatement(sql);
      getSqlParametersSetter().setParameters(stmt, parameters);

      getLoggerConfig().createLogPointBeforeSql(LoggerContext.Category.EXECUTE_QUERY,
          OrmConnectionImpl.class, connection, sql, parameters);

      final ResultSet resultSet = stmt.executeQuery();

      @SuppressWarnings({"unchecked", "rawtypes", "resource"})
      LazyResultSet<Map<String, Object>> ret =
          (LazyResultSet<Map<String, Object>>) new LazyResultSetImpl(this, stmt, resultSet);
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    return readMapList(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> traverseAndMapToMapList(resultSet));
  }



  @Override
  public Map<String, Object> readMapOne(ParameterizedSql sql) {
    return readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(final String sql, final Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> {
          ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
          Map<String, Object> ret = null;
          if (resultSet.next()) {
            ret = toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
          }
          if (resultSet.next()) {
            throw new SormException("Non-unique result returned");
          }
          return ret;
        });
  }

  @Override
  public <T> T readOne(Class<T> objectClass, ParameterizedSql sql) {
    return readOne(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readOne(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndClose(getLoggerConfig(), getJdbcConnection(), getSqlParametersSetter(),
        sql, parameters, resultSet -> {
          T ret = null;
          if (resultSet.next()) {
            ret = mapRowToObject(objectClass, resultSet);
          }
          if (resultSet.next()) {
            throw new SormException("Non-unique result returned");
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
    List<Tuple3<T1, T2, T3>> ret = executeQueryAndClose(getLoggerConfig(), getJdbcConnection(),
        getSqlParametersSetter(), sql, parameters, resultSet -> {
          final List<Tuple3<T1, T2, T3>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuple.of(loadSingleContainerObject(t1, resultSet),
                loadSingleContainerObject(t2, resultSet),
                loadSingleContainerObject(t3, resultSet)));
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
    List<Tuple2<T1, T2>> ret = executeQueryAndClose(getLoggerConfig(), getJdbcConnection(),
        getSqlParametersSetter(), sql, parameters, resultSet -> {
          final List<Tuple2<T1, T2>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuple.of(loadSingleContainerObject(t1, resultSet),
                loadSingleContainerObject(t2, resultSet)));
          }
          return ret1;
        });
    return ret;
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> join(Class<T1> t1, Class<T2> t2, String onCondition) {
    return readTupleList(t1, t2, joinHelper(JOIN, t1, t2, onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> join(Class<T1> t1, Class<T2> t2,
      String t1T2OnCondition, Class<T3> t3, String t2T3OnCondition) {
    return readTupleList(t1, t2, t3,
        joinHelper(JOIN, t1, t2, t1T2OnCondition, t3, t2T3OnCondition));
  }

  private <T1, T2, T3> String joinHelper(String joinType, Class<T1> t1, Class<T2> t2,
      String t1T2OnCondition) {
    TableMetaData t1m = getTableMapping(t1).getTableMetaData();
    TableMetaData t2m = getTableMapping(t2).getTableMetaData();
    String sql = SELECT + t1m.getColumnAliases() + ", " + t2m.getColumnAliases() + ", " + FROM
        + t1m.getTableName() + joinType + t2m.getTableName();
    return sql;
  }

  private <T1, T2, T3> String joinHelper(String joinType, Class<T1> t1, Class<T2> t2,
      String t1T2OnCondition, Class<T3> t3, String t2T3OnCondition) {
    TableMetaData t1m = getTableMapping(t1).getTableMetaData();
    TableMetaData t2m = getTableMapping(t2).getTableMetaData();
    TableMetaData t3m = getTableMapping(t3).getTableMetaData();
    String sql = SELECT + t1m.getColumnAliases() + ", " + t2m.getColumnAliases() + ", "
        + t3m.getColumnAliases() + FROM + t1m.getTableName() + joinType + t2m.getTableName() + ON
        + t1T2OnCondition + joinType + t3m.getTableName() + ON + t2T3OnCondition;
    return sql;
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> leftJoin(Class<T1> t1, Class<T2> t2, String onCondition) {
    return readTupleList(t1, t2, joinHelper(LEFT + JOIN, t1, t2, onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoin(Class<T1> t1, Class<T2> t2,
      String t1T2OnCondition, Class<T3> t3, String t2T3OnCondition) {
    return readTupleList(t1, t2, t3,
        joinHelper(LEFT + JOIN, t1, t2, t1T2OnCondition, t3, t2T3OnCondition));
  }

  @Override
  public void rollback() {
    Try.runOrElseThrow(() -> getJdbcConnection().rollback(), Try::rethrow);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrElseThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), Try::rethrow);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrElseThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        Try::rethrow);
  }

  public <T> List<T> traverseAndMapToList(Class<T> objectClass, ResultSet resultSet) {
    try {
      return getColumnValueToJavaObjectConverter().isSupportedType(objectClass)
          ? loadNativeObjectList(objectClass, resultSet)
          : loadContainerObjectList(objectClass, resultSet);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public List<Map<String, Object>> traverseAndMapToMapList(ResultSet resultSet) {
    try {
      final List<Map<String, Object>> ret = new ArrayList<>();
      ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
      while (resultSet.next()) {
        ret.add(toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
      }
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T> int[] update(List<T> objects) {
    return applytoArray(objects, array -> update(array));
  }

  @Override
  public <T> int update(T object) {
    return getCastedTableMapping(object.getClass()).update(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] update(@SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(objects,
        mapping -> mapping.update(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }

  @Override
  public <T> int[] updateOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> updateOn(tableName, array));
  }

  @Override
  public <T> int updateOn(String tableName, T object) {
    return getCastedParameterContainerAndTableMapping(tableName, object.getClass())
        .update(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.update(getJdbcConnection(), objects), EMPTY_INT_SUPPLIER);
  }


  @SuppressWarnings("unchecked")
  private static <T, R> R applytoArray(List<T> objects, Function<T[], R> sqlFunc) {
    return sqlFunc.apply((T[]) objects.toArray(Object[]::new));
  }


  static <R> R executeQueryAndClose(Connection connection, ResultSetTraverser<R> resultSetTraverser,
      FunctionHandler<Connection, PreparedStatement> statementSupplier) {
    try (PreparedStatement stmt = statementSupplier.apply(connection);
        ResultSet resultSet = stmt.executeQuery()) {
      return resultSetTraverser.traverseAndMap(resultSet);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  static <R> R executeQueryAndClose(LoggerContext loggerContext, Connection connection,
      SqlParametersSetter sqlParametersSetter, String sql, Object[] parameters,
      ResultSetTraverser<R> resultSetTraverser) {
    final Optional<LogPoint> lp = loggerContext.createLogPointBeforeSql(
        LoggerContext.Category.EXECUTE_QUERY, OrmConnectionImpl.class, connection, sql, parameters);
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParametersSetter.setParameters(stmt, parameters);
      ResultSet resultSet = stmt.executeQuery();
      R ret = resultSetTraverser.traverseAndMap(resultSet);
      lp.ifPresent(_lp -> _lp.logAfterQuery(ret));
      return ret;
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  public static final int executeUpdateAndClose(LoggerContext loggerContext, Connection connection,
      SqlParametersSetter sqlParametersSetter, String sql, Object[] parameters) {

    final Optional<LogPoint> lp =
        loggerContext.createLogPointBeforeSql(LoggerContext.Category.EXECUTE_UPDATE,
            OrmConnectionImpl.class, connection, sql, parameters);

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParametersSetter.setParameters(stmt, parameters);
      int ret = stmt.executeUpdate();
      lp.ifPresent(_lp -> _lp.logAfterUpdate(ret));
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }


  private static class ColumnsAndTypes {

    private final List<String> columns;
    private final List<Integer> columnTypes;

    private ColumnsAndTypes(List<String> columns, List<Integer> columnTypes) {
      this.columns = columns;
      this.columnTypes = columnTypes;
    }

    public List<String> getColumns() {
      return columns;
    }

    public List<Integer> getColumnTypes() {
      return columnTypes;
    }

    static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) throws SQLException {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int colNum = metaData.getColumnCount();
      List<String> columns = new ArrayList<>(colNum);
      List<Integer> columnTypes = new ArrayList<>(colNum);
      for (int i = 1; i <= colNum; i++) {
        columns.add(metaData.getColumnLabel(i));
        columnTypes.add(metaData.getColumnType(i));
      }
      return new ColumnsAndTypes(columns, columnTypes);
    }
  }

}
