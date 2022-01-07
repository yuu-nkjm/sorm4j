package org.nkjmlab.sorm4j.internal.mapping;

import static java.lang.System.*;
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
import org.nkjmlab.sorm4j.common.InsertResult;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.extension.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.extension.TableSql;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext.LogPoint;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.internal.sql.result.InsertResultImpl;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Holds mapping data from a given class and a table
 */
public final class TableMapping<T> extends Mapping<T> {

  private final Map<String, Class<?>> setterParameterTypeMap = new ConcurrentHashMap<>();
  private final SqlParametersSetter sqlParametersSetter;
  private final MultiRowProcessor<T> multiRowProcessor;

  private final TableMetaData tableMetaData;
  private final TableSql sql;
  private final LoggerContext loggerContext;

  public TableMapping(LoggerContext loggerContext, SormOptions options,
      ColumnValueToJavaObjectConverters columnValueConverter, SqlParametersSetter sqlParametersSetter,
      MultiRowProcessorFactory multiRowProcessorFactory, Class<T> objectClass,
      ColumnToAccessorMap columnToAccessorMap, TableMetaData tableMetaData, TableSql sql) {
    super(options, columnValueConverter, objectClass, columnToAccessorMap);
    this.loggerContext = loggerContext;
    this.tableMetaData = tableMetaData;
    this.sql = sql;
    this.sqlParametersSetter = sqlParametersSetter;
    @SuppressWarnings("unchecked")
    MultiRowProcessor<T> processor =
        (MultiRowProcessor<T>) multiRowProcessorFactory.getMultiRowProcessor(this);
    this.multiRowProcessor = processor;
  }

  private Class<?> getSetterParamType(String column) {
    return setterParameterTypeMap.computeIfAbsent(column,
        k -> getColumnToAccessorMap().get(column).getSetterParameterType());
  }

  public TableSql getSql() {
    return sql;
  }

  public Object[] getPrimaryKeyParameters(T object) {
    return getParameters(object, tableMetaData.getPrimaryKeys());
  }

  public Object[] getInsertParameters(T object) {
    return getParameters(object, tableMetaData.getNotAutoGeneratedColumns());
  }

  public Object[] getMergeParameters(T object) {
    return getParameters(object, tableMetaData.getNotAutoGeneratedColumns());
  }

  private Object[] getParametersWithoutAutoGeneratedColumns(T object) {
    return getParameters(object, tableMetaData.getNotAutoGeneratedColumns());
  }


  private Object[] getDeleteParameters(T object) {
    return getParameters(object, tableMetaData.getPrimaryKeys());
  }

  private Object[] getUpdateParameters(T object) {
    return getParameters(object, tableMetaData.getColumnsForUpdate());
  }

  public Object[] getParameters(Object object, List<String> columns) {
    if (object == null) {
      throw new SormException(ParameterizedStringUtils.newString(
          "Fail to get value from a instance of [{}] but it is null.", getObjectClass()));
    }
    final Object[] ret = new Object[columns.size()];

    for (int i = 0; i < ret.length; i++) {
      ret[i] = getColumnToAccessorMap().getValue(object, columns.get(i));
    }
    return ret;
  }

