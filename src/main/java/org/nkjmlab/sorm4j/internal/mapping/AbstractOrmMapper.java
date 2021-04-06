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
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SormLogger;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.util.LogPoint;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableFunction;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.nkjmlab.sorm4j.sql.tuple.Tuple2;
import org.nkjmlab.sorm4j.sql.tuple.Tuple3;
import org.nkjmlab.sorm4j.sql.tuple.Tuples;

abstract class AbstractOrmMapper implements SqlExecutor {

  static final org.slf4j.Logger log = org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  private final Connection connection;
  private final Mappings mappings;
  private final ResultSetConverter resultSetConverter;
  private final SqlParameterSetter sqlParameterSetter;

  private final int transactionIsolationLevel;

  /**
   * Creates a instance
   *
   * @param connection {@link java.sql.Connection} object to be used
   */
  public AbstractOrmMapper(Connection connection, ConfigStore configStore) {
    this.connection = connection;
    this.mappings = configStore.getMappings();
    this.resultSetConverter = mappings.getResultSetConverter();
    this.sqlParameterSetter = mappings.getSqlParameterSetter();
    this.transactionIsolationLevel = configStore.getTransactionIsolationLevel();
  }


  public <T> int deleteAll(Class<T> objectClass) {
    return mappings.getTableMapping(connection, objectClass).deleteAll(connection);
  }

  public int deleteAllOn(String tableName) {
    return executeUpdate("DELETE FROM " + tableName);
  }

