package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SormLogger;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.OrderedParameterQueryImpl;
import org.nkjmlab.sorm4j.internal.sql.QueryOrmExecutor;
import org.nkjmlab.sorm4j.internal.sql.SelectQueryImpl;
import org.nkjmlab.sorm4j.internal.util.LogPoint;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.SelectQuery;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.nkjmlab.sorm4j.sql.tuple.Tuple2;
import org.nkjmlab.sorm4j.sql.tuple.Tuple3;
import org.nkjmlab.sorm4j.sql.tuple.Tuples;

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

  @SuppressWarnings("unchecked")
  private static <T, R> R applytoArray(List<T> objects, Function<T[], R> sqlFunc) {
    return sqlFunc.apply((T[]) objects.toArray(Object[]::new));
  }

  static <R> R executeQueryAndRead(Connection connection, SqlParameterSetter sqlParameterSetter,
      String sql, Object[] parameters, FunctionHandler<ResultSet, R> resultSetHandler) {
    final Optional<LogPoint> dp = LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_QUERY);
    dp.ifPresent(lp -> {
      lp.debug(OrmConnectionImpl.class, "[{}] [{}] with {} parameters", lp.getTag(), sql,
          parameters == null ? 0 : parameters.length);
      lp.trace(OrmConnectionImpl.class, "[{}] Parameters = {}", lp.getTag(), parameters);
    });

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      try (ResultSet resultSet = stmt.executeQuery()) {
        R ret = resultSetHandler.apply(resultSet);
        dp.ifPresent(lp -> lp.debug(OrmConnectionImpl.class, "{} Read [{}] objects from [{}]",
            lp.getTagAndElapsedTime(), ret instanceof Collection ? ((Collection<?>) ret).size() : 1,
            Try.getOrNull(() -> connection.getMetaData().getURL())));
        return ret;
      }
    } catch (Exception e) {
      String msg = (parameters == null || parameters.length == 0) ? format("Error in sql=[{}]", sql)
          : format("Fail to execute sql=[{}], parameters={}", sql, parameters);
      throw new SormException(msg + System.lineSeparator() + e.getMessage(), e);
    }
  }

  static final int executeUpdateAndClose(Connection connection,
      SqlParameterSetter sqlParameterSetter, String sql, Object[] parameters) {
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      return stmt.executeUpdate();
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  private final Connection connection;

  private final Mappings mappings;


  private final ResultSetConverter resultSetConverter;

  private final SqlParameterSetter sqlParameterSetter;


  private final int transactionIsolationLevel;


  private final List<LazyResultSet<?>> lazyResultSets = new ArrayList<>();



  /**
   * Creates a instance that will use the default cache for table-object and column-object mappings.
   *
   * @param connection {@link java.sql.Connection} object to be used
   * @param configStore
   */
  public OrmConnectionImpl(Connection connection, ConfigStore configStore) {
    this.connection = connection;
    this.mappings = configStore.getMappings();
    this.resultSetConverter = mappings.getResultSetConverter();
    this.sqlParameterSetter = mappings.getSqlParameterSetter();
    this.transactionIsolationLevel = configStore.getTransactionIsolationLevel();
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
    Try.runOrThrow(() -> {
      lazyResultSets.forEach(rs -> rs.close());
      lazyResultSets.clear();
      getJdbcConnection().close();
    }, Try::rethrow);
  }


  @Override
  public void commit() {
    Try.runOrThrow(() -> getJdbcConnection().commit(), Try::rethrow);
  }

  @Override
  public <T> NamedParameterQuery<T> createNamedParameterQuery(Class<T> objectClass, String sql) {
    return NamedParameterQueryImpl.createFrom(new QueryOrmExecutor<>(this, objectClass), sql);
  }


  @Override
  public NamedParameterRequest createNamedParameterRequest(String sql) {
    return NamedParameterRequest.from(this, sql);
  }


  @Override
  public <T> OrderedParameterQuery<T> createOrderedParameterQuery(Class<T> objectClass,
      String sql) {
    return OrderedParameterQueryImpl.createFrom(new QueryOrmExecutor<>(this, objectClass), sql);
  }

  @Override
  public OrderedParameterRequest createOrderedParameterRequest(String sql) {
    return OrderedParameterRequest.from(this, sql);
  }


  @Override
  public <T> SelectQuery<T> createSelectQuery(Class<T> objectClass) {
    SelectQueryImpl<T> ret = new SelectQueryImpl<T>(new QueryOrmExecutor<>(this, objectClass));
    ret.from(getTableName(objectClass));
    return ret;
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
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
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
    return getCastedTableMapping(tableName, object.getClass()).delete(getJdbcConnection(), object);
  }

  @Override
  public <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.delete(getJdbcConnection(), objects), () -> new int[0]);
  }

  /**
   * Execute sql function with table name. objects when objects[0] is null,
   * {@code NullPointerException} are throw.
   */
  private final <T, R> R execSqlIfParameterExists(String tableName, T[] objects,
      Function<TableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    TableMapping<T> mapping = getCastedTableMapping(tableName, objects[0].getClass());
    return sqlFunction.apply(mapping);
  }

  /**
   * Execute sql function. objects when objects[0] is null, {@code NullPointerException} are throw.
   */
  private final <T, R> R execSqlIfParameterExists(T[] objects,
      Function<TableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    TableMapping<T> mapping = getCastedTableMapping(objects[0].getClass());
    return sqlFunction.apply(mapping);
  }

  @Override
  public <T> T executeQuery(SqlStatement sql, FunctionHandler<ResultSet, T> resultSetHandler) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql.getSql(),
        sql.getParameters(), resultSetHandler);
  }

  @Override
  public <T> List<T> executeQuery(SqlStatement sql, RowMapper<T> rowMapper) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql.getSql(),
        sql.getParameters(), RowMapper.convertToRowListMapper(rowMapper));
  }

  @Override
  public int executeUpdate(SqlStatement sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    final Optional<LogPoint> dp =
        LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_UPDATE);

    final int ret = executeUpdateAndClose(connection, sqlParameterSetter, sql, parameters);
    dp.ifPresent(lp -> {
      lp.trace(OrmConnectionImpl.class, "[{}] Parameters = {} ", lp.getTag(), parameters);
      lp.debug(OrmConnectionImpl.class, "{} Call [{}] [{}]", lp.getTagAndElapsedTime(), sql,
          Try.getOrNull(() -> connection.getMetaData().getURL()), sql);
    });
    return ret;
  }

  private <T> TableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return mappings.getCastedTableMapping(connection, objectClass);
  }

  private <T> TableMapping<T> getCastedTableMapping(String tableName, Class<?> objectClass) {
    return mappings.getCastedTableMapping(connection, tableName, objectClass);
  }


  <T> ColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    return mappings.getColumnsMapping(objectClass);
  }

  @Override
  public Connection getJdbcConnection() {
    return connection;
  }


  /**
   * Gets {@link TableMapping}. This method is for internal use.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  public <T> TableMapping<T> getTableMapping(Class<T> objectClass) {
    return mappings.getTableMapping(connection, objectClass);
  }

  @Override
  public String getTableName(Class<?> objectClass) {
    return mappings.getTableName(connection, objectClass);
  }

  private int getTransactionIsolationLevel() {
    return transactionIsolationLevel;
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
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }

  @Override
  public <T> InsertResult<T> insertAndGet(List<T> objects) {
    return applytoArray(objects, array -> insertAndGet(array));
  }



  @Override
  public <T> InsertResult<T> insertAndGet(T object) {
    TableMapping<T> mapping = getCastedTableMapping(object.getClass());
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
    TableMapping<T> mapping = getCastedTableMapping(tableName, object.getClass());
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
    return getCastedTableMapping(tableName, object.getClass()).insert(getJdbcConnection(), object);
  }


  @Override
  public <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.insert(getJdbcConnection(), objects), () -> new int[0]);
  }


  <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      return mapRow(objectClass, resultSet);
    }
    return null;
  }


  Map<String, Object> loadFirstMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = mapRowToMap(resultSet);
    }
    return ret;
  }


  private final <T> List<T> loadNativeObjectList(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    Try.runOrThrow(() -> {
      ResultSetMetaData metaData = resultSet.getMetaData();
      if (metaData.getColumnCount() != 1) {
        throw new SormException("ResultSet returned [" + metaData.getColumnCount()
            + "] columns but 1 column was expected to load data into an instance of ["
            + objectClass.getName() + "]");
      }
    }, Try::rethrow);
    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(resultSetConverter.toSingleNativeObject(resultSet, objectClass));
    }
    return ret;

  }


  <T> T loadOne(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    T ret = null;
    if (resultSet.next()) {
      ret = mapRow(objectClass, resultSet);
    }
    if (resultSet.next()) {
      throw new RuntimeException("Non-unique result returned");
    }
    return ret;
  }


  Map<String, Object> loadOneMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = mapRowToMap(resultSet);
    }
    if (resultSet.next()) {
      throw new SormException("Non-unique result returned");
    }
    return ret;
  }



  public final <T> List<T> loadPojoList(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojoList(resultSet);
  }


  private final <T> T loadSinglePojo(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojo(resultSet);
  }

  private <T> T loadSinglePojoByColumnLabels(Class<T> objectClass, ResultSet resultSet) {
    final ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return Try.getOrThrow(() -> mapping.loadPojo(mapping.createColumnLabels(resultSet), resultSet),
        Try::rethrow);
  }

  @Override
  public <T> T mapRow(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> resultSetConverter.isEnableToConvertNativeObject(objectClass)
        ? resultSetConverter.toSingleNativeObject(resultSet, objectClass)
        : loadSinglePojo(objectClass, resultSet), Try::rethrow);
  }



  @Override
  public <T> List<T> mapRowList(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> resultSetConverter.isEnableToConvertNativeObject(objectClass)
        ? loadNativeObjectList(objectClass, resultSet)
        : loadPojoList(objectClass, resultSet), Try::rethrow);
  }

  @Override
  public List<Map<String, Object>> mapRowsToMapList(ResultSet resultSet) {
    return Try.getOrThrow(() -> {
      final List<Map<String, Object>> ret = new ArrayList<>();
      ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
      while (resultSet.next()) {
        ret.add(resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
      }
      return ret;
    }, Try::rethrow);
  }


  @Override
  public Map<String, Object> mapRowToMap(ResultSet resultSet) {
    return Try.getOrThrow(() -> {
      ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
      return resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
    }, Try::rethrow);
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
        () -> new int[0]);
  }


  @Override
  public <T> int[] mergeOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> mergeOn(tableName, array));
  }


  @Override
  public <T> int mergeOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).merge(getJdbcConnection(), object);
  }


  @Override
  public <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.merge(getJdbcConnection(), objects), () -> new int[0]);
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
    final TableMapping<T> mapping = getTableMapping(objectClass);
    mapping.throwExeptionIfPrimaryKeysIsNotExist();
    final String sql = mapping.getSql().getSelectByPrimaryKeySql();
    return readFirst(objectClass, sql, primaryKeyValues);
  }

  @Override
  public <T> T readByPrimaryKeyOf(T object) {
    if (object == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    Class<T> objectClass = (Class<T>) object.getClass();
    final TableMapping<T> mapping = getTableMapping(objectClass);
    return readByPrimaryKey(objectClass, mapping.getReadPrimaryKeyParameters(object));
  }



  @Override
  public <T> T readFirst(Class<T> objectClass, SqlStatement sql) {
    return readFirst(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> loadFirst(objectClass, resultSet));
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, SqlStatement sql) {
    return readLazy(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters) {
    try {
      final PreparedStatement stmt = connection.prepareStatement(sql);
      sqlParameterSetter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      LazyResultSetImpl<T> ret = new LazyResultSetImpl<T>(this, objectClass, stmt, resultSet);
      lazyResultSets.add(ret);
      return ret;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public <T> List<T> readList(Class<T> objectClass, SqlStatement sql) {
    return readList(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> mapRowList(objectClass, resultSet));
  }

  @Override
  public Map<String, Object> readMapFirst(SqlStatement sql) {
    return readMapFirst(sql.getSql(), sql.getParameters());
  }



  @Override
  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> {
          ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
          if (resultSet.next()) {
            return resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
          }
          return null;
        });
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(SqlStatement sql) {
    return readMapLazy(sql.getSql(), sql.getParameters());
  }


  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    try {
      final PreparedStatement stmt = connection.prepareStatement(sql);
      sqlParameterSetter.setParameters(stmt, parameters);
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
  public List<Map<String, Object>> readMapList(SqlStatement sql) {
    return readMapList(sql.getSql(), sql.getParameters());
  }

  @Override
  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> mapRowsToMapList(resultSet));
  }

  @Override
  public Map<String, Object> readMapOne(SqlStatement sql) {
    return readMapOne(sql.getSql(), sql.getParameters());
  }

  @Override
  public Map<String, Object> readMapOne(final String sql, final Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> {
          ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
          Map<String, Object> ret = null;
          if (resultSet.next()) {
            ret = resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
          }
          if (resultSet.next()) {
            throw new SormException("Non-unique result returned");
          }
          return ret;
        });
  }

  @Override
  public <T> T readOne(Class<T> objectClass, SqlStatement sql) {
    return readOne(objectClass, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> T readOne(Class<T> objectClass, String sql, Object... parameters) {
    return executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters,
        resultSet -> {
          T ret = null;
          if (resultSet.next()) {
            ret = mapRow(objectClass, resultSet);
          }
          if (resultSet.next()) {
            throw new SormException("Non-unique result returned");
          }
          return ret;
        });
  }



  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, SqlStatement sql) {
    return readTupleList(t1, t2, t3, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, String sql, Object... parameters) {
    List<Tuple3<T1, T2, T3>> ret =
        executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters, resultSet -> {
          final List<Tuple3<T1, T2, T3>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuples.of(loadSinglePojoByColumnLabels(t1, resultSet),
                loadSinglePojoByColumnLabels(t2, resultSet),
                loadSinglePojoByColumnLabels(t3, resultSet)));
          }
          return ret1;
        });
    return ret;
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, SqlStatement sql) {
    return readTupleList(t1, t2, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    List<Tuple2<T1, T2>> ret =
        executeQueryAndRead(getJdbcConnection(), sqlParameterSetter, sql, parameters, resultSet -> {
          final List<Tuple2<T1, T2>> ret1 = new ArrayList<>();
          while (resultSet.next()) {
            ret1.add(Tuples.of(loadSinglePojoByColumnLabels(t1, resultSet),
                loadSinglePojoByColumnLabels(t2, resultSet)));
          }
          return ret1;
        });
    return ret;
  }

  @Override
  public void rollback() {
    Try.runOrThrow(() -> getJdbcConnection().rollback(), Try::rethrow);
  }

  @Override
  public void setAutoCommit(final boolean autoCommit) {
    Try.runOrThrow(() -> getJdbcConnection().setAutoCommit(autoCommit), Try::rethrow);
  }

  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel), Try::rethrow);
  }

  @Override
  public <S> TypedOrmConnection<S> type(Class<S> objectClass) {
    return new TypedOrmConnectionImpl<>(objectClass, this);
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
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }

  @Override
  public <T> int[] updateOn(String tableName, List<T> objects) {
    return applytoArray(objects, array -> updateOn(tableName, array));
  }

  @Override
  public <T> int updateOn(String tableName, T object) {
    return getCastedTableMapping(tableName, object.getClass()).update(getJdbcConnection(), object);
  }


  @Override
  public <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return execSqlIfParameterExists(tableName, objects,
        mapping -> mapping.update(getJdbcConnection(), objects), () -> new int[0]);
  }



}
