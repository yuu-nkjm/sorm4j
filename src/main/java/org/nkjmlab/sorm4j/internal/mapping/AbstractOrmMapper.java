package org.nkjmlab.sorm4j.internal.mapping;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.OrmLogger;
import org.nkjmlab.sorm4j.ResultSetMapper;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.extension.TableName;
import org.nkjmlab.sorm4j.extension.TableNameMapper;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.util.LogPoint;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableFunction;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;

abstract class AbstractOrmMapper implements SqlExecutor, ResultSetMapper {
  private static class ColumnsAndTypes {

    private final List<String> columns;
    private final List<Integer> columnTypes;

    public ColumnsAndTypes(List<String> columns, List<Integer> columnTypes) {
      this.columns = columns;
      this.columnTypes = columnTypes;
    }

    public List<String> getColumns() {
      return columns;
    }

    public List<Integer> getColumnTypes() {
      return columnTypes;
    }

  }


  private static final org.slf4j.Logger log =
      org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  private static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int colNum = metaData.getColumnCount();
    List<String> columns = new ArrayList<>(colNum);
    List<Integer> columnTypes = new ArrayList<>(colNum);
    for (int i = 1; i <= colNum; i++) {
      columns.add(metaData.getColumnName(i));
      columnTypes.add(metaData.getColumnType(i));
    }
    return new ColumnsAndTypes(columns, columnTypes);
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

  private final ColumnFieldMapper fieldMapper;

  private final TableNameMapper tableNameMapper;

  private final ResultSetConverter resultSetConverter;

  private final SqlParameterSetter sqlParameterSetter;

  private final Connection connection;

  private final MultiRowProcessorFactory batchConfig;

  private final ConfigStore configStore;

  private final ConcurrentMap<String, TableMapping<?>> tableMappings;

  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;


  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;

  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;


  private final int transactionIsolationLevel;

  /**
   * Creates a instance
   *
   * @param connection {@link java.sql.Connection} object to be used
   */
  public AbstractOrmMapper(Connection connection, ConfigStore configStore) {
    this.connection = connection;
    this.configStore = configStore;
    this.batchConfig = configStore.getMultiRowProcessorGeneratorFactory();
    this.fieldMapper = configStore.getColumnFieldMapper();
    this.tableNameMapper = configStore.getTableNameMapper();
    this.resultSetConverter = configStore.getResultSetConverter();
    this.sqlParameterSetter = configStore.getSqlParameterSetter();
    this.tableMappings = configStore.getTableMappings();
    this.columnsMappings = configStore.getColumnsMappings();
    this.classNameToValidTableNameMap = configStore.getClassNameToValidTableNameMap();
    this.tableNameToValidTableNameMap = configStore.getTableNameToValidTableNameMaps();
    this.transactionIsolationLevel = configStore.getTransactionIsolationLevel();
  }


