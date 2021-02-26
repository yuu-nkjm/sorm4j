package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.util.PreparedStatementUtils.*;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.config.JavaToSqlDataConverter;
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.config.SqlToJavaDataConverter;
import org.nkjmlab.sorm4j.config.TableNameMapper;
import org.nkjmlab.sorm4j.mapping.ColumnsMapping;
import org.nkjmlab.sorm4j.mapping.TableMapping;
import org.nkjmlab.sorm4j.util.DebugPoint;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.StringUtils;
import org.nkjmlab.sorm4j.util.Try;

abstract class AbstractOrmMapper implements SqlExecutor {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();
  private final static ConcurrentMap<Class<?>, String> classNameToTableNameMap =
      new ConcurrentHashMap<>();

  private final ColumnFieldMapper fieldMapper;
  private final TableNameMapper tableNameMapper;
  private final SqlToJavaDataConverter sqlToJavaConverter;
  private final JavaToSqlDataConverter javaToSqlConverter;

  private final ConcurrentMap<String, TableMapping<?>> tableMappings;
  private final ConcurrentMap<Class<?>, ColumnsMapping<?>> columnsMappings;
  private final Connection connection;
  private final MultiRowProcessorFactory batchConfig;
  private final OrmConfigStore configStore;


  /**
   * Creates a instance
   *
   * @param connection {@link java.sql.Connection} object to be used
   * @param cacheName
   * @param fieldNameGuesser
   * @param tableNameGuesser
   * @param converter
   */
  AbstractOrmMapper(Connection connection, OrmConfigStore configStore) {
    this.connection = connection;
    this.configStore = configStore;
    this.batchConfig = configStore.getBatchConfig();
    this.fieldMapper = configStore.getFieldNameMapper();
    this.tableNameMapper = configStore.getTableNameMapper();
    this.sqlToJavaConverter = configStore.getSqlToJavaDataConverter();
    this.javaToSqlConverter = configStore.getJavaToSqlDataConverter();
    String cacheName = configStore.getCacheName();
    this.tableMappings = OrmService.getTableMappings(cacheName);
    this.columnsMappings = OrmService.getColumnsMappings(cacheName);
  }



  public <T> TableMapping<T> getTableMapping(Class<T> objectClass) {
    String tableName = toTableName(objectClass);
    return getTableMapping(tableName, objectClass);
  }

  /**
   * Get table mapping by the table name and the object class. When there is no mapping, the method
   * create a mapping and register it.
   *
   */
  <T> TableMapping<T> getTableMapping(String tableName, Class<T> objectClass) {
    String key = tableName + "-" + objectClass.getName();
    @SuppressWarnings("unchecked")
    TableMapping<T> ret =
        (TableMapping<T>) tableMappings.computeIfAbsent(key, Try.applyOrThrow(_tableName -> {
          TableMapping<T> m = TableMapping.createMapping(sqlToJavaConverter, javaToSqlConverter,
              objectClass, tableName, fieldMapper, batchConfig, connection);
          log.debug(System.lineSeparator() + m.getFormattedString());
          return m;
        }, OrmException::new));
    return ret;
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

  private String toTableName(Class<?> objectClass) {
    String tableName = classNameToTableNameMap.computeIfAbsent(objectClass,
        k -> tableNameMapper.getTableName(objectClass, connection));
    return tableName;
  }


  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(objectClass);
  }

  @SuppressWarnings("unchecked")
  protected <T> TableMapping<T> getCastedTableMapping(String tableName, Class<?> objectClass) {
    return (TableMapping<T>) getTableMapping(tableName, objectClass);
  }