  /**
   * Execute sql function with table name. objects when objects[0] is null,
   * {@code NullPointerException} are throw.
   */
  protected final <T, R> R execSqlIfParameterExists(String tableName, T[] objects,
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
  protected final <T, R> R execSqlIfParameterExists(T[] objects,
      Function<TableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    TableMapping<T> mapping = getCastedTableMapping(objects[0].getClass());
    return sqlFunction.apply(mapping);
  }


  private <R> R execStatementAndReadResultSet(String sql, Object[] parameters,
      FunctionHandler<ResultSet, R> resultSetHandler) {
    final Optional<LogPoint> dp = LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_QUERY);
    dp.ifPresent(lp -> {
      log.debug("[{}] [{}] with {} parameters", lp.getTag(), sql,
          parameters == null ? 0 : parameters.length);
      log.trace("[{}] Parameters = {}", lp.getTag(), parameters);
    });

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      try (ResultSet resultSet = stmt.executeQuery()) {
        R ret = resultSetHandler.apply(resultSet);
        dp.ifPresent(sw -> log.debug("{} Read [{}] objects from [{}]", sw.getTagAndElapsedTime(),
            ret instanceof Collection ? ((Collection<?>) ret).size() : 1,
            Try.getOrNull(() -> connection.getMetaData().getURL())));
        return ret;
      }
    } catch (Exception e) {
      String msg = (parameters == null || parameters.length == 0) ? format("Error in sql=[{}]", sql)
          : format("Fail to execute sql=[{}], parameters={}", sql, parameters);
      throw new SormException(msg + System.lineSeparator() + e.getMessage(), e);
    }
  }


  @Override
  public <T> T executeQuery(SqlStatement sql, FunctionHandler<ResultSet, T> resultSetHandler) {
    return execStatementAndReadResultSet(sql.getSql(), sql.getParameters(), resultSetHandler);
  }

  @Override
  public <T> List<T> executeQuery(SqlStatement sql, RowMapper<T> rowMapper) {
    return execStatementAndReadResultSet(sql.getSql(), sql.getParameters(),
        RowMapper.convertToRowsMapper(rowMapper));
  }



  @Override
  public int executeUpdate(SqlStatement sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    final Optional<LogPoint> dp =
        LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_UPDATE);

    final int ret = execPreparedStatementAndClose(sqlParameterSetter, connection, sql, parameters,
        stmt -> stmt.executeUpdate());
    dp.ifPresent(sw -> {
      log.trace("[{}] Parameters = {} ", sw.getTag(), parameters);
      log.debug("{} Call [{}] [{}]", sw.getTagAndElapsedTime(), sql,
          Try.getOrNull(() -> connection.getMetaData().getURL()), sql);
    });
    return ret;
  }


  @Override
  public Connection getJdbcConnection() {
    return connection;
  }

  protected int getTransactionIsolationLevel() {
    return transactionIsolationLevel;
  }


  <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      return mapRowAux(objectClass, resultSet);
    }
    return null;
  }

  Map<String, Object> loadFirstMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = mapRowAux(resultSet);
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
      ret = mapRowAux(objectClass, resultSet);
    }
    if (resultSet.next()) {
      throw new RuntimeException("Non-unique result returned");
    }
    return ret;
  }

  Map<String, Object> loadOneMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = mapRowAux(resultSet);
    }
    if (resultSet.next()) {
      throw new SormException("Non-unique result returned");
    }
    return ret;
  }


  public final <T> List<T> loadPojoList(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = mappings.getColumnsMapping(objectClass);
    return mapping.loadPojoList(resultSet);
  }


  public <T> T mapRowAux(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return resultSetConverter.isEnableToConvertNativeObject(objectClass)
        ? resultSetConverter.toSingleNativeObject(resultSet, objectClass)
        : toSinglePojo(objectClass, resultSet);
  }

  public Map<String, Object> mapRowAux(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
    return resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
  }

  public final <T> List<T> mapRowsAux(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    return resultSetConverter.isEnableToConvertNativeObject(objectClass)
        ? loadNativeObjectList(objectClass, resultSet)
        : loadPojoList(objectClass, resultSet);
  }

  public final List<Map<String, Object>> mapRowsAux(ResultSet resultSet) throws SQLException {
    final List<Map<String, Object>> ret = new ArrayList<>();
    ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
    while (resultSet.next()) {
      ret.add(resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
    }
    return ret;
  }

  public <T> T mapRow(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowAux(objectClass, resultSet), Try::rethrow);
  }

  public Map<String, Object> mapRowToMap(ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowAux(resultSet), Try::rethrow);
  }

  public final <T> List<T> mapRows(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowsAux(objectClass, resultSet), Try::rethrow);
  }

  public final List<Map<String, Object>> mapRowsToMapList(ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowsAux(resultSet), Try::rethrow);
  }


  /**
   * Reads a list of all objects in the database mapped to the given object class.
   */
  final <T> List<T> readAllAux(final Class<T> objectClass) {
    return readListAux(objectClass, getCastedTableMapping(objectClass).getSql().getSelectAllSql());
  }


  /**
   * Reads an object from the database by its primary keys.
   */
  final <T> T readByPrimaryKeyAux(final Class<T> objectClass, final Object... primaryKeyValues) {
    final TableMapping<T> mapping = mappings.getTableMapping(connection, objectClass);
    mapping.throwExeptionIfPrimaryKeysIsNotExist();
    final String sql = mapping.getSql().getSelectByPrimaryKeySql();
    return readFirstAux(objectClass, sql, primaryKeyValues);
  }

  final <T> T readFirstAux(Class<T> objectClass, String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters,
        resultSet -> loadFirst(objectClass, resultSet));
  }

  protected final List<LazyResultSet<?>> lazyResultSets = new ArrayList<>();

  final <T> LazyResultSet<T> readAllLazyAux(Class<T> objectClass) {
    return readLazyAux(objectClass,
        mappings.getTableMapping(connection, objectClass).getSql().getSelectAllSql());
  }

  final <T> LazyResultSet<T> readLazyAux(Class<T> objectClass, String sql, Object... parameters) {
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

  final <T> List<T> readListAux(Class<T> objectClass, String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters,
        resultSet -> mapRowsAux(objectClass, resultSet));
  }

  private <T> T getAux(Class<T> objectClass, ResultSet resultSet) {
    final ColumnsMapping<T> m = mappings.getColumnsMapping(objectClass);
    return Try.getOrThrow(() -> m.loadPojo(m.createColumnLabels(resultSet), resultSet),
        Try::rethrow);
  }


  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    List<Tuple2<T1, T2>> ret = execStatementAndReadResultSet(sql, parameters, resultSet -> {
      final List<Tuple2<T1, T2>> ret1 = new ArrayList<>();
      while (resultSet.next()) {
        ret1.add(Tuples.of(getAux(t1, resultSet), getAux(t2, resultSet)));
      }
      return ret1;
    });
    return ret;
  }

  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, String sql, Object... parameters) {
    List<Tuple3<T1, T2, T3>> ret = execStatementAndReadResultSet(sql, parameters, resultSet -> {
      final List<Tuple3<T1, T2, T3>> ret1 = new ArrayList<>();
      while (resultSet.next()) {
        ret1.add(Tuples.of(getAux(t1, resultSet), getAux(t2, resultSet), getAux(t3, resultSet)));
      }
      return ret1;
    });
    return ret;
  }


  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, SqlStatement sql) {
    return readTupleList(t1, t2, sql.getSql(), sql.getParameters());
  }

  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, SqlStatement sql) {
    return readTupleList(t1, t2, t3, sql.getSql(), sql.getParameters());
  }



  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
      ColumnsAndTypes ct = ColumnsAndTypes.createColumnsAndTypes(resultSet);
      if (resultSet.next()) {
        return resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
      }
      return null;
    });
  }

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

  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> mapRowsAux(resultSet));
  }

  public Map<String, Object> readMapOne(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
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

  final <T> T readOneAux(final Class<T> objectClass, final String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
      T ret = null;
      if (resultSet.next()) {
        ret = mapRowAux(objectClass, resultSet);
      }
      if (resultSet.next()) {
        throw new SormException("Non-unique result returned");
      }
      return ret;
    });
  }

  private final <T> T toSinglePojo(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = mappings.getColumnsMapping(objectClass);
    return mapping.loadPojo(resultSet);
  }


  protected <T> TableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return mappings.getCastedTableMapping(connection, objectClass);
  }

  protected <T> TableMapping<T> getTableMapping(Class<T> objectClass) {
    return mappings.getTableMapping(connection, objectClass);
  }


  protected <T> TableMapping<T> getCastedTableMapping(String tableName, Class<?> objectClass) {
    return mappings.getCastedTableMapping(connection, tableName, objectClass);
  }

  static final <R> R execPreparedStatementAndClose(SqlParameterSetter sqlParameterSetter,
      Connection connection, String sql, Object[] parameters,
      ThrowableFunction<PreparedStatement, R> func) {
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      return func.apply(stmt);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  public String getTableName(Class<?> objectClass) {
    return mappings.getTableName(connection, objectClass);
  }

}
