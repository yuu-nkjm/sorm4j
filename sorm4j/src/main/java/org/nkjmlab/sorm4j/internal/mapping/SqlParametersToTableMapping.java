package org.nkjmlab.sorm4j.internal.mapping;

import static java.lang.System.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableSql;
import org.nkjmlab.sorm4j.internal.mapping.multirow.MultiRowProcessor;
import org.nkjmlab.sorm4j.internal.result.InsertResultImpl;
import org.nkjmlab.sorm4j.internal.result.TableMetaDataImpl;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.util.logger.LogPoint;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

/**
 * Holds mapping data from a given class and a table
 */
public final class SqlParametersToTableMapping<T> {

  private final Class<T> objectClass;
  private final ColumnValueToJavaObjectConverters columnValueConverter;
  private final ColumnToAccessorMapping columnToAccessorMap;
  private final PreparedStatementSupplier statementSupplier;
  private final SqlParametersSetter sqlParametersSetter;
  private final MultiRowProcessor<T> multiRowProcessor;

  private final TableMetaDataImpl tableMetaData;
  private final TableSql sql;
  private final LoggerContext loggerContext;

  public SqlParametersToTableMapping(LoggerContext loggerContext,
      ColumnValueToJavaObjectConverters columnValueConverter,
      SqlParametersSetter sqlParametersSetter, PreparedStatementSupplier statementSupplier,
      MultiRowProcessorFactory multiRowProcessorFactory, Class<T> objectClass,
      ColumnToAccessorMapping columnToAccessorMap, TableMetaDataImpl tableMetaData, TableSql sql) {
    this.columnValueConverter = columnValueConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = columnToAccessorMap;
    this.loggerContext = loggerContext;
    this.tableMetaData = tableMetaData;
    this.sql = sql;
    this.sqlParametersSetter = sqlParametersSetter;
    this.statementSupplier = statementSupplier;
    this.multiRowProcessor = multiRowProcessorFactory.getMultiRowProcessor(loggerContext,
        sqlParametersSetter, statementSupplier, objectClass, this);
  }

  public int[] batch(Connection con, String sql, Function<T, Object[]> parameterCreator,
      T[] objects) {
    return this.multiRowProcessor.batch(con, sql, parameterCreator, objects);
  }

  /**
   * For test
   *
   * @return
   */
  ColumnToAccessorMapping getColumnToAccessorMap() {
    return columnToAccessorMap;
  }

  public Object[] getDeleteParameters(T object) {
    return getParameters(object, tableMetaData.getPrimaryKeys());
  }

  public Object[] getInsertParameters(T object) {
    return getParametersWithoutAutoGeneratedColumns(object);
  }

  public Object[] getMergeParameters(T object) {
    return getParametersWithoutAutoGeneratedColumns(object);
  }

  public Object[] getParameters(Object object, List<String> columns) {
    if (object == null) {
      throw new SormException(ParameterizedStringUtils
          .newString("Fail to get value from a instance of [{}] but it is null.", objectClass));
    }
    final Object[] ret = new Object[columns.size()];

    for (int i = 0; i < ret.length; i++) {
      ret[i] = columnToAccessorMap.getValue(object, columns.get(i));
    }
    return ret;
  }

  public Object[] getParametersWithoutAutoGeneratedColumns(T object) {
    return getParameters(object, tableMetaData.getNotAutoGeneratedColumns());
  }

  public Object[] getPrimaryKeyParameters(T object) {
    return getParameters(object, tableMetaData.getPrimaryKeys());
  }

  public TableSql getSql() {
    return sql;
  }

  public TableMetaData getTableMetaData() {
    return tableMetaData;
  }

  public Object[] getUpdateParameters(T object) {
    throwExeptionIfPrimaryKeyIsNotExist();
    return getParameters(object, tableMetaData.getColumnsForUpdate());
  }

  public final int[] insert(Connection con, T[] objects) {
    return multiRowProcessor.multiRowInsert(con, objects);
  }

  public InsertResult<T> insertAndGet(Connection connection, T object) {
    String insertSql = sql.getInsertSql();

    try (PreparedStatement stmt = !tableMetaData.hasAutoGeneratedColumns()
        ? statementSupplier.prepareStatement(connection, insertSql)
        : statementSupplier.prepareStatement(connection, insertSql,
            tableMetaData.getAutoGeneratedColumnsArray())) {
      final Object[] parameters = getInsertParameters(object);
      sqlParametersSetter.setParameters(stmt, parameters);

      Optional<LogPoint> lp = loggerContext.createLogPoint(LoggerContext.Category.EXECUTE_UPDATE,
          SqlParametersToTableMapping.class);
      lp.ifPresent(_lp -> _lp.logBeforeSql(connection, insertSql, parameters));

      int rowsModified = stmt.executeUpdate();

      lp.ifPresent(_lp -> _lp.logAfterUpdate(rowsModified));

      List<Object> keys = setAutoGeneratedKeys(stmt, object);

      return new InsertResultImpl<T>(new int[] {rowsModified}, object, keys);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   *
   * @param con
   * @param objects not allow {@code null} or {@code 0} length
   * @return
   */
  public final InsertResult<T> insertAndGet(Connection con, T[] objects) {
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

  private List<Object> setAutoGeneratedKeys(PreparedStatement stmt, T object) throws SQLException {
    try (ResultSet resultSet = stmt.getGeneratedKeys()) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      List<Object> ret = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = metaData.getColumnName(1);
        int columnType = metaData.getColumnType(1);
        try {
          Class<?> classType = columnToAccessorMap.get(columnName).getSetterParameterType();
          final Object value = columnValueConverter.convertTo(resultSet, 1, columnType, classType);
          columnToAccessorMap.setValue(object, columnName, value);
          ret.add(value);
        } catch (Exception e) {
          throw Try.rethrow(new SormException(ParameterizedStringUtils.newString(
              "insert an instance of [{}] and get the result has error because [{}] has invalid setter.",
              object.getClass(), columnName), e));
        }
      }
      return ret;
    }
  }



  public int[] merge(Connection con, T[] objects) {
    int[] result = multiRowProcessor.multiRowMerge(con, objects);
    return result;
  }



  private void throwExeptionIfPrimaryKeyIsNotExist() {
    if (!tableMetaData.hasPrimaryKey()) {
      throw new SormException("This opperation requiers primary keys but Table ["
          + tableMetaData.getTableName() + "] doesn't have them.");
    }
  }

  @Override
  public String toString() {
    return "[" + objectClass.getName() + "] class is mapped to [" + tableMetaData.getTableName()
        + "] Table. [" + objectClass.getName() + "] could set/get values for following columns."
        + lineSeparator() + columnToAccessorMap.toString() + lineSeparator() + "  with ["
        + multiRowProcessor.getClass().getSimpleName() + "]";
  }



}
