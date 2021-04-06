package org.nkjmlab.sorm4j.internal.mapping;

import java.io.IOException;
import java.sql.Connection;
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
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SormLogger;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SqlParameterSetter;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.LogPoint;
import org.nkjmlab.sorm4j.internal.util.LogPointFactory;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.InsertResult;

/**
 * Holds mapping data from a given class and a table
 */
public final class TableMapping<T> extends Mapping<T> {

  private static final org.slf4j.Logger log =
      org.nkjmlab.sorm4j.internal.util.LoggerFactory.getLogger();

  private final Map<String, Class<?>> setterParameterTypeMap = new ConcurrentHashMap<>();
  private final SqlParameterSetter sqlParameterSetter;
  private final TableMappingSql sql;
  private final MultiRowProcessor<T> multiRowProcessor;

  public TableMapping(ResultSetConverter resultSetConverter, Class<T> objectClass,
      ColumnToAccessorMap columnToAccessorMap, SqlParameterSetter sqlParameterSetter,
      MultiRowProcessorFactory multiRowProcessorFactory, TableMappingSql sql) {
    super(resultSetConverter, objectClass, columnToAccessorMap);
    this.sqlParameterSetter = sqlParameterSetter;
    this.sql = sql;
    @SuppressWarnings("unchecked")
    MultiRowProcessor<T> processor =
        (MultiRowProcessor<T>) multiRowProcessorFactory.getMultiRowProcessor(this);
    this.multiRowProcessor = processor;
  }

  private Class<?> getSetterParamType(String column) {
    return setterParameterTypeMap.computeIfAbsent(column,
        k -> columnToAccessorMap.get(column).getSetterParameterType());
  }

  public TableMappingSql getSql() {
    return sql;
  }

  public Object[] getInsertParameters(T object) {
    return getParameters(object, sql.getNotAutoGeneratedColumns());
  }


  public Object[] getMergeParameters(T object) {
    return getParameters(object, sql.getNotAutoGeneratedColumns());
  }

  private Object[] getParametersWithoutAutoGeneratedColumns(T object) {
    return getParameters(object, sql.getNotAutoGeneratedColumns());
  }


  private Object[] getDeleteParameters(T object) {
    return getParameters(object, sql.getPrimaryKeys());
  }

  private Object[] getUpdateParameters(T object) {
    return getParameters(object, sql.getColumnsForUpdate());
  }

  private final Map<List<String>, Accessor[]> accessorsMap = new ConcurrentHashMap<>();

  public Object[] getParameters(Object object, List<String> columns) {
    if (object == null) {
      throw new SormException(StringUtils
          .format("Fail to get value from a instance of [{}] but it is null.", getObjectClass()));
    }
    final Accessor[] accessors = accessorsMap.computeIfAbsent(columns, k -> columns.stream()
        .map(columnName -> getAccessor(object, columnName)).toArray(Accessor[]::new));
    final Object[] ret = new Object[accessors.length];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = getValue(object, accessors[i]);
    }
    return ret;
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
        final Object value =
            resultSetConverter.getValueBySetterParameterType(resultSet, 1, classType);
        setValue(object, columnName, value);
        ret.add(value);
      }
      return ret;
    }
  }


  @SafeVarargs
  public final void setPrameters(PreparedStatement stmt, T... objects)
      throws SQLException, IOException {
    Object[] parameters = Arrays.stream(objects)
        .flatMap(obj -> Arrays.stream(getParametersWithoutAutoGeneratedColumns(obj)))
        .toArray(Object[]::new);
    sqlParameterSetter.setParameters(stmt, parameters);
  }



  private int executeUpdate(Connection connection, String sql, final Object... parameters) {
    final Optional<LogPoint> dp =
        LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_UPDATE);
    dp.ifPresent(lp -> log.debug("[{}] [{}] with {} parameters", lp.getTag(), sql,
        parameters == null ? 0 : parameters.length));

    int ret = OrmMapperImpl.executeUpdateAndClose(connection, sqlParameterSetter, sql, parameters);
    dp.ifPresent(lp -> {
      log.trace("[{}] Parameters = {}", lp.getTag(), parameters);
      log.debug("{} Call executeUpdate for [{}] to [{}] Table at [{}]", lp.getTagAndElapsedTime(),
          getObjectClass().getSimpleName(), getTableName(),
          Try.getOrNull(() -> connection.getMetaData().getURL()), sql);
    });
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
    if (!sql.hasPrimaryKey()) {
      throw new SormException("This opperation requiers primary keys but Table [" + getTableName()
          + "] doesn't have them.");
    }
  }


  public void throwExeptionIfAutoGeneratedColumnsExist() {
    if (sql.hasAutoGeneratedColumns()) {
      throw new SormException("This opperation requiers no autogenerated columns but Table ["
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

  public int insert(Connection connection, T object) {
    return executeUpdate(connection, sql.getInsertSql(), getInsertParameters(object));
  }

  public int merge(Connection connection, T object) {
    throwExeptionIfAutoGeneratedColumnsExist();
    return executeUpdate(connection, sql.getMergeSql(), getMergeParameters(object));
  }

  public InsertResult<T> insertAndGet(Connection connection, T object) {
    String insertSql = sql.getInsertSql();

    final Optional<LogPoint> dp =
        LogPointFactory.createLogPoint(SormLogger.Category.EXECUTE_UPDATE);
    dp.ifPresent(lp -> log.debug("[{}] [{}]", lp.getTag(), insertSql));

    try (PreparedStatement stmt =
        (!sql.hasAutoGeneratedColumns()) ? connection.prepareStatement(insertSql)
            : connection.prepareStatement(insertSql, sql.getAutoGeneratedColumnsArray())) {
      sqlParameterSetter.setParameters(stmt, getInsertParameters(object));

      int rowsModified = stmt.executeUpdate();
      List<Object> keys = setAutoGeneratedKeys(stmt, object);
      dp.ifPresent(lp -> {
        log.trace("[{}] Parameter = {}", lp.getTag(), object);
        log.debug("{} Call executeUpdate for [{}] to [{}] Table at [{}]", lp.getTagAndElapsedTime(),
            getObjectClass().getSimpleName(), getTableName(),
            Try.getOrNull(() -> connection.getMetaData().getURL()), insertSql);
      });
      return new InsertResultImpl<T>(new int[] {rowsModified}, object, keys);
    } catch (SQLException e) {
      throw Try.rethrow(e);
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

    InsertResult<T> insertResult = insertAndGet(con, last);
    int[] result = ArrayUtils.add(resultWithoutLast, insertResult.getRowsModified()[0]);

    return new InsertResultImpl<T>(result, insertResult.getObject(),
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
    return "[" + TableMapping.class.getSimpleName() + "] TABLE [" + getTableName()
        + "] is mapped to [" + getObjectClass().getSimpleName() + "] class. " + "PRIMARY KEY is "
        + sql.getPrimaryKeys() + System.lineSeparator() + super.getColumnToAccessorString()
        + System.lineSeparator() + "[" + multiRowProcessor.getClass().getSimpleName()
        + "] is used for processing multirow.";
  }

  public String getTableName() {
    return sql.getTableName();
  }

}
