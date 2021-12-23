
package org.nkjmlab.sorm4j.extension.impl;

import static java.util.Objects.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.ColumnName;
import org.nkjmlab.sorm4j.extension.ColumnNameWithMetaData;
import org.nkjmlab.sorm4j.extension.FieldName;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

/**
 * Default implementation of {@link ColumnFieldMapper}
 *
 * @author nkjm
 *
 */

public class DefaultColumnFieldMapper implements ColumnFieldMapper {

  private final LoggerContext loggerContext;

  public DefaultColumnFieldMapper() {
    this(LoggerContext.builder().build());
  }

  public DefaultColumnFieldMapper(LoggerContext loggerContext) {
    this.loggerContext = loggerContext;
  }

  private Map<String, Method> extractedMethodStartWith(Class<?> objectClass, String prefix) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getMethods())
        .filter(m -> Objects.isNull(m.getAnnotation(ignoreAnn))
            && !Modifier.isStatic(m.getModifiers()) && m.getName().length() > prefix.length()
            && m.getName().substring(0, prefix.length()).equals(prefix))
        .collect(Collectors.toMap(m -> toCanonicalCase(m.getName().substring(prefix.length())),
            m -> m, (v1, v2) -> v2));
  }


  private static Map<String, Field> getAllFields(final Class<?> objectClass) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getFields())
        .filter(
            f -> Objects.isNull(f.getAnnotation(ignoreAnn)) && !Modifier.isStatic(f.getModifiers()))
        .collect(Collectors.toMap(f -> toCanonicalCase(f.getName()), f -> {
          // f.setAccessible(true);
          return f;
        }));
  }


  private Map<String, Method> getAllGetters(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "get").entrySet().stream()
        .filter(e -> nonNull(isValidGetter(e.getValue())))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private Map<String, Method> getAllMethods(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "").entrySet().stream()
        .filter(e -> nonNull(isValidGetter(e.getValue(), false)))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private Map<String, Method> getAllSetters(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "set").entrySet().stream()
        .filter(e -> nonNull(isValidSetter(e.getValue())))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private Map<String, Method> getAnnotatatedSettersMap(Class<?> objectClass) {
    Class<OrmSetter> ann = OrmSetter.class;
    return Arrays.stream(objectClass.getMethods())
        .filter(m -> nonNull(m.getAnnotation(ann)) && nonNull(isValidSetter(m)))
        .collect(Collectors.toMap(m -> toCanonicalCase(m.getAnnotation(ann).value()), m -> m));
  }

  private Map<String, Field> getAnnotatedFieldsMap(Class<?> objectClass) {
    Class<OrmColumn> ann = OrmColumn.class;
    return Arrays.stream(objectClass.getFields()).filter(f -> Objects.nonNull(f.getAnnotation(ann)))
        .collect(Collectors.toMap(f -> toCanonicalCase(f.getAnnotation(ann).value()), f -> {
          // f.setAccessible(true);
          return f;
        }));
  }


  private Map<String, Method> getAnnotatedGettersMap(Class<?> objectClass) {
    Class<OrmGetter> ann = OrmGetter.class;
    return Arrays.stream(objectClass.getMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)) && nonNull(isValidGetter(m)))
        .collect(Collectors.toMap(m -> toCanonicalCase(m.getAnnotation(ann).value()), m -> m));
  }

  private Method isValidGetter(Method getter) {
    return isValidGetter(getter, true);
  }

  private Method isValidGetter(Method getter, boolean logging) {
    if (getter == null) {
      return null;
    }

    if (getter.getName().equals("getClass")) {
      return null;
    }
    if (getter.getParameterCount() != 0) {
      if (logging) {
        loggerContext.getLogger().warn("Getter [{}] should not have parameter but has {} params.",
            getter, getter.getParameterCount());
      }
      return null;
    }
    if (getter.getReturnType() == void.class) {
      if (logging) {
        loggerContext.getLogger().warn("Getter [{}] must have return a parameter.", getter);
      }
    }
    return getter;
  }

  private Method isValidSetter(Method setter) {
    if (setter == null) {
      return null;
    }
    if (setter.getParameterCount() != 1) {
      loggerContext.getLogger().warn(
          "Setter [{}] should have a single parameter but has {} params.", setter,
          setter.getParameterCount());
      return null;
    }
    return setter;
  }

  @Override
  public Map<String, Accessor> createAccessors(Class<?> objectClass) {
    Set<String> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());
    names.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    names.addAll(getAnnotatedGettersMap(objectClass).keySet());
    names.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    return createAccessors(objectClass, new ArrayList<>(names));
  }

  @Override
  public Map<String, Accessor> createAccessors(Class<?> objectClass, List<String> columnNames) {
    Map<String, Field> fields = getAllFields(objectClass);
    Map<String, Method> getters = getAllGetters(objectClass);
    Map<String, Method> allMethods = getAllMethods(objectClass);
    Map<String, Method> setters = getAllSetters(objectClass);
    Map<String, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<String, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<String, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    Map<String, Accessor> ret = new HashMap<>();
    for (String canonicalColName : columnNames.stream().map(col -> toCanonicalCase(col))
        .toArray(String[]::new)) {
      Field f =
          nonNull(annotatedFields.get(canonicalColName)) ? annotatedFields.get(canonicalColName)
              : fields.get(canonicalColName);
      Method g =
          nonNull(annotatedGetters.get(canonicalColName)) ? annotatedGetters.get(canonicalColName)
              : nonNull(getters.get(canonicalColName)) ? getters.get(canonicalColName)
                  : allMethods.get(canonicalColName);
      Method s =
          nonNull(annotatedSetters.get(canonicalColName)) ? annotatedSetters.get(canonicalColName)
              : setters.get(canonicalColName);

      if (f == null && (g == null && s == null)) {
        loggerContext.getLogger().debug(
            "Skip matching with ColumnName [{}] to field because could not found corresponding field.",
            canonicalColName);
      } else {
        ret.put(canonicalColName, new Accessor(canonicalColName, f, g, s));
      }
    }
    return ret;
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
  public List<ColumnNameWithMetaData> getColumns(DatabaseMetaData metaData, String tableName)
      throws SQLException {
    try (ResultSet resultSet =
        metaData.getColumns(null, getSchemaPattern(metaData), tableName, "%")) {
      final List<ColumnNameWithMetaData> columnsList = new ArrayList<>();
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
    return Stream.of(toCanonicalCase(_fieldName)).map(ColumnName::new).collect(Collectors.toList());
  }

  @Override
  public String getColumnAliasPrefix(Class<?> objectClass) {
    return Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
        .map(a -> a.value()).orElse("");
  }

  @Override
  public Map<String, Accessor> createAliasAccessors(String prefix,
      Map<String, Accessor> accessors) {
    if (prefix.length() == 0) {
      return Collections.emptyMap();
    }

    Map<String, Accessor> ret = new HashMap<>();

    for (String key : accessors.keySet()) {
      String aKey = toCanonicalCase(prefix + key);
      if (accessors.containsKey(aKey)) {
        throw new SormException(ParameterizedStringUtils.newString(
            "Modify table alias because table alias [{}] and column [{}] is concatenated and it becomes duplicated column",
            prefix, key));
      }
      ret.put(aKey, accessors.get(key));
    }
    return ret;
  }



}