  public Connection getJdbcConnection() {
    return connection;
  }


  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.applyOrThrow(stmt -> stmt.executeUpdate(), OrmException::new));
  }

  @Override
  public boolean execute(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.applyOrThrow(stmt -> stmt.execute(), OrmException::new));
  }

  @Override
  public ResultSet executeQuery(String sql, Object... parameters) {
    return execPreparedStatementWithParameters(sql, parameters,
        Try.applyOrThrow(stmt -> stmt.executeQuery(), OrmException::new));
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



  protected final <T> T readOneAux(final Class<T> objectClass, final String sql,
      Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        T ret = null;
        if (resultSet.next()) {
          ret = loadOneObject(objectClass, resultSet);
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


  protected final <T> T readFirstAux(Class<T> objectClass, String sql, Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> loadFirst(objectClass, resultSet));
  }

  <T> T loadFirst(Class<T> objectClass, ResultSet resultSet) {
    try {
      if (resultSet.next()) {
        return loadOneObject(objectClass, resultSet);
      }
      return null;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }



  <T> T loadOneObject(Class<T> objectClass, ResultSet resultSet) {
    return isEnableToConvertNativeSqlType(objectClass) ? loadOneNativeObject(objectClass, resultSet)
        : loadOnePojo(objectClass, resultSet);
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



  protected <T> List<T> readListAux(Class<T> objectClass, String sql, Object... parameters) {
    return execResultSet(sql, parameters,
        resultSet -> isEnableToConvertNativeSqlType(objectClass)
            ? loadNativeObjectList(objectClass, resultSet)
            : loadPojoList(objectClass, resultSet));
  }


  /**
   * Reads a list of all objects in the database mapped to the given object class.
   */
  protected <T> List<T> readAllAux(final Class<T> objectClass) {
    final TableMapping<T> mapping = getTableMapping(objectClass);
    final String sql = mapping.getSql().getSelectAllSql();
    Optional<DebugPoint> dp = DebugPointFactory.createDebugPoint(DebugPointFactory.Name.READ);
    List<T> result = readListAux(objectClass, sql);
    dp.ifPresent(sw -> log.debug("{} Read [{}] objects of [{}]",
        sw.getFormattedNameAndElapsedTime(), result.size(), objectClass.getSimpleName()));
    return result;
  }

  <T> int deleteAllAux(Class<T> objectClass) {
    return getTableMapping(objectClass).deleteAll(connection);
  }

  <T> int deleteAllAux(String tableName, Class<T> objectClass) {
    return getTableMapping(tableName, objectClass).deleteAll(connection);
  }


  public Map<String, Object> readMap(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
        Map<String, Object> ret = null;
        if (resultSet.next()) {
          ret = loadOneMap(resultSet, ct.getColumns(), ct.getColumnTypes());
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

  public Map<String, Object> readMapFirst(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> {
      try {
        ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
        if (resultSet.next()) {
          return loadOneMap(resultSet, ct.getColumns(), ct.getColumnTypes());
        }
        return null;
      } catch (SQLException e) {
        throw new OrmException(e);
      }
    });
  }


  public List<Map<String, Object>> readMapList(final String sql, final Object... parameters) {
    return execResultSet(sql, parameters, resultSet -> loadMapList(resultSet));
  }

  List<Map<String, Object>> loadMapList(ResultSet resultSet) {
    try {
      final List<Map<String, Object>> ret = new ArrayList<>();
      ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
      while (resultSet.next()) {
        ret.add(loadOneMap(resultSet, ct.getColumns(), ct.getColumnTypes()));
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  public final <T> ReadResultSet<T> readAllLazyAux(Class<T> objectClass) {
    return readLazyAux(objectClass, getTableMapping(objectClass).getSql().getSelectAllSql());
  }

  public final <T> ReadResultSet<T> readLazyAux(Class<T> objectClass, String sql,
      Object... parameters) {
    final PreparedStatement stmt = getPreparedStatement(connection, sql);
    try {
      javaToSqlConverter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      return new ReadResultSet<T>(this, objectClass, stmt, resultSet);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  public ReadResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    final PreparedStatement stmt = getPreparedStatement(connection, sql);
    try {
      javaToSqlConverter.setParameters(stmt, parameters);
      final ResultSet resultSet = stmt.executeQuery();
      @SuppressWarnings({"unchecked", "rawtypes", "resource"})
      ReadResultSet<Map<String, Object>> ret =
          (ReadResultSet<Map<String, Object>>) new ReadResultSet(this, Map.class, stmt, resultSet);
      return ret;
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
                Try.getForceOrNull(() -> connection.getMetaData().getURL())));
        return ret;
      }
    } catch (Exception e) {
      throw new OrmException(StringUtils.format("Error in [{}] with {}", sql, parameters), e);
    }
  }


  private <T> List<T> loadNativeObjectList(Class<T> objectClass, ResultSet resultSet) {
    try {
      final Optional<DebugPoint> dp =
          DebugPointFactory.createDebugPoint(DebugPointFactory.Name.LOAD_OBJECT);
      final List<T> ret = new ArrayList<>();
      while (resultSet.next()) {
        ret.add(loadOneNativeObject(objectClass, resultSet));
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


  final <T> List<T> loadPojoList(final Class<T> objectClass, final ResultSet resultSet) {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.createObjectList(resultSet);
  }

  private final <T> T loadOnePojo(final Class<T> objectClass, final ResultSet resultSet) {
    ColumnsMapping<T> mapping = getColumnsMapping(objectClass);
    return mapping.createObject(resultSet);
  }

  private final <T> T loadOneNativeObject(final Class<T> objectClass, final ResultSet resultSet) {
    try {
      // Don't user type from metadata (metaData.getColumnType(1)) because object class of container
      // is prior.
      Object value = sqlToJavaConverter.getValueByClass(resultSet, 1, objectClass);
      @SuppressWarnings("unchecked")
      T valueT = (T) value;
      return valueT;
    } catch (SQLException e) {
      throw new OrmException("Fail to get value as [" + objectClass.getSimpleName() + "]", e);
    }
  }

  Map<String, Object> loadOneMap(ResultSet resultSet) {
    ColumnsAndTypes ct = createColumnsAndTypes(resultSet);
    return loadOneMap(resultSet, ct.getColumns(), ct.getColumnTypes());

  }

  private Map<String, Object> loadOneMap(final ResultSet resultSet, List<String> columns,
      List<Integer> columnTypes) {
    try {
      final Map<String, Object> ret = new LinkedHashMap<>();
      for (int i = 1; i <= columns.size(); i++) {
        int type = columnTypes.get(i - 1);
        Object value = sqlToJavaConverter.getValueBySqlType(resultSet, i, type);
        ret.put(columns.get(i - 1), value);
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
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

  protected OrmConfigStore getConfigStore() {
    return configStore;
  }

  <T> List<String> getAllColumnsAux(Class<T> objectClass) {
    return getTableMapping(objectClass).getAllColumns();
  }

  <T> List<String> getPrimaryKeysAux(Class<T> objectClass) {
    return getTableMapping(objectClass).getPrimaryKeys();
  }

}
