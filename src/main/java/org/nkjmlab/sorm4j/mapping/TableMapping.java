package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.InsertResult;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.config.JavaToSqlDataConverter;
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.SqlToJavaDataConverter;
import org.nkjmlab.sorm4j.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.DebugPoint;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.PreparedStatementUtils;
import org.nkjmlab.sorm4j.util.StringUtils;
import org.nkjmlab.sorm4j.util.Try;

/**
 * Holds mapping data from a given class and a table
 */
public final class TableMapping<T> extends Mapping<T> {

  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  final JavaToSqlDataConverter javaToSqlConverter;

  private final String tableName;
  private final List<String> primaryKeys;
  private final List<String> autoGeneratedColumns;
  private final String[] autoGeneratedColumnsArray;
  private final List<String> notAutoGeneratedColumns;
  private final List<String> columnsForUpdate;
  private final List<String> allColumns;

  private final SqlFromTableMapping sql;

  final MultiRowProcessor<T> batcher;


  private TableMapping(SqlToJavaDataConverter sqlToJavaConverter,
      JavaToSqlDataConverter javaToSqlConverter, Class<T> objectClass, String tableName,
      List<Column> columns, ColumnFieldMapper fieldMapper, MultiRowProcessorFactory batchConf,
      Connection connection) {
    super(sqlToJavaConverter, objectClass, columns, fieldMapper);
    try {
      this.javaToSqlConverter = javaToSqlConverter;

      this.batcher = (MultiRowProcessor<T>) batchConf.getMultiRowProcessorFactory().apply(this);
      DatabaseMetaData metaData = connection.getMetaData();
      this.tableName = tableName;



      // all primary keys (from db)
      this.primaryKeys = fieldMapper.getPrimaryKeys(metaData, tableName);


      List<Column> _autoGeneratedColumns = fieldMapper.getAutoGeneratedColumns(metaData, tableName);

      this.autoGeneratedColumns =
          _autoGeneratedColumns.stream().map(c -> c.getName()).collect(Collectors.toList());
      this.autoGeneratedColumnsArray = autoGeneratedColumns.toArray(String[]::new);

      this.notAutoGeneratedColumns =
          columns.stream().filter(col -> !_autoGeneratedColumns.contains(col)).map(c -> c.getName())
              .collect(Collectors.toList());

      List<String> notPrimaryKeys = createNoPrimaryKeys(primaryKeys, columns).stream()
          .map(c -> c.getName()).collect(Collectors.toList());
      this.columnsForUpdate = new ArrayList<>(notPrimaryKeys);
      columnsForUpdate.addAll(primaryKeys);

      this.allColumns = columns.stream().map(c -> c.getName()).collect(Collectors.toList());
      // assemble sql blocks to be used by crud sql statements
      this.sql = new SqlFromTableMapping(tableName, primaryKeys, notPrimaryKeys,
          autoGeneratedColumns, notAutoGeneratedColumns, allColumns);

      if (!StringUtils.equalsSetIgnoreCase(allColumns, columnToAccessorMap.keySet())) {
        throw new OrmException(StringUtils.format(
            "{} does not match any field. Table [{}] contains Columns {} but [{}] contains Fields {}.",
            allColumns.stream().filter(e -> !columnToAccessorMap.keySet().contains(e)).sorted()
                .collect(Collectors.toList()),
            tableName, columns.stream().sorted().collect(Collectors.toList()),
            objectClass.getName(),
            columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList())));
        // .orElseThrow(() -> new RuntimeException(MessageUtils.format(
        // "Column [{}] does not match any field of [{}]. The field names of the class are {}.",
        // column, objectClass.getName(), fields.keySet())))
      }

    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }



