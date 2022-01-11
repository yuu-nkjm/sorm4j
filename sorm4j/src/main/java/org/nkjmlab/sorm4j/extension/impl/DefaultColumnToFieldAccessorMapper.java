
package org.nkjmlab.sorm4j.extension.impl;

import static java.util.Objects.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.OrmColumn;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.extension.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.extension.FieldAccessor;
import org.nkjmlab.sorm4j.extension.SormContext;
import org.nkjmlab.sorm4j.extension.logger.LoggerContext;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMapping;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

/**
 * Default implementation of {@link ColumnToFieldAccessorMapper}
 *
 * @author nkjm
 *
 */

public final class DefaultColumnToFieldAccessorMapper implements ColumnToFieldAccessorMapper {

  private final LoggerContext loggerContext;

  public DefaultColumnToFieldAccessorMapper() {
    this(LoggerContext.builder().build());
  }

  public DefaultColumnToFieldAccessorMapper(LoggerContext loggerContext) {
    this.loggerContext = loggerContext;
  }

  @Override
  public ColumnToAccessorMapping createMapping(Class<?> objectClass) {
    Map<String, FieldAccessor> accessors = createAccessors(objectClass);
    String aliasPrefix = SormContext.getColumnAliasPrefix(objectClass);
    Map<String, FieldAccessor> aliasAccessors = createAliasAccessors(aliasPrefix, accessors);
    return new ColumnToAccessorMapping(objectClass, accessors, aliasPrefix, aliasAccessors);
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
        loggerContext.getLogger(DefaultColumnToFieldAccessorMapper.class).warn(
            "Getter [{}] should not have parameter but has {} params.", getter,
            getter.getParameterCount());
      }
      return null;
    }
    if (getter.getReturnType() == void.class) {
      if (logging) {
        loggerContext.getLogger(DefaultColumnToFieldAccessorMapper.class)
            .warn("Getter [{}] must have return a parameter.", getter);
      }
    }
    return getter;
  }

  private Method isValidSetter(Method setter) {
    if (setter == null) {
      return null;
    }
    if (setter.getParameterCount() != 1) {
      loggerContext.getLogger(DefaultColumnToFieldAccessorMapper.class).warn(
          "Setter [{}] should have a single parameter but has {} params.", setter,
          setter.getParameterCount());
      return null;
    }
    return setter;
  }


  private Map<String, FieldAccessor> createAccessors(Class<?> objectClass) {
    Set<String> names = new HashSet<>();
    names.addAll(getAllFields(objectClass).keySet());
    names.addAll(getAllGetters(objectClass).keySet());
    names.addAll(getAllSetters(objectClass).keySet());
    names.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    names.addAll(getAnnotatedGettersMap(objectClass).keySet());
    names.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    return createAccessors(objectClass, new ArrayList<>(names));
  }

  private Map<String, FieldAccessor> createAccessors(Class<?> objectClass,
      List<String> columnNames) {
    Map<String, Field> fields = getAllFields(objectClass);
    Map<String, Method> getters = getAllGetters(objectClass);
    Map<String, Method> allMethods = getAllMethods(objectClass);
    Map<String, Method> setters = getAllSetters(objectClass);
    Map<String, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<String, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<String, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    Map<String, FieldAccessor> ret = new HashMap<>();
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
        loggerContext.getLogger(DefaultColumnToFieldAccessorMapper.class).debug(
            "Skip matching with ColumnName [{}] to field because could not found corresponding field.",
            canonicalColName);
      } else {
        ret.put(canonicalColName, new FieldAccessor(canonicalColName, f, g, s));
      }
    }
    return ret;
  }

  private Map<String, FieldAccessor> createAliasAccessors(String prefix,
      Map<String, FieldAccessor> accessors) {
    if (prefix.length() == 0) {
      return Collections.emptyMap();
    }

    Map<String, FieldAccessor> ret = new HashMap<>();

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