  private List<Object> setAutoGeneratedKeys(PreparedStatement stmt, T object) throws SQLException {
    try (ResultSet resultSet = stmt.getGeneratedKeys()) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      List<Object> ret = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = metaData.getColumnName(1);
        int columnType = metaData.getColumnType(1);
        Class<?> classType = getSetterParamType(columnName);
        final Object value =
            columnValueConverter.convertTo(options, resultSet, 1, columnType, classType);
        getColumnToAccessorMap().setValue(object, columnName, value);
        ret.add(value);
      }
      return ret;
    }
  }


  @SafeVarargs
  public final void setPrametersOfMultiRow(PreparedStatement stmt, T... objects)
      throws SQLException, IOException {
    Object[] parameters = Arrays.stream(objects)
        .flatMap(obj -> Arrays.stream(getParametersWithoutAutoGeneratedColumns(obj)))
        .toArray(Object[]::new);
    sqlParametersSetter.setParameters(options, stmt, parameters);
  }



  private int executeUpdate(Connection connection, String sql, final Object... parameters) {
    return OrmConnectionImpl.executeUpdateAndClose(loggerContext, options, connection,
        sqlParametersSetter, sql, parameters);
  }

  /**
   * Updates an object in the database. The object will be identified using its mapped table's
   * primary key. If no primary keys are defined in the mapped table, a {@link RuntimeException}
   * will be thrown.
   *
   *
   */

  public int update(Connection connection, T object) {
    throwExeptionIfPrimaryKeyIsNotExist();
    return executeUpdate(connection, getSql().getUpdateSql(), getUpdateParameters(object));
  }

  public void throwExeptionIfPrimaryKeyIsNotExist() {
    if (!tableMetaData.hasPrimaryKey()) {
      throw new SormException("This opperation requiers primary keys but Table ["
          + tableMetaData.getTableName() + "] doesn't have them.");
    }
  }


  public void throwExeptionIfAutoGeneratedColumnsExist() {
    if (tableMetaData.hasAutoGeneratedColumns()) {
      throw new SormException("This opperation requiers no autogenerated columns but Table ["
          + tableMetaData.getTableName() + "] has them.");
    }
  }



  /**
   * Updates a batch of objects in the database. The objects will be identified using their mapped
   * table's primary keys. If no primary keys are defined in the mapped table, a
   * {@link RuntimeException} will be thrown.
   *
   */

  public int[] update(Connection connection, @SuppressWarnings("unchecked") T... objects) {
    throwExeptionIfPrimaryKeyIsNotExist();
    return batch(connection, sql.getUpdateSql(), obj -> getUpdateParameters(obj), objects);
  }

  /**
   * Deletes an object in the database. The object will be identified using its mapped table's
   * primary key.
   *
   */
  public int delete(Connection connection, T object) {
    throwExeptionIfPrimaryKeyIsNotExist();
    return executeUpdate(connection, getSql().getDeleteSql(), getDeleteParameters(object));
  }

  /**
   * Updates a batch of objects in the database. The objects will be identified using their matched
   * table's primary keys. If no primary keys are defined in a given object, a RuntimeException will
   * be thrown.
   *
   */

  public int[] delete(Connection connection, @SuppressWarnings("unchecked") T... objects) {
    throwExeptionIfPrimaryKeyIsNotExist();
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


    try (PreparedStatement stmt = (!tableMetaData.hasAutoGeneratedColumns())
        ? connection.prepareStatement(insertSql)
        : connection.prepareStatement(insertSql, tableMetaData.getAutoGeneratedColumnsArray())) {
      final Object[] parameters = getInsertParameters(object);
      sqlParametersSetter.setParameters(options, stmt, parameters);

      final Optional<LogPoint> lp =
          loggerContext.createLogPointBeforeSql(LoggerContext.Category.EXECUTE_UPDATE,
              TableMapping.class, connection, insertSql, parameters);

      int rowsModified = stmt.executeUpdate();

      lp.ifPresent(_lp -> _lp.logAfterUpdate(rowsModified));

      List<Object> keys = setAutoGeneratedKeys(stmt, object);

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
    return this.multiRowProcessor.batch(options, con, sql, parameterCreator, objects);
  }


  @SuppressWarnings("unchecked")
  public int[] merge(Connection con, T... objects) {
    throwExeptionIfAutoGeneratedColumnsExist();
    int[] result = multiRowProcessor.multiRowMerge(con, objects);
    return result;
  }

  public String getFormattedString() {
    return "[" + TableMapping.class.getSimpleName() + "] Table [" + tableMetaData.getTableName()
        + "] is mapped to [" + getObjectClass().getName() + "] class. " + lineSeparator() + "PK="
        + tableMetaData.getPrimaryKeys() + ",  " + tableMetaData.getColumnsWithMetaData()
        + lineSeparator() + super.getColumnToAccessorString() + lineSeparator() + "  with ["
        + multiRowProcessor.getClass().getSimpleName() + "]";
  }

  public TableMetaData getTableMetaData() {
    return tableMetaData;
  }

}