  public static final <T> TableMapping<T> createMapping(SqlToJavaDataConverter sqlToJavaConverter,
      JavaToSqlDataConverter javaToSqlConverter, Class<T> objectClass, String tableName,
      ColumnFieldMapper fieldMapper, MultiRowProcessorFactory batchConfig, Connection connection) {
    try {
      DatabaseMetaData metaData = connection.getMetaData();
      return new TableMapping<>(sqlToJavaConverter, javaToSqlConverter, objectClass, tableName,
          fieldMapper.getColumns(metaData, tableName), fieldMapper, batchConfig, connection);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  private static List<Column> createNoPrimaryKeys(List<String> primaryKeys, List<Column> columns) {
    return columns.stream().filter(col -> !primaryKeys.contains(col.getName()))
        .collect(Collectors.toList());
  }



  private static final Map<String, Class<?>> setterParamTypeMap = new ConcurrentHashMap<>();

  private Class<?> getSetterParamType(String column) {
    return setterParamTypeMap.computeIfAbsent(column,
        k -> columnToAccessorMap.get(column).getSetterParameterType());
  }


  public String getTableName() {
    return tableName;
  }

  public List<String> getAllColumns() {
    return allColumns;
  }

  public List<String> notAutoGeneratedColumns() {
    return notAutoGeneratedColumns;
  }


  public List<String> getPrimaryKeys() {
    return primaryKeys;
  }

  public SqlFromTableMapping getSql() {
    return sql;
  }

  Object[] getInsertParameters(T object) {
    return getParameters(object, notAutoGeneratedColumns);
  }

  Object[] getMergeParameters(T object) {
    return getParameters(object, notAutoGeneratedColumns);
  }

  private Object[] getParametersWithoutAutoGeneratedColumns(T object) {
    return getParameters(object, notAutoGeneratedColumns);
  }


  private Object[] getDeleteParameters(T object) {
    return getParameters(object, getPrimaryKeys());
  }

  private Object[] getUpdateParameters(T object) {
    return getParameters(object, columnsForUpdate);
  }

  public Object[] getParameters(Object object, List<String> columns) {
    return columns.stream().map(columnName -> getValue(object, columnName)).toArray(Object[]::new);
  }



  private List<Object> setAutoGeneratedKeys(PreparedStatement stmt, T object) {
    try (ResultSet resultSet = stmt.getGeneratedKeys()) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      List<Object> ret = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = metaData.getColumnName(1);
        // Don't user type from metadata (metaData.getColumnType(1)) because object class of
        // container is prior.
        Class<?> type = getSetterParamType(columnName);
        final Object value = sqlToJavaConverter.getValueByClass(resultSet, 1, type);
        setValue(object, columnName, value);
        ret.add(value);
      }
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }

  }


