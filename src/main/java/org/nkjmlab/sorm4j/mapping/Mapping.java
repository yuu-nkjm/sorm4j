package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.StringUtils.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.annotation.OrmColum;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.mapping.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.mapping.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.util.StringUtils;

abstract class Mapping<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  protected final Class<T> objectClass;
  protected final ResultSetConverter defaultResultSetConverter;
  protected final ColumnToAccessorMap columnToAccessorMap;

  Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass,
      ColumnFieldMapper columnFieldMapper) {
    this.defaultResultSetConverter = sqlToJavaConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = createColumnToAccessorMap(columnFieldMapper, objectClass);
  }


  Mapping(ResultSetConverter sqlToJavaConverter, Class<T> objectClass, List<Column> columns,
      ColumnFieldMapper columnFieldMapper) {
    this.defaultResultSetConverter = sqlToJavaConverter;
    this.objectClass = objectClass;
    this.columnToAccessorMap = createColumnToAccessorMap(columnFieldMapper, objectClass, columns);
  }


  final Object getValue(Object object, String columnName) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new OrmException(format(
          "Error getting value from [{}] because column [{}] does not have a corresponding getter method or field access",
          object.getClass(), columnName));
    }
    try {
      return acc.get(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new OrmException(format(
          "Could not get a value from instance of [{}] for column [{}] with [{}] The instance is =[{}]",
          (object == null ? "null" : object.getClass().getName()), columnName,
          acc.getFormattedString(), object), e);
    }
  }


  final void setValue(Object object, String columnName, Object value) {
    final Accessor acc = columnToAccessorMap.get(columnName);
    if (acc == null) {
      throw new OrmException(StringUtils.format("Error setting value [{}]" + " of type [{}] in [{}]"
          + " because column [{}] does not have a corresponding setter method or field access =>[{}]",
          value, value.getClass().getSimpleName(), object.getClass().getName(), columnName,
          columnToAccessorMap.keySet()));
    }
    try {
      acc.set(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new OrmException(format(
          "Could not set a value for column [{}] to instance of [{}] with [{}]. The value is=[{}]",
          columnName, object == null ? "null" : object.getClass().getSimpleName(),
          acc.getFormattedString(), value), e);

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

  private static Map<String, Accessor> createAccessors(ColumnFieldMapper columnFieldMapper,
      List<Column> columns, Class<?> objectClass) {
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

      Optional<FieldName> op = columnFieldMapper.getFieldNameByColumnName(column, fieldsList);
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



  private static ColumnToAccessorMap createColumnToAccessorMap(ColumnFieldMapper columnFieldMapper,
      Class<?> objectClass) {
    return createColumnToAccessorMap(columnFieldMapper, objectClass,
        guessColumnNames(columnFieldMapper, objectClass));
  }



  private static ColumnToAccessorMap createColumnToAccessorMap(ColumnFieldMapper columnFieldMapper,
      Class<?> objectClass, List<Column> columns) {
    return new ColumnToAccessorMap(createAccessors(columnFieldMapper, columns, objectClass));
  }

  /**
   * Guess cloumn names from the object class
   *
   * @param objectClass
   * @return
   */
  private static List<Column> guessColumnNames(ColumnFieldMapper columnFieldMapper,
      Class<?> objectClass) {
    Set<FieldName> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());

    List<Column> columns = columnFieldMapper.getColumnNameCandidates(new ArrayList<>(names));
    columns.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    columns.addAll(getAnnotatedGettersMap(objectClass).keySet());
    columns.addAll(getAnnotatatedSettersMap(objectClass).keySet());
    return columns;
  }


}
