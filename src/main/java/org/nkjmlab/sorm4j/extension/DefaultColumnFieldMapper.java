
package org.nkjmlab.sorm4j.extension;

import static org.nkjmlab.sorm4j.internal.util.StringUtils.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.annotation.OrmColumn;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.internal.util.LoggerFactory;
import org.nkjmlab.sorm4j.internal.util.SqlTypeUtils;
import org.nkjmlab.sorm4j.internal.util.StringUtils;

/**
 * Default implementation of {@link ColumnFieldMapper}
 *
 * @author nkjm
 *
 */

public class DefaultColumnFieldMapper implements ColumnFieldMapper {

  private static Map<FieldName, Method> extractedMethodStartWith(Class<?> objectClass,
      String prefix) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.isNull(m.getAnnotation(ignoreAnn))
            && !Modifier.isStatic(m.getModifiers()) && m.getName().length() > prefix.length()
            && m.getName().substring(0, prefix.length()).equals(prefix))
        .collect(Collectors.toMap(m -> new FieldName(
            m.getName().substring(prefix.length(), prefix.length() + 1).toLowerCase()
                + m.getName().substring(prefix.length() + 1)),
            m -> m));
  }


  private static Map<FieldName, Field> getAllFields(final Class<?> objectClass) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getDeclaredFields())
        .filter(
            f -> Objects.isNull(f.getAnnotation(ignoreAnn)) && !Modifier.isStatic(f.getModifiers()))
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
    Class<OrmColumn> ann = OrmColumn.class;
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

  private static Method isValidGetter(Method getter) {
    if (getter == null) {
      return null;
    }
    if (getter.getParameterCount() != 0) {
      LoggerFactory.warn(DefaultResultSetConverter.class,
          "Getter [{}] should not have parameter but has {} params.", getter,
          getter.getParameterCount());
      return null;
    }
    if (getter.getReturnType() == void.class) {
      LoggerFactory.warn(DefaultResultSetConverter.class,
          "Getter [{}] must have return a parameter.", getter);
    }

    return getter;
  }

  private static Method isValidSetter(Method setter) {
    if (setter == null) {
      return null;
    }
    if (setter.getParameterCount() != 1) {
      LoggerFactory.warn(DefaultResultSetConverter.class,
          "Setter [{}] should have a single parameter but has {} params.", setter,
          setter.getParameterCount());
      return null;
    }
    return setter;
  }

  @Override
  public Map<String, Accessor> createAccessors(Class<?> objectClass) {
    Set<FieldName> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());

    List<Column> columns = new ArrayList<>(names).stream()
        .flatMap(fieldName -> guessColumnNameCandidates(fieldName).stream())
        .collect(Collectors.toList());
    columns.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    columns.addAll(getAnnotatedGettersMap(objectClass).keySet());
    columns.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    return createAccessors(objectClass, columns);
  }

  @Override
  public Map<String, Accessor> createAccessors(Class<?> objectClass, List<Column> columns) {
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

      Optional<FieldName> op =
          fieldsList.stream().filter(fieldName -> isMatch(column, fieldName)).findFirst();
      if (op.isPresent()) {
        FieldName fieldName = op.get();
        f = f != null ? f : fields.get(fieldName);
        g = g != null ? g : isValidGetter(getters.get(fieldName));
        s = s != null ? g : isValidSetter(setters.get(fieldName));
      }
      if (f == null && (g == null || s == null)) {
        LoggerFactory.debug(getClass(),
            "Skip matching with Column [{}] to field because could not found corresponding field.",
            column);
      } else {
        ret.put(StringUtils.toCanonical(column.getName()), new Accessor(column, f, g, s));
      }
    }
    return ret;
  }


  /**
   * Gets field name corresponding to the column name. If the set of column name candidates guessed
   * from a field contains the given column name, the field is mapped to the column. Capital case is
   * ignored for the mapping.
   *
   * @param column
   * @param fieldName
   * @return
   */
  protected boolean isMatch(Column column, FieldName fieldName) {
    final List<String> candidates = guessColumnNameCandidates(fieldName).stream()
        .map(Column::getName).collect(Collectors.toList());
    return containsAsCanonical(candidates, column.getName());
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

    // Column name and data type for message.
    final class ColumnOnTable extends Column {

      private int dataType;

      public ColumnOnTable(String name, int dataType) {
        super(name);
        this.dataType = dataType;
      }

      @Override
      public String toString() {
        return getName() + "(" + SqlTypeUtils.sqlTypeToString(dataType) + ")";
      }
    }

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
  public List<Column> getPrimaryKeys(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    final List<Column> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(new Column(columnName));
      }
      return primaryKeysList;
    }
  }

  /**
   * Gets schema pattern for accessing {@link DatabaseMetaData}.
   *
   * @param metaData
   * @return
   * @throws SQLException
   */
  protected String getSchemaPattern(DatabaseMetaData metaData) throws SQLException {
    // oracle expects a pattern such as "%" to work
    return "Oracle".equalsIgnoreCase(metaData.getDatabaseProductName()) ? "%" : null;
  }

  /**
   * Guesses candidates of column name from the given field name.
   *
   * @param fieldName
   * @return
   */

  protected List<Column> guessColumnNameCandidates(FieldName fieldName) {
    final String _fieldName = fieldName.getName();
    return Stream.of(toCanonical(_fieldName)).map(Column::new).collect(Collectors.toList());
  }



}
