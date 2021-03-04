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
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.PreparedStatementParametersSetter;
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
  private final Map<String, Class<?>> setterParamTypeMap = new ConcurrentHashMap<>();

  final PreparedStatementParametersSetter preparedStatementParametersSetter;

  private final String tableName;
  private final List<String> primaryKeys;
  private final List<String> autoGeneratedColumns;
  private final String[] autoGeneratedColumnsArray;
  private final List<String> notAutoGeneratedColumns;
  private final List<String> columnsForUpdate;
  private final List<String> allColumns;

  private final SqlFromTableMapping sql;

  private final MultiRowProcessor<T> multiRowProcessor;
  private final boolean hasPrimaryKey;
  private final boolean hasAutoGeneratedColumns;


  private TableMapping(ResultSetConverter sqlToJavaConverter,
      PreparedStatementParametersSetter javaToSqlConverter, Class<T> objectClass, String tableName,
      List<Column> columns, ColumnFieldMapper fieldMapper,
      MultiRowProcessorFactory multiRowProcessorFactory, Connection connection)
      throws SQLException {
    super(sqlToJavaConverter, objectClass, columns, fieldMapper);
    this.preparedStatementParametersSetter = javaToSqlConverter;

    @SuppressWarnings("unchecked")
    MultiRowProcessor<T> processor =
        (MultiRowProcessor<T>) multiRowProcessorFactory.getMultiRowProcessorFactory().apply(this);
    this.multiRowProcessor = processor;
    DatabaseMetaData metaData = connection.getMetaData();
    this.tableName = tableName;



    // all primary keys (from db)
    this.primaryKeys = fieldMapper.getPrimaryKeys(metaData, tableName);
    this.hasPrimaryKey = getPrimaryKeys().size() != 0;

    List<Column> _autoGeneratedColumns = fieldMapper.getAutoGeneratedColumns(metaData, tableName);

    this.autoGeneratedColumns =
        _autoGeneratedColumns.stream().map(c -> c.getName()).collect(Collectors.toList());
    this.autoGeneratedColumnsArray = autoGeneratedColumns.toArray(String[]::new);
    this.hasAutoGeneratedColumns = autoGeneratedColumns.size() != 0;

    this.notAutoGeneratedColumns =
        columns.stream().filter(col -> !_autoGeneratedColumns.contains(col)).map(c -> c.getName())
            .collect(Collectors.toList());

    List<String> notPrimaryKeys = createNoPrimaryKeys(primaryKeys, columns).stream()
        .map(c -> c.getName()).collect(Collectors.toList());
    this.columnsForUpdate = new ArrayList<>(notPrimaryKeys);
    columnsForUpdate.addAll(primaryKeys);

    this.allColumns = columns.stream().map(c -> c.getName()).collect(Collectors.toList());
    // assemble sql blocks to be used by crud sql statements
    this.sql = new SqlFromTableMapping(tableName, primaryKeys, notPrimaryKeys, autoGeneratedColumns,
        notAutoGeneratedColumns, allColumns);

    if (!StringUtils.equalsSetIgnoreCase(allColumns, columnToAccessorMap.keySet())) {
      throw new OrmException(StringUtils.format(
          "{} does not match any field. Table [{}] contains Columns {} but [{}] contains Fields {}.",
          allColumns.stream().filter(e -> !columnToAccessorMap.keySet().contains(e)).sorted()
              .collect(Collectors.toList()),
          tableName, columns.stream().sorted().collect(Collectors.toList()), objectClass.getName(),
          columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList())));
    }

  }



  public static final <T> TableMapping<T> createMapping(ResultSetConverter sqlToJavaConverter,
      PreparedStatementParametersSetter javaToSqlConverter, Class<T> objectClass, String tableName,
      ColumnFieldMapper fieldMapper, MultiRowProcessorFactory batchConfig, Connection connection)
      throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    return new TableMapping<>(sqlToJavaConverter, javaToSqlConverter, objectClass, tableName,
        fieldMapper.getColumns(metaData, tableName), fieldMapper, batchConfig, connection);
  }


  private static List<Column> createNoPrimaryKeys(List<String> primaryKeys, List<Column> columns) {
    return columns.stream().filter(col -> !primaryKeys.contains(col.getName()))
        .collect(Collectors.toList());
  }



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
    if (object == null) {
      throw new OrmException(StringUtils
          .format("Fail to get value from a instance of [{}] but it is null.", objectClass));
    }
    return columns.stream().map(columnName -> getValue(object, columnName)).toArray(Object[]::new);
  }



  private List<Object> setAutoGeneratedKeys(PreparedStatement stmt, T object) throws SQLException {
    try (ResultSet resultSet = stmt.getGeneratedKeys()) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      List<Object> ret = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = metaData.getColumnName(1);
        // Don't user type from metadata (metaData.getColumnType(1)) because object class of
        // container is prior.
        Class<?> classType = getSetterParamType(columnName);
        final Object value = resultSetConverter.getValueByClass(resultSet, 1, classType);
        setValue(object, columnName, value);
        ret.add(value);
      }
      return ret;
    }
  }


  @SafeVarargs
  final void setPrameters(PreparedStatement stmt, T... objects) throws SQLException {
    Object[] parameters = Arrays.stream(objects)
        .flatMap(obj -> Arrays.stream(getParametersWithoutAutoGeneratedColumns(obj)))
        .toArray(Object[]::new);
    preparedStatementParametersSetter.setParameters(stmt, parameters);
  }



  private int executeUpdate(Connection connection, String sql, final Object... parameters) {
    final Optional<DebugPoint> dp =
        DebugPointFactory.createDebugPoint(DebugPointFactory.Name.EXECUTE_UPDATE);
    int ret = AbstractOrmMapper.execPreparedStatementAndClose(preparedStatementParametersSetter,
        connection, sql, parameters, stmt -> stmt.executeUpdate());
    dp.ifPresent(
        sw -> log.debug("{} ExecuteUpdate for one object of [{}] to [{}] Table at [{}] =? [{}]",
            sw.getFormattedNameAndElapsedTime(), objectClass.getSimpleName(), getTableName(),
            Try.getOrNull(() -> connection.getMetaData().getURL()), sql));
    return ret;
  }

  /**
   * Updates an object in the database. The object will be identified using its mapped table's
   * primary key. If no primary keys are defined in the mapped table, a {@link RuntimeException}
   * will be thrown.
   *
   *
   */

  public int update(Connection connection, T object) {
    throwExeptionIfPrimaryKeysIsNotExist();
    return executeUpdate(connection, getSql().getUpdateSql(), getUpdateParameters(object));
  }

  public void throwExeptionIfPrimaryKeysIsNotExist() {
    if (!hasPrimaryKey) {
      throw new OrmException("This opperation requiers primary keys but Table [" + getTableName()
          + "] doesn't have them.");
    }
  }


  public void throwExeptionIfAutoGeneratedColumnsExist() {
    if (hasAutoGeneratedColumns) {
      throw new OrmException("This opperation requiers no autogenerated columns but Table ["
          + getTableName() + "] has them.");
    }
  }



  /**
   * Updates a batch of objects in the database. The objects will be identified using their mapped
   * table's primary keys. If no primary keys are defined in the mapped table, a
   * {@link RuntimeException} will be thrown.
   *
   */

  public int[] update(Connection connection, @SuppressWarnings("unchecked") T... objects) {
    throwExeptionIfPrimaryKeysIsNotExist();
    return batch(connection, sql.getUpdateSql(), obj -> getUpdateParameters(obj), objects);
  }

  /**
   * Deletes an object in the database. The object will be identified using its mapped table's
   * primary key.
   *
   */
  public int delete(Connection connection, T object) {
    throwExeptionIfPrimaryKeysIsNotExist();
    return executeUpdate(connection, getSql().getDeleteSql(), getDeleteParameters(object));
  }

  /**
   * Updates a batch of objects in the database. The objects will be identified using their matched
   * table's primary keys. If no primary keys are defined in a given object, a RuntimeException will
   * be thrown.
   *
   */

  public int[] delete(Connection connection, @SuppressWarnings("unchecked") T... objects) {
    throwExeptionIfPrimaryKeysIsNotExist();
    return batch(connection, sql.getDeleteSql(), obj -> getDeleteParameters(obj), objects);
  }

  public int deleteAll(Connection connection) {
    return executeUpdate(connection, sql.getDeleteAllSql());
  }


  public int insert(Connection connection, T object) {
    return executeUpdate(connection, sql.getInsertSql(), getInsertParameters(object));
  }

  public int merge(Connection connection, T object) {
    throwExeptionIfAutoGeneratedColumnsExist();
    return executeUpdate(connection, sql.getMergeSql(), getMergeParameters(object));
  }



  public InsertResult<T> insertAndGetResult(Connection connection, T object) {
    try (PreparedStatement stmt = PreparedStatementUtils.getPreparedStatementWithAutoGeneratedKeys(
        connection, sql.getInsertSql(), autoGeneratedColumnsArray)) {
      preparedStatementParametersSetter.setParameters(stmt, getInsertParameters(object));
      int rowsModified = stmt.executeUpdate();
      List<Object> keys = setAutoGeneratedKeys(stmt, object);
      return new InsertResult<T>(new int[] {rowsModified}, object, keys);
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }



  @SafeVarargs
  public final int[] insert(Connection con, T... objects) {
    return multiRowProcessor.multiRowInsert(con, objects);
  }



  /**
   *
   * @param con
   * @param objects not allow {@code null} or {@code 0} length
   * @return
   */
  @SafeVarargs
  public final InsertResult<T> insertAndGet(Connection con, T... objects) {
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
    return this.multiRowProcessor.batch(con, sql, parameterCreator, objects);
  }


  @SuppressWarnings("unchecked")
  public int[] merge(Connection con, T... objects) {
    throwExeptionIfAutoGeneratedColumnsExist();
    int[] result = multiRowProcessor.multiRowMerge(con, objects);
    return result;
  }

  public String getFormattedString() {
    return "TABLE [" + tableName + "] is mapped to [" + objectClass.getSimpleName() + "] class. "
        + "PRIMARY KEY is " + primaryKeys + System.lineSeparator()
        + super.getColumnToAccessorString() + System.lineSeparator() + "["
        + multiRowProcessor.getClass().getSimpleName() + "] is used for processing multirow.";
  }

}
