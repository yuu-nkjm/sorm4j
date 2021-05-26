
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
import org.nkjmlab.sorm4j.internal.extension.LoggerFactory;
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

  private static Map<ColumnName, Method> getAnnotatatedSettersMap(Class<?> objectClass) {
    Class<OrmSetter> ann = OrmSetter.class;
    Map<ColumnName, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new ColumnName(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }

  private static Map<ColumnName, Field> getAnnotatedFieldsMap(Class<?> objectClass) {
    Class<OrmColumn> ann = OrmColumn.class;
    return Arrays.stream(objectClass.getDeclaredFields())
        .filter(f -> Objects.nonNull(f.getAnnotation(ann)))
        .collect(Collectors.toMap(f -> new ColumnName(f.getAnnotation(ann).value()), f -> {
          f.setAccessible(true);
          return f;
        }));
  }


  private static Map<ColumnName, Method> getAnnotatedGettersMap(Class<?> objectClass) {
    Class<OrmGetter> ann = OrmGetter.class;
    Map<ColumnName, Method> annos = Arrays.stream(objectClass.getDeclaredMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)))
        .collect(Collectors.toMap(m -> new ColumnName(m.getAnnotation(ann).value()), m -> m));
    return annos;
  }

  private static Method isValidGetter(Method getter) {
    if (getter == null) {
      return null;
    }
    if (getter.getParameterCount() != 0) {
      LoggerFactory.getLogger().warn("Getter [{}] should not have parameter but has {} params.",
          getter, getter.getParameterCount());
      return null;
    }
    if (getter.getReturnType() == void.class) {
      LoggerFactory.getLogger().warn("Getter [{}] must have return a parameter.", getter);
    }

    return getter;
  }

  private static Method isValidSetter(Method setter) {
    if (setter == null) {
      return null;
    }
    if (setter.getParameterCount() != 1) {
      LoggerFactory.getLogger().warn(
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

    List<ColumnName> columnNames = new ArrayList<>(names).stream()
        .flatMap(fieldName -> guessColumnNameCandidates(fieldName).stream())
        .collect(Collectors.toList());
    columnNames.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    columnNames.addAll(getAnnotatedGettersMap(objectClass).keySet());
    columnNames.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    return createAccessors(objectClass, columnNames);
  }

  @Override
  public Map<String, Accessor> createAccessors(Class<?> objectClass, List<ColumnName> columnNames) {
    Map<FieldName, Field> fields = getAllFields(objectClass);
    Map<FieldName, Method> getters = getAllGetters(objectClass);
    Map<FieldName, Method> setters = getAllSetters(objectClass);
    Map<ColumnName, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<ColumnName, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<ColumnName, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    List<FieldName> fieldsList = new ArrayList<>(fields.keySet());
    Map<String, Accessor> ret = new HashMap<>();
    for (ColumnName columnName : columnNames) {
      Field f = annotatedFields.get(columnName);
      Method g = isValidGetter(annotatedGetters.get(columnName));
      Method s = isValidSetter(annotatedSetters.get(columnName));

      Optional<FieldName> op =
          fieldsList.stream().filter(fieldName -> isMatch(columnName, fieldName)).findFirst();
      if (op.isPresent()) {
        FieldName fieldName = op.get();
        f = f != null ? f : fields.get(fieldName);
        g = g != null ? g : isValidGetter(getters.get(fieldName));
        s = s != null ? g : isValidSetter(setters.get(fieldName));
      }
      if (f == null && (g == null || s == null)) {
        LoggerFactory.getLogger().debug(
            "Skip matching with ColumnName [{}] to field because could not found corresponding field.",
            columnName);
      } else {
        ret.put(StringUtils.toCanonical(columnName.getName()), new Accessor(columnName, f, g, s));
      }
    }
    return ret;
  }


  /**
   * Gets field name corresponding to the column name. If the set of column name candidates guessed
   * from a field contains the given column name, the field is mapped to the column. Capital case is
   * ignored for the mapping.
   *
   * @param columnName
   * @param fieldName
   * @return
   */
  protected boolean isMatch(ColumnName columnName, FieldName fieldName) {
    final List<String> candidates = guessColumnNameCandidates(fieldName).stream()
        .map(ColumnName::getName).collect(Collectors.toList());
    return containsAsCanonical(candidates, columnName.getName());
  }


  @Override
  public List<ColumnName> getAutoGeneratedColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<ColumnName> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        String isAutoIncrement = resultSet.getString(23);
        if (isAutoIncrement.equals("YES")) {
          columnsList.add(new ColumnName(columnName));
        }
      }
      return columnsList;
    }
  }

  @Override
  public List<ColumnName> getColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {

    // ColumnName name and data type for message.
    final class ColumnNameWithMetaData extends ColumnName {

      private final String msg;

      public ColumnNameWithMetaData(String name, int dataType, String typeName, int ordinalPosition,
          String isNullable, String isAutoIncremented, String isGenerated) {
        super(name);
        this.msg =
            StringUtils.format("{}. {} [{}({})] [{},{},{}]", String.format("%02d", ordinalPosition),
                name, typeName, dataType, isNullable, isAutoIncremented, isGenerated);
      }

      @Override
      public String toString() {
        return msg;
      }
    }

    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<ColumnName> columnsList = new ArrayList<>();
      while (resultSet.next()) {
        String columnName = resultSet.getString(4);
        int dataType = resultSet.getInt(5);
        String typeName = resultSet.getString(6);
        int ordinalPosition = resultSet.getInt(17);
        String isNullable = resultSet.getString(18);
        String isAutoIncremented = resultSet.getString(23);
        String isGenerated = resultSet.getString(24);

        columnsList.add(new ColumnNameWithMetaData(columnName, dataType, typeName, ordinalPosition,
            isNullable, isAutoIncremented, isGenerated));
      }
      return columnsList;
    }
  }



  @Override
  public List<ColumnName> getPrimaryKeys(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    final List<ColumnName> primaryKeysList = new ArrayList<>();
    try (ResultSet resultSet =
        metaData.getPrimaryKeys(null, getSchemaPattern(metaData), tableName)) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString(4);
        primaryKeysList.add(new ColumnName(columnName));
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

  protected List<ColumnName> guessColumnNameCandidates(FieldName fieldName) {
    final String _fieldName = fieldName.getName();
    return Stream.of(toCanonical(_fieldName)).map(ColumnName::new).collect(Collectors.toList());
  }



}
