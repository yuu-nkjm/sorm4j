package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.mapping.PreparedStatementUtils.*;
import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.math.BigDecimal;
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
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.mapping.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.mapping.extension.TableNameMapper;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.util.DebugPoint;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.Try;
import org.nkjmlab.sorm4j.util.Try.ThrowableFunction;

abstract class AbstractOrmMapper implements SqlExecutor {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  private final ColumnFieldMapper fieldMapper;

  private final TableNameMapper tableNameMapper;

  private final ResultSetConverter resultSetConverter;

  private final SqlParameterSetter sqlParameterSetter;

  private final Connection connection;

  private final MultiRowProcessorGeneratorFactory batchConfig;

  private final OrmConfigStore configStore;

  private final ConcurrentMap<String, TableMapping<?>> tableMappings;

  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;

  private final ConcurrentMap<Class<?>, TableName> classNameToValidTableNameMap;

  private final ConcurrentMap<String, TableName> tableNameToValidTableNameMap;


  /**
   * Creates a instance
   *
   * @param connection {@link java.sql.Connection} object to be used
   */
  public AbstractOrmMapper(Connection connection, OrmConfigStore configStore) {
    this.connection = connection;
    this.configStore = configStore;
    this.batchConfig = configStore.getMultiProcessorFactory();
    this.fieldMapper = configStore.getColumnFieldMapper();
    this.tableNameMapper = configStore.getTableNameMapper();
    this.resultSetConverter = configStore.getSqlToJavaDataConverter();
    this.sqlParameterSetter = configStore.getSqlParameterSetter();
    this.tableMappings = configStore.getTableMappings();
    this.columnsMappings = configStore.getColumnsMappings();
    this.classNameToValidTableNameMap = configStore.getClassNameToValidTableNameMap();
    this.tableNameToValidTableNameMap = configStore.getTableNameToValidTableNameMaps();
  }

  public <T> int deleteAll(Class<T> objectClass) {
    return getTableMapping(objectClass).deleteAll(connection);
  }


  public int deleteAllOn(String tableName) {
    return executeUpdate("DELETE FROM " + tableName);
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

  @Override
  public boolean execute(String sql, Object... parameters) {
    return execPreparedStatementAndClose(sqlParameterSetter, connection, sql, parameters,
        stmt -> stmt.execute());
  }

  @Override
  public ResultSet executeQuery(String sql, Object... parameters) {
    return execPreparedStatementAndClose(sqlParameterSetter, connection, sql, parameters,
        stmt -> stmt.executeQuery());
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return execPreparedStatementAndClose(sqlParameterSetter, connection, sql, parameters,
        stmt -> stmt.executeUpdate());
  }


  private <R> R execStatementAndReadResultSet(String sql, Object[] parameters,
      ThrowableFunction<ResultSet, R> sqlResultReader) {
    try (PreparedStatement stmt = getPreparedStatement(connection, sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      try (ResultSet resultSet = stmt.executeQuery()) {
        Optional<DebugPoint> dp = DebugPointFactory.createDebugPoint(DebugPointFactory.Name.READ);
        dp.ifPresent(sw -> log.debug("[{}] with {} ", sql, parameters));
        R ret = sqlResultReader.apply(resultSet);
        dp.ifPresent(
            sw -> log.debug("{} Read [{}] objects from [{}]", sw.getFormattedNameAndElapsedTime(),
                ret instanceof Collection ? ((Collection<?>) ret).size() : 1,
                Try.getOrNull(() -> connection.getMetaData().getURL())));
        return ret;
      }
    } catch (Throwable e) {
      String msg = (parameters == null || parameters.length == 0) ? format("Error in sql=[{}]", sql)
          : format("Fail to execute sql=[{}], parameters={}", sql, parameters);
      throw new OrmException(msg + System.lineSeparator() + e.getMessage(), e);
    }
  }


  static final <R> R execPreparedStatementAndClose(SqlParameterSetter sqlParameterSetter,
      Connection connection, String sql, Object[] parameters,
      ThrowableFunction<PreparedStatement, R> func) {
    try (PreparedStatement stmt = getPreparedStatement(connection, sql)) {
      sqlParameterSetter.setParameters(stmt, parameters);
      return func.apply(stmt);
    } catch (Throwable e) {
      throw OrmException.wrapIfNotOrmException(e);
    }
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
          ColumnsMapping.createMapping(objectClass, resultSetConverter, fieldMapper);
      log.info(System.lineSeparator() + m.getFormattedString());
      return m;
    });
    return ret;
  }


