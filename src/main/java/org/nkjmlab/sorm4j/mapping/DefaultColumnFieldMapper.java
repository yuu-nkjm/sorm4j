
package org.nkjmlab.sorm4j.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.annotation.OrmColum;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

/**
 * Defines how a class or field name should be mapped to a table or column.
 */
public final class DefaultColumnFieldMapper implements ColumnFieldMapper {

  @Override
  public List<Column> guessColumnNameCandidates(FieldName fieldName) {
    return List.of(new Column(StringUtils.toUpperSnakeCase(fieldName.getName())));
  }

  @Override
  public Map<Column, Field> getAnnotatedFieldsMap(Class<?> objectClass) {
    Class<OrmColum> ann = OrmColum.class;
    return Arrays.stream(objectClass.getDeclaredFields())
        .filter(f -> Objects.nonNull(f.getAnnotation(ann)))
        .collect(Collectors.toMap(f -> new Column(f.getAnnotation(ann).value()), f -> {
          f.setAccessible(true);
          return f;
        }));
  }


  @Override
  public Map<Column, Method> getAnnotatatedSettersMap(Class<?> objectClass) {
    Class<OrmSetter> ann = OrmSetter.class;
    Map<Column, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new Column(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }

  @Override
  public Map<Column, Method> getAnnotatedGettersMap(Class<?> objectClass) {
    Class<OrmGetter> ann = OrmGetter.class;
    Map<Column, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new Column(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }


  @Override
  public Optional<FieldName> getFieldNameByColumnName(Column column, List<FieldName> fieldNames) {
    for (FieldName fieldName : fieldNames) {
      if (StringUtils.containsIgnoreCase(guessColumnNameCandidates(fieldName).stream()
          .map(s -> s.toString()).collect(Collectors.toList()), column.getName())) {
        return Optional.of(fieldName);
      }
    }
    return Optional.empty();
  }


  @Override
  public List<Column> getColumnNameCandidates(List<FieldName> fieldNames) {
    return fieldNames.stream().flatMap(fieldName -> guessColumnNameCandidates(fieldName).stream())
        .collect(Collectors.toList());
  }


  @Override
  public List<Column> getAutoGeneratedColumns(DatabaseMetaData metaData, String tableName) {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<Column> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        String isAutoIncrement = resultSet.getString(23);
        if (isAutoIncrement.equals("YES")) {
          columnsList.add(new Column(columnName));
        }
      }
      return columnsList;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }


  @Override
  public List<Column> getColumns(DatabaseMetaData metaData, String tableName) {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<Column> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        int dataType = resultSet.getInt(5);
        columnsList.add(new ColumnOnTable(columnName, dataType));
      }
      return columnsList;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  @Override
  public List<String> getPrimaryKeys(DatabaseMetaData metaData, String tableName) {
    final List<String> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(columnName);
      }
      return primaryKeysList;
    } catch (SQLException e) {
      throw new OrmException(e);
    }
  }

  private String getSchemaPattern(DatabaseMetaData metaData) throws SQLException {
    if ("Oracle".equalsIgnoreCase(metaData.getDatabaseProductName())) {
      return "%"; // oracle expects a pattern such as "%" to work
    }
    return null;
  }
}