package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.PreparedStatementUtils.*;
import java.math.BigDecimal;
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
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.LazyResultSet;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.OrmCache;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.config.PreparedStatementParametersSetter;
import org.nkjmlab.sorm4j.config.TableNameMapper;
import org.nkjmlab.sorm4j.util.DebugPoint;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.StringUtils;
import org.nkjmlab.sorm4j.util.Try;

public abstract class AbstractOrmMapper implements SqlExecutor {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  private final ColumnFieldMapper fieldMapper;

  private final TableNameMapper tableNameMapper;
  private final ResultSetConverter sqlToJavaConverter;

  private final PreparedStatementParametersSetter javaToSqlConverter;

  private final Connection connection;

  private final MultiRowProcessorFactory batchConfig;

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
    this.sqlToJavaConverter = new ResultSetConverter(configStore.getSqlToJavaDataConverter());
    this.javaToSqlConverter = configStore.getJavaToSqlDataConverter();
    String cacheName = configStore.getCacheName();
    this.tableMappings = OrmCache.getTableMappings(cacheName);
    this.columnsMappings = OrmCache.getColumnsMappings(cacheName);
    this.classNameToValidTableNameMap = OrmCache.getClassNameToValidTableNameMap(cacheName);
    this.tableNameToValidTableNameMap = OrmCache.getTableNameToValidTableNameMaps(cacheName);
  }

  public <T> int deleteAll(Class<T> objectClass) {
    return getTableMapping(objectClass).deleteAll(connection);
  }


  public int deleteAllOn(String tableName) {
    return executeUpdate("DELETE FROM " + tableName);
  }


  private <R> R execPreparedStatementWithParameters(String sql, Object[] parameters,
      Function<PreparedStatement, R> func) {
    try (PreparedStatement stmt = getPreparedStatement(connection, sql)) {
      javaToSqlConverter.setParameters(stmt, parameters);
      return func.apply(stmt);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  private <R> R execResultSet(String sql, Object[] parameters,
      Function<ResultSet, R> sqlResultReader) {
    try (PreparedStatement stmt = getPreparedStatement(connection, sql)) {
      javaToSqlConverter.setParameters(stmt, parameters);
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
    } catch (Exception e) {
      throw new OrmException(StringUtils.format("Error in [{}] with {}", sql, parameters), e);
    }
  }

  protected final <T, R> R execSqlIfParameterExists(T[] objects,
      Function<TableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    TableMapping<T> mapping = getCastedTableMapping(objects[0].getClass());
    return sqlFunction.apply(mapping);
  }


  protected final <T, R> R execSqlIfParameterExists(T[] objects, String tableName,
      Function<TableMapping<T>, R> sqlFunction, Supplier<R> notExists) {
    if (objects == null || objects.length == 0) {
      return notExists.get();
    }
    TableMapping<T> mapping = getCastedTableMapping(tableName, objects[0].getClass());
    return sqlFunction.apply(mapping);
  }

  @Override
  public boolean execute(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.createFunctionWithThrow(stmt -> stmt.execute(), OrmException::new));
  }

  @Override
  public ResultSet executeQuery(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.createFunctionWithThrow(stmt -> stmt.executeQuery(), OrmException::new));
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.createFunctionWithThrow(stmt -> stmt.executeUpdate(), OrmException::new));
  }



  <T> List<String> getAllColumnsAux(Class<T> objectClass) {
    return getTableMapping(objectClass).getAllColumns();
  }


  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(objectClass);
  }

  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(String tableName, Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(tableName, objectClass);
  }

  public <T> ColumnsMapping<T> getColumnsMapping(Class<T> objectClass) {
    @SuppressWarnings("unchecked")
    ColumnsMapping<T> ret = (ColumnsMapping<T>) columnsMappings.computeIfAbsent(objectClass, _k -> {
      ColumnsMapping<T> m =
          ColumnsMapping.createMapping(sqlToJavaConverter, objectClass, fieldMapper);
      DebugPointFactory.createDebugPoint(DebugPointFactory.Name.MAPPING).ifPresent(dw -> log
          .debug("[{}] {}", dw.getName() + System.lineSeparator() + m.getFormattedString()));
      return m;
    });
    return ret;
  }


  protected OrmConfigStore getConfigStore() {
    return configStore;
  }

  @Override
  public Connection getJdbcConnection() {
    return connection;
  }



  <T> List<String> getPrimaryKeysAux(Class<T> objectClass) {
    return getTableMapping(objectClass).getPrimaryKeys();
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
          TableMapping<T> m = TableMapping.createMapping(sqlToJavaConverter, javaToSqlConverter,
              objectClass, tableName.getName(), fieldMapper, batchConfig, connection);
          log.info(System.lineSeparator() + m.getFormattedString());
          return m;
        }, OrmException::new));
    return ret;
  }


  public <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) {
    try {
      if (resultSet.next()) {
        return toSingleObject(objectClass, resultSet);
      }
      return null;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public Map<String, Object> loadFirstMap(ResultSet resultSet) {
    try {
      Map<String, Object> ret = null;
      if (resultSet.next()) {
        ret = toSingleMap(resultSet);
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public List<Map<String, Object>> loadMapList(ResultSet resultSet) {
    try {
      final List<Map<String, Object>> ret = new ArrayList<>();
      ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
      while (resultSet.next()) {
        ret.add(sqlToJavaConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  private final <T> List<T> loadNativeObjectList(Class<T> objectClass, ResultSet resultSet) {
    try {
      final Optional<DebugPoint> dp =
          DebugPointFactory.createDebugPoint(DebugPointFactory.Name.LOAD_OBJECT);
      final List<T> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(sqlToJavaConverter.toSingleNativeObject(resultSet, objectClass));
      }
      dp.ifPresent(sw -> {
        try {
          ResultSetMetaData metaData = resultSet.getMetaData();
          if (metaData.getColumnCount() != 1) {
            throw new OrmException("ResultSet returned [" + metaData.getColumnCount()
                + "] columns but 1 column was expected to load data into an instance of ["
                + objectClass.getName() + "]");
          }
        } catch (SQLException e) {
          throw new OrmException(e);
        }
      });
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }

  }

  public <T> T loadOne(Class<T> objectClass, ResultSet resultSet) {
    try {
      T ret = null;
      if (resultSet.next()) {
        ret = toSingleObject(objectClass, resultSet);
      }
      if (resultSet.next()) {
        throw new RuntimeException("Non-unique result returned");
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  public Map<String, Object> loadOneMap(ResultSet resultSet) {
    try {
      Map<String, Object> ret = null;
      if (resultSet.next()) {
        ret = toSingleMap(resultSet);
      }
      if (resultSet.next()) {
        throw new OrmException("Non-unique result returned");
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public final <T> List<T> loadPojoList(final Class<T> objectClass, final ResultSet resultSet) {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadObjectList(resultSet);
  }

  /**
   * Reads a list of all objects in the database mapped to the given object class.
   */
  protected final <T> List<T> readAllAux(final Class<T> objectClass) {
    final TableMapping<T> mapping = getTableMapping(objectClass);
    final String sql = mapping.getSql().getSelectAllSql();
    Optional<DebugPoint> dp = DebugPointFactory.createDebugPoint(DebugPointFactory.Name.READ);
    List<T> result = readListAux(objectClass, sql);
    dp.ifPresent(sw -> log.debug("{} Read [{}] objects of [{}]",
        sw.getFormattedNameAndElapsedTime(), result.size(), objectClass.getSimpleName()));
    return result;
  }

  public final <T> LazyResultSet<T> readAllLazyAux(Class<T> objectClass) {
    return readLazyAux(objectClass, getTableMapping(objectClass).getSql().getSelectAllSql());
  }


  /**
   * Reads an object from the database by its primary keys.
   */
  protected final <T> T readByPrimaryKeyAux(final Class<T> objectClass,
      final Object... primaryKeyValues) {
    final TableMapping<T> mapping = getTableMapping(objectClass);
    final String sql = mapping.getSql().getSelectByPrimaryKeySql();
    return readFirstAux(objectClass, sql, primaryKeyValues);
  }


  protected final <T> T readFirstAux(Class<T> objectClass, String sql, Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> loadFirst(objectClass, resultSet));
  }

  public final <T> LazyResultSet<T> readLazyAux(Class<T> objectClass, String sql,
      Object... parameters) {
    final PreparedStatement stmt = getPreparedStatement(connection, sql);
    try {
      javaToSqlConverter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      return new LazyResultSet<T>(this, objectClass, stmt, resultSet);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  protected final <T> List<T> readListAux(Class<T> objectClass, String sql, Object... parameters) {
    return execResultSet(sql, parameters,
        resultSet -> isEnableToConvertNativeSqlType(objectClass)
            ? loadNativeObjectList(objectClass, resultSet)
            : loadPojoList(objectClass, resultSet));
  }


  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
        if (resultSet.next()) {
          return sqlToJavaConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
        }
        return null;
      } catch (SQLException e) {
        throw new OrmException(e);
      }
    });
  }



  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    final PreparedStatement stmt = getPreparedStatement(connection, sql);
    try {
      javaToSqlConverter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      @SuppressWarnings({"unchecked", "rawtypes", "resource"})
      LazyResultSet<Map<String, Object>> ret =
          (LazyResultSet<Map<String, Object>>) new LazyResultSet(this, Map.class, stmt, resultSet);
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> loadMapList(resultSet));
  }

  public Map<String, Object> readMapOne(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
        Map<String, Object> ret = null;
        if (resultSet.next()) {
          ret = sqlToJavaConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());
        }
        if (resultSet.next()) {
          throw new OrmException("Non-unique result returned");
        }
        return ret;
      } catch (SQLException e) {
        throw new OrmException(e);
      }
    });
  }

  protected final <T> T readOneAux(final Class<T> objectClass, final String sql,
      Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        T ret = null;
        if (resultSet.next()) {
          ret = toSingleObject(objectClass, resultSet);
        }
        if (resultSet.next()) {
          throw new OrmException("Non-unique result returned");
        }
        return ret;
      } catch (SQLException e) {
        throw new OrmException(e);
      }
    });
  }

  public Map<String, Object> toSingleMap(ResultSet resultSet) {
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
    return sqlToJavaConverter.toSingleMap(resultSet, ct.getColumns(), ct.getColumnTypes());

  }

  public <T> T toSingleObject(Class<T> objectClass, ResultSet resultSet) {
    return isEnableToConvertNativeSqlType(objectClass)
        ? sqlToJavaConverter.toSingleNativeObject(resultSet, objectClass)
        : toSinglePojo(objectClass, resultSet);
  }

  private final <T> T toSinglePojo(final Class<T> objectClass, final ResultSet resultSet) {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.loadObject(resultSet);
  }

  private TableName toTableName(Class<?> objectClass) {
    return classNameToValidTableNameMap.computeIfAbsent(objectClass,
        k -> tableNameMapper.getTableName(objectClass, connection));
  }

  private TableName toTableName(String tableName) {
    return tableNameToValidTableNameMap.computeIfAbsent(tableName,
        k -> tableNameMapper.toValidTableName(tableName, connection));
  }

  private static ColumnsAndTypes createColumnsAndTypes(ResultSet resultSet) {
    try {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int colNum = metaData.getColumnCount();
      List<String> columns = new ArrayList<>(colNum);
      List<Integer> columnTypes = new ArrayList<>(colNum);
      for (int i = 1; i <= colNum; i++) {
        columns.add(metaData.getColumnName(i));
        columnTypes.add(metaData.getColumnType(i));
      }
      return new ColumnsAndTypes(columns, columnTypes);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
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