  @SafeVarargs
  final void setPrameters(PreparedStatement stmt, T... objects) {
    Object[] parameters = Arrays.stream(objects)
        .flatMap(obj -> Arrays.stream(getParametersWithoutAutoGeneratedColumns(obj)))
        .toArray(Object[]::new);
    try {
      javaToSqlConverter.setParameters(stmt, parameters);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }



  private int executeUpdate(Connection connection, String sql, final Object... parameters) {
    try (PreparedStatement stmt = PreparedStatementUtils.getPreparedStatement(connection, sql)) {
      javaToSqlConverter.setParameters(stmt, parameters);
      final Optional<DebugPoint> dp =
          DebugPointFactory.createDebugPoint(DebugPointFactory.Name.EXECUTE_UPDATE);
      int ret = stmt.executeUpdate();
      dp.ifPresent(
          sw -> log.debug("{} ExecuteUpdate for one object of [{}] to [{}] Table at [{}] =? [{}]",
              sw.getFormattedNameAndElapsedTime(), objectClass.getSimpleName(), getTableName(),
              Try.getForceOrNull(() -> connection.getMetaData().getURL()), sql));
      return ret;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  /**
   * Updates an object in the database. The object will be identified using its mapped table's
   * primary key. If no primary keys are defined in the mapped table, a {@link RuntimeException}
   * will be thrown.
   *
   * @since 1.0
   */


  public int update(Connection connection, T object) {
    if (getPrimaryKeys().size() == 0) {
      throw new OrmException("Table " + getTableName() + " doesn't have a primary key");
    }
    return executeUpdate(connection, getSql().getUpdateSql(), getUpdateParameters(object));
  }

  /**
   * Updates a batch of objects in the database. The objects will be identified using their mapped
   * table's primary keys. If no primary keys are defined in the mapped table, a
   * {@link RuntimeException} will be thrown.
   *
   * @since 1.0
   */

  public int[] update(Connection connection, T... objects) {
    return batch(connection, sql.getUpdateSql(), obj -> getUpdateParameters(obj), objects);
  }

  /**
   * Deletes an object in the database. The object will be identified using its mapped table's
   * primary key. If no primary keys are defined in the mapped table, a RuntimeException will be
   * thrown.
   *
   * @since 1.0
   */


  public int delete(Connection connection, T object) {
    if (getPrimaryKeys().size() == 0) {
      throw new OrmException("Table " + getTableName() + " doesn't have a primary key");
    }
    return executeUpdate(connection, getSql().getDeleteSql(), getDeleteParameters(object));
  }

  /**
   * Updates a batch of objects in the database. The objects will be identified using their matched
   * table's primary keys. If no primary keys are defined in a given object, a RuntimeException will
   * be thrown.
   *
   * @since 1.0
   */

  public int[] delete(Connection connection, @SuppressWarnings("unchecked") T... objects) {
    return batch(connection, sql.getDeleteSql(), obj -> getDeleteParameters(obj), objects);
  }



  public int insert(Connection connection, T object) {
    return executeUpdate(connection, sql.getInsertSql(), getInsertParameters(object));
  }

  public int merge(Connection connection, T object) {
    return executeUpdate(connection, sql.getInsertSql(), getMergeParameters(object));
  }



  public InsertResult<T> insertAndGetResult(Connection connection, T object) {
    try (PreparedStatement stmt = PreparedStatementUtils.getPreparedStatement(connection,
        sql.getInsertSql(), autoGeneratedColumnsArray)) {
      javaToSqlConverter.setParameters(stmt, getInsertParameters(object));
      int rowsModified = stmt.executeUpdate();
      List<Object> keys = setAutoGeneratedKeys(stmt, object);
      return new InsertResult<T>(new int[] {rowsModified}, object, keys);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }



  @SafeVarargs
  public final int[] insert(Connection con, T... objects) {
    return batcher.multiRowInsert(con, objects);
  }



  @SafeVarargs
  public final InsertResult<T> insertAndGetResult(Connection con, T... objects) {
    if (objects == null || objects.length == 0) {
      return InsertResult.empty();
    }

    @SuppressWarnings("unchecked")
    T[] objsWithoutLast =
        (T[]) Arrays.asList(objects).subList(0, objects.length - 1).toArray(Object[]::new);
    T last = objects[objects.length - 1];
    int[] resultWithoutLast = insert(con, objsWithoutLast);

    InsertResult<T> insertResult = insertAndGetResult(con, last);
    int[] result = ArrayUtils.add(resultWithoutLast, insertResult.getRowsModified()[0]);

    return new InsertResult<T>(result, insertResult.getObject(),
        insertResult.getAutoGeneratedKeys());
  }

  public int[] batch(Connection con, String sql, Function<T, Object[]> parameterCreator,
      T[] objects) {
    return this.batcher.batch(con, sql, parameterCreator, objects);
  }



  @SuppressWarnings("unchecked")
  public int[] merge(Connection con, T... objects) {
    int[] result = batcher.multiRowMerge(con, objects);
    return result;
  }



  @Override
  public String toString() {
    return "TableMapping [" + super.toString() + "]";
  }

  public String getFormattedString() {
    return "TABLE [" + tableName + "] is mapped to [" + objectClass.getSimpleName() + "] class. "
        + "PRIMARY KEY is " + primaryKeys + System.lineSeparator()
        + super.getColumnToAccessorString();
  }



}