  public <T> int deleteAll(Class<T> objectClass) {
    return getTableMapping(objectClass).deleteAll(connection);
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
    final Optional<LogPoint> dp = LogPointFactory.createLogPoint(OrmLogger.Category.EXECUTE_QUERY);
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
        ResultSetMapper.convertToRowsMapper(rowMapper));
  }



  @Override
  public int executeUpdate(SqlStatement sql) {
    return executeUpdate(sql.getSql(), sql.getParameters());
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    final Optional<LogPoint> dp = LogPointFactory.createLogPoint(OrmLogger.Category.EXECUTE_UPDATE);

    final int ret = execPreparedStatementAndClose(sqlParameterSetter, connection, sql, parameters,
        stmt -> stmt.executeUpdate());
    dp.ifPresent(sw -> {
      log.debug("{} Call [{}] [{}]", sw.getTagAndElapsedTime(), sql,
          Try.getOrNull(() -> connection.getMetaData().getURL()), sql);
      log.trace("[{}] Parameters = {} ", sw.getTag(), parameters);
    });
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(objectClass);
  }


  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(String tableName, Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(tableName, objectClass);
  }

  <T> ColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    ColumnsMapping<T> ret = (ColumnsMapping<T>) columnsMappings.computeIfAbsent(objectClass, _k -> {
      ColumnsMapping<T> m =
          MappingFactory.createColumnsMapping(objectClass, resultSetConverter, fieldMapper);

      LogPointFactory.createLogPoint(OrmLogger.Category.MAPPING)
          .ifPresent(lp -> log.info(System.lineSeparator() + m.getFormattedString()));

      return m;
    });
    return ret;
  }

  @Override
  public Connection getJdbcConnection() {
    return connection;
  }

  public <T> TableMapping<T> getTableMapping(Class<T> objectClass) {
    TableName tableName = toTableName(objectClass);
    return getTableMapping(tableName, objectClass);
  }


  /**
   * Get table mapping by the table name and the object class. When there is no mapping, the method
   * create a mapping and register it.
   *
   */
  <T> TableMapping<T> getTableMapping(String tableName, Class<T> objectClass) {
    return getTableMapping(toTableName(tableName), objectClass);
  }

  <T> TableMapping<T> getTableMapping(TableName tableName, Class<T> objectClass) {
    String key = tableName.getName() + "-" + objectClass.getName();
    @SuppressWarnings("unchecked")
    TableMapping<T> ret =
        (TableMapping<T>) tableMappings.computeIfAbsent(key, Try.createFunctionWithThrow(_key -> {
          TableMapping<T> m = MappingFactory.createTableMapping(resultSetConverter, sqlParameterSetter,
              objectClass, tableName.getName(), fieldMapper, batchConfig, connection);
          LogPointFactory.createLogPoint(OrmLogger.Category.MAPPING).ifPresent(lp -> log
              .info("[{}]" + System.lineSeparator() + "{}", lp.getTag(), m.getFormattedString()));
          return m;
        }, Try::rethrow));
    return ret;
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
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojoList(resultSet);
  }


  public <T> T mapRowAux(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return resultSetConverter.isEnableToConvertNativeObject(objectClass)
        ? resultSetConverter.toSingleNativeObject(resultSet, objectClass)
        : toSinglePojo(objectClass, resultSet);
  }

  public Map<String, Object> mapRowAux(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
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
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
    while (resultSet.next()) {
      ret.add(resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
    }
    return ret;
  }

  @Override
  public <T> T mapRow(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowAux(objectClass, resultSet), Try::rethrow);
  }

  @Override
  public Map<String, Object> mapRow(ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowAux(resultSet), Try::rethrow);
  }

  @Override
  public final <T> List<T> mapRows(Class<T> objectClass, ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowsAux(objectClass, resultSet), Try::rethrow);
  }

  @Override
  public final List<Map<String, Object>> mapRows(ResultSet resultSet) {
    return Try.getOrThrow(() -> mapRowsAux(resultSet), Try::rethrow);
  }


  /**
   * Reads a list of all objects in the database mapped to the given object class.
   */
  final <T> List<T> readAllAux(final Class<T> objectClass) {
    return readListAux(objectClass, getCastedTableMapping(objectClass).getSql().getSelectAllSql());
  }



  final <T> LazyResultSet<T> readAllLazyAux(Class<T> objectClass) {
    return readLazyAux(objectClass, getTableMapping(objectClass).getSql().getSelectAllSql());
  }

  /**
   * Reads an object from the database by its primary keys.
   */
  final <T> T readByPrimaryKeyAux(final Class<T> objectClass, final Object... primaryKeyValues) {
    final TableMapping<T> mapping = getTableMapping(objectClass);
    mapping.throwExeptionIfPrimaryKeysIsNotExist();
    final String sql = mapping.getSql().getSelectByPrimaryKeySql();
    return readFirstAux(objectClass, sql, primaryKeyValues);
  }

  final <T> T readFirstAux(Class<T> objectClass, String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters,
        resultSet -> loadFirst(objectClass, resultSet));
  }

  final <T> LazyResultSet<T> readLazyAux(Class<T> objectClass, String sql, Object... parameters) {
    try {
      final PreparedStatement stmt = connection.prepareStatement(sql);
      sqlParameterSetter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      return new LazyResultSetImpl<T>(this, objectClass, stmt, resultSet);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  final <T> List<T> readListAux(Class<T> objectClass, String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters,
        resultSet -> mapRowsAux(objectClass, resultSet));
  }

  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
      ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
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
      @SuppressWarnings({"unchecked", "rawtypes"})
      LazyResultSet<Map<String, Object>> ret =
          (LazyResultSet<Map<String, Object>>) new LazyResultSetImpl(this, LinkedHashMap.class,
              stmt, resultSet);
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
      ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
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
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojo(resultSet);
  }

  private TableName toTableName(Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass, Try.createFunctionWithThrow(
        k -> tableNameMapper.getTableName(objectClass, connection.getMetaData()), Try::rethrow));
  }

  public String getTableName(Class<?> objectClass) {
    return toTableName(objectClass).getName();
  }

  private TableName toTableName(String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName, Try.createFunctionWithThrow(
        k -> tableNameMapper.getTableName(tableName, connection.getMetaData()), Try::rethrow));
  }

}