  public OrmConfigStore getConfigStore() {
    return configStore;
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
          TableMapping<T> m = TableMapping.createMapping(resultSetConverter, sqlParameterSetter,
              objectClass, tableName.getName(), fieldMapper, batchConfig, connection);
          log.info(System.lineSeparator() + m.getFormattedString());
          return m;
        }, OrmException::new));
    return ret;
  }


  <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      return toSingleObject(objectClass, resultSet);
    }
    return null;
  }

  Map<String, Object> loadFirstMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = toSingleMap(resultSet);
    }
    return ret;
  }

  List<Map<String, Object>> loadMapList(ResultSet resultSet) throws SQLException {
    final List<Map<String, Object>> ret = new ArrayList<>();
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
    while (resultSet.next()) {
      ret.add(resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
    }
    return ret;
  }


  private final <T> List<T> loadNativeObjectList(Class<T> objectClass, ResultSet resultSet)
      throws SQLException {
    Try.runOrThrow(() -> {
      ResultSetMetaData metaData = resultSet.getMetaData();
      if (metaData.getColumnCount() != 1) {
        throw new OrmException("ResultSet returned [" + metaData.getColumnCount()
            + "] columns but 1 column was expected to load data into an instance of ["
            + objectClass.getName() + "]");
      }
    }, OrmException::wrapIfNotOrmException);
    final List<T> ret = new ArrayList<>();
    while (resultSet.next()) {
      ret.add(resultSetConverter.toSingleNativeObject(resultSet, objectClass));
    }
    return ret;

  }

  <T> T loadOne(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    T ret = null;
    if (resultSet.next()) {
      ret = toSingleObject(objectClass, resultSet);
    }
    if (resultSet.next()) {
      throw new RuntimeException("Non-unique result returned");
    }
    return ret;
  }


  Map<String, Object> loadOneMap(ResultSet resultSet) throws SQLException {
    Map<String, Object> ret = null;
    if (resultSet.next()) {
      ret = toSingleMap(resultSet);
    }
    if (resultSet.next()) {
      throw new OrmException("Non-unique result returned");
    }
    return ret;
  }


  /**
   * Reads a list of all objects in the database mapped to the given object class.
   */
  final <T> List<T> readAllAux(final Class<T> objectClass) {
    final TableMapping<T> mapping = getTableMapping(objectClass);
    final String sql = mapping.getSql().getSelectAllSql();
    Optional<DebugPoint> dp = DebugPointFactory.createDebugPoint(DebugPointFactory.Name.READ);
    List<T> result = readListAux(objectClass, sql);
    dp.ifPresent(sw -> log.debug("{} Read [{}] objects of [{}]",
        sw.getFormattedNameAndElapsedTime(), result.size(), objectClass.getSimpleName()));
    return result;
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
      final PreparedStatement stmt = getPreparedStatement(connection, sql);
      sqlParameterSetter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      return new LazyResultSetImpl<T>(this, objectClass, stmt, resultSet);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  final <T> List<T> readListAux(Class<T> objectClass, String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters,
        resultSet -> isEnableToConvertNativeSqlType(objectClass)
            ? loadNativeObjectList(objectClass, resultSet)
            : toPojoList(objectClass, resultSet));
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
      final PreparedStatement stmt = getPreparedStatement(connection, sql);
      sqlParameterSetter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      @SuppressWarnings({"unchecked", "rawtypes", "resource"})
      LazyResultSet<Map<String, Object>> ret =
          (LazyResultSet<Map<String, Object>>) new LazyResultSetImpl(this, LinkedHashMap.class,
              stmt, resultSet);
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> loadMapList(resultSet));
  }

  public Map<String, Object> readMapOne(final String sql, final Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
      ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
      Map<String, Object> ret = null;
      if (resultSet.next()) {
        ret = resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
      }
      if (resultSet.next()) {
        throw new OrmException("Non-unique result returned");
      }
      return ret;
    });
  }

  final <T> T readOneAux(final Class<T> objectClass, final String sql, Object... parameters) {
    return execStatementAndReadResultSet(sql, parameters, resultSet -> {
      T ret = null;
      if (resultSet.next()) {
        ret = toSingleObject(objectClass, resultSet);
      }
      if (resultSet.next()) {
        throw new OrmException("Non-unique result returned");
      }
      return ret;
    });
  }

  public final <T> List<T> toPojoList(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojoList(resultSet);
  }

  public Map<String, Object> toSingleMap(ResultSet resultSet) throws SQLException {
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
    return resultSetConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());

  }

  public <T> T toSingleObject(Class<T> objectClass, ResultSet resultSet) throws SQLException {
    return isEnableToConvertNativeSqlType(objectClass)
        ? resultSetConverter.toSingleNativeObject(resultSet, objectClass)
        : toSinglePojo(objectClass, resultSet);
  }

  private final <T> T toSinglePojo(final Class<T> objectClass, final ResultSet resultSet)
      throws SQLException {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadPojo(resultSet);
  }

  private TableName toTableName(Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass,
        Try.createFunctionWithThrow(
            k -> tableNameMapper.getTableName(objectClass, connection.getMetaData()),
            OrmException::new));
  }

  private TableName toTableName(String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName,
        Try.createFunctionWithThrow(
            k -> tableNameMapper.toValidTableName(tableName, connection.getMetaData()),
            OrmException::new));
  }

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

  private static final Set<Class<?>> nativeSqlTypes = Set.of(boolean.class, Boolean.class,
      byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class,
      Long.class, float.class, Float.class, double.class, Double.class, char.class, Character.class,
      byte[].class, Byte[].class, char[].class, Character[].class, String.class, BigDecimal.class,
      java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
      java.io.InputStream.class, java.io.Reader.class, java.sql.Clob.class, java.sql.Blob.class,
      Object.class);

  private static boolean isEnableToConvertNativeSqlType(final Class<?> type) {
    return nativeSqlTypes.contains(type);
  }

}
