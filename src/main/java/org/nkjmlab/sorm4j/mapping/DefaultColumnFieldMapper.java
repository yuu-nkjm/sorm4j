
package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.OrmColum;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

/**
 * Defines how a class or field name should be mapped to a table or column.
 */
public final class DefaultColumnFieldMapper implements ColumnFieldMapper {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private static Map<String, Accessor> createAccessors(List<Column> columns, Class<?> objectClass) {
    Map<FieldName, Field> fields = getAllFields(objectClass);
    Map<FieldName, Method> getters = getAllGetters(objectClass);
    Map<FieldName, Method> setters = getAllSetters(objectClass);
    Map<Column, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<Column, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<Column, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    List<FieldName> fieldsList = new ArrayList<>(fields.keySet());
    Map<String, Accessor> ret = new HashMap<>();
    for (Column column : columns) {
      Field f = annotatedFields.get(column);
      Method g = isValidGetter(annotatedGetters.get(column));
      Method s = isValidSetter(annotatedSetters.get(column));

      Optional<FieldName> op = getFieldNameByColumnName(column, fieldsList);
      if (op.isPresent()) {
        FieldName fieldName = op.get();
        f = f != null ? f : fields.get(fieldName);
        g = g != null ? g : isValidGetter(getters.get(fieldName));
        s = s != null ? g : isValidSetter(setters.get(fieldName));
      }
      if (f == null && (g == null || s == null)) {
        log.debug(
            "Skip matching with Column [{}] to field because could not found corresponding field.",
            column);
      } else {
        ret.put(column.getName(), new Accessor(column, f, g, s));
      }
    }
    return ret;
  }

  private static Map<FieldName, Method> extractedMethodStartWith(Class<?> objectClass,
      String prefix) {
    return Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> m.getName().length() > prefix.length()
            && m.getName().substring(0, prefix.length()).equals(prefix))
        .collect(Collectors.toMap(m -> new FieldName(
            m.getName().substring(prefix.length(), prefix.length() + 1).toLowerCase()
                + m.getName().substring(prefix.length() + 1)),
            m -> m));
  }


  private static Map<FieldName, Field> getAllFields(final Class<?> objectClass) {
    return Arrays.stream(objectClass.getDeclaredFields())
        .collect(Collectors.toMap(f -> new FieldName(f), f -> {
          f.setAccessible(true);
          return f;
        }));
  }

  private static Map<FieldName, Method> getAllGetters(Class<?> objectClass) {
    Map<FieldName, Method> getters = extractedMethodStartWith(objectClass, "get");
    return getters;
  }


  private static Map<FieldName, Method> getAllSetters(Class<?> objectClass) {
    Map<FieldName, Method> setters = extractedMethodStartWith(objectClass, "set");
    return setters;
  }


  private static Map<Column, Method> getAnnotatatedSettersMap(Class<?> objectClass) {
    Class<OrmSetter> ann = OrmSetter.class;
    Map<Column, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new Column(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }


  private static Map<Column, Field> getAnnotatedFieldsMap(Class<?> objectClass) {
    Class<OrmColum> ann = OrmColum.class;
    return Arrays.stream(objectClass.getDeclaredFields())
        .filter(f -> Objects.nonNull(f.getAnnotation(ann)))
        .collect(Collectors.toMap(f -> new Column(f.getAnnotation(ann).value()), f -> {
          f.setAccessible(true);
          return f;
        }));
  }


  private static Map<Column, Method> getAnnotatedGettersMap(Class<?> objectClass) {
    Class<OrmGetter> ann = OrmGetter.class;
    Map<Column, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new Column(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }

  /**
   * Get column name candidates based the field names.
   *
   * @param fieldNames
   * @return
   */
  private static List<Column> getColumnNameCandidates(List<FieldName> fieldNames) {
    return fieldNames.stream().flatMap(fieldName -> guessColumnNameCandidates(fieldName).stream())
        .collect(Collectors.toList());
  }

  /**
   * Get field name corresponding to the column name.
   *
   * @param column column name
   * @param fieldNames fieldNames exists in mapped object.
   * @return
   */
  private static Optional<FieldName> getFieldNameByColumnName(Column column,
      List<FieldName> fieldNames) {
    for (FieldName fieldName : fieldNames) {
      if (StringUtils.containsIgnoreCase(guessColumnNameCandidates(fieldName).stream()
          .map(s -> s.toString()).collect(Collectors.toList()), column.getName())) {
        return Optional.of(fieldName);
      }
    }
    return Optional.empty();
  }

  private static String getSchemaPattern(DatabaseMetaData metaData) throws SQLException {
    // oracle expects a pattern such as "%" to work
    return "Oracle".equalsIgnoreCase(metaData.getDatabaseProductName()) ? "%" : null;
  }

  private static List<Column> guessColumnNameCandidates(FieldName fieldName) {
    return List.of(new Column(toUpperSnakeCase(fieldName.getName())),
        new Column(toUpperCase(fieldName.getName())));
  }

  private static Method isValidGetter(Method getter) {
    if (getter == null) {
      return null;
    }
    if (getter.getParameterCount() != 0) {
      log.warn("Getter [{}] should not have parameter but has {} params.", getter,
          getter.getParameterCount());
      return null;
    }
    if (getter.getReturnType() == void.class) {
      log.warn("Getter [{}] must have return a parameter.");
    }

    return getter;
  }


  private static Method isValidSetter(Method setter) {
    if (setter == null) {
      return null;
    }
    if (setter.getParameterCount() != 1) {
      log.warn("Setter [{}] should have a single parameter but has {} params.", setter,
          setter.getParameterCount());
      return null;
    }
    return setter;
  }



  @Override
  public ColumnToAccessorMap createColumnToAccessorMap(Class<?> objectClass) {
    return new ColumnToAccessorMap(createAccessors(guessColumnNames(objectClass), objectClass));
  }



  @Override
  public ColumnToAccessorMap createColumnToAccessorMap(Class<?> objectClass, List<Column> columns) {
    return new ColumnToAccessorMap(createAccessors(columns, objectClass));
  }



  @Override
  public List<Column> getAutoGeneratedColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
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
    }
  }


  @Override
  public List<Column> getColumns(DatabaseMetaData metaData, String tableName) throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<Column> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        int dataType = resultSet.getInt(5);
        columnsList.add(new ColumnOnTable(columnName, dataType));
      }
      return columnsList;
    }
  }


  @Override
  public List<String> getPrimaryKeys(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    final List<String> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(columnName);
      }
      return primaryKeysList;
    }
  }

  /**
   * Guess cloumn names from the object class
   *
   * @param objectClass
   * @return
   */
  public List<Column> guessColumnNames(Class<?> objectClass) {
    Set<FieldName> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());

    List<Column> columns = getColumnNameCandidates(new ArrayList<>(names));
    columns.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    columns.addAll(getAnnotatedGettersMap(objectClass).keySet());
    columns.addAll(getAnnotatatedSettersMap(objectClass).keySet());
    return columns;
  }


}
