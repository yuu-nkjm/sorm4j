package org.nkjmlab.sorm4j.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.ResultSetConverter;
import org.nkjmlab.sorm4j.config.ColumnFieldMapper;
import org.nkjmlab.sorm4j.util.StringUtils;

abstract class Mapping<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  protected final ResultSetConverter sqlToJavaConverter;
  protected final Class<T> objectClass;
  protected final ColumnToAccessorMap columnToAccessorMap;

  public Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass,
      ColumnFieldMapper nameGuesser) {
    this(sqlToJavaConverter, objectClass, guessColumnNames(objectClass, nameGuesser), nameGuesser);
  }



  private static List<Column> guessColumnNames(Class<?> objectClass,
      ColumnFieldMapper nameGuesser) {
    Set<FieldName> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());

    List<Column> columns = nameGuesser.getColumnNameCandidates(new ArrayList<>(names));
    columns.addAll(nameGuesser.getAnnotatedFieldsMap(objectClass).keySet());
    columns.addAll(nameGuesser.getAnnotatedGettersMap(objectClass).keySet());
    columns.addAll(nameGuesser.getAnnotatatedSettersMap(objectClass).keySet());
    return columns;
  }



  public Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass, List<Column> columns,
      ColumnFieldMapper nameGuesser) {
    this.sqlToJavaConverter = sqlToJavaConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = new ColumnToAccessorMap(createAccessors(columns, nameGuesser));
  }



  protected Map<String, Accessor> createAccessors(List<Column> columns,
      ColumnFieldMapper nameGuesser) {
    Map<FieldName, Field> fields = getAllFields(objectClass);
    Map<FieldName, Method> getters = getAllGetters(objectClass);
    Map<FieldName, Method> setters = getAllSetters(objectClass);
    Map<Column, Field> annotatedFields = nameGuesser.getAnnotatedFieldsMap(objectClass);
    Map<Column, Method> annotatedGetters = nameGuesser.getAnnotatedGettersMap(objectClass);
    Map<Column, Method> annotatedSetters = nameGuesser.getAnnotatatedSettersMap(objectClass);

    List<FieldName> fieldsList = new ArrayList<>(fields.keySet());
    Map<String, Accessor> ret = new HashMap<>();
    for (Column column : columns) {
      Field f = annotatedFields.get(column);
      Method g = isValidGetter(annotatedGetters.get(column));
      Method s = isValidSetter(annotatedSetters.get(column));

      Optional<FieldName> op = nameGuesser.getFieldNameByColumnName(column, fieldsList);
      if (op.isPresent()) {
        FieldName fieldName = op.get();
        f = f != null ? f : fields.get(fieldName);
        g = g != null ? g : isValidGetter(getters.get(fieldName));
        s = s != null ? g : isValidSetter(setters.get(fieldName));
      }
      if (f == null && (g == null || s == null)) {
        log.warn(
            "Skip matching with Column [{}] to field because could not found corresponding field.",
            column);
      } else {
        ret.put(column.getName(), new Accessor(column, f, g, s));
      }


    }
    return ret;
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



  public static Map<FieldName, Field> getAllFields(final Class<?> objectClass) {
    return Arrays.stream(objectClass.getDeclaredFields())
        .collect(Collectors.toMap(f -> new FieldName(f), f -> {
          f.setAccessible(true);
          return f;
        }));
  }


  private static Map<FieldName, Method> getAllSetters(Class<?> objectClass) {
    Map<FieldName, Method> setters = extractedMethodStartWith(objectClass, "set");
    return setters;
  }


  private static Map<FieldName, Method> getAllGetters(Class<?> objectClass) {
    Map<FieldName, Method> getters = extractedMethodStartWith(objectClass, "get");
    return getters;
  }


  private static Map<FieldName, Method> extractedMethodStartWith(Class<?> objectClass,
      String prefix) {
    return Arrays.stream(objectClass.getDeclaredMethods())
        .filter(
            m -> m.getName().substring(0, prefix.length()).equals(prefix))
        .collect(Collectors.toMap(m -> new FieldName(
            m.getName().substring(prefix.length(), prefix.length() + 1).toLowerCase()
                + m.getName().substring(prefix.length() + 1)),
            m -> m));
  }

  final Object getValue(Object object, String columnName) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc != null) {
      return acc.get(object);
    } else {
      throw new OrmException(StringUtils.format(
          "Error getting value from [{}] because column [{}] does not have a corresponding setter method or field",
          object.getClass(), columnName));
    }
  }


  final void setValue(Object object, String columnName, Object value) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc != null) {
      acc.set(object, value);
    } else {
      throw new OrmException(StringUtils.format(
          "Error setting value [{}]" + " of type [{}] in [{}]"
              + " because column [{}] does not have a corresponding setter method or field =>[{}]",
          value, value.getClass().getName(), object.getClass().getName(), columnName,
          columnToAccessorMap.keySet()));
    }
  }

  @Override
  public String toString() {
    return "Mapping [objectClass=" + objectClass.getName() + ", columnToAccessorMap="
        + columnToAccessorMap.values() + "]";
  }

  protected String getColumnToAccessorString() {
    List<String> columnStrs =
        columnToAccessorMap.keySet().stream().sorted().collect(Collectors.toList());
    return "COLUMNS " + columnStrs + " is mapped to [" + objectClass.getName() + "]"
        + System.lineSeparator()
        + String.join(System.lineSeparator(),
            columnStrs.stream()
                .map(e -> "  COLUM " + e + " => " + columnToAccessorMap.get(e).getFormattedString())
                .collect(Collectors.toList()));
  }


}
