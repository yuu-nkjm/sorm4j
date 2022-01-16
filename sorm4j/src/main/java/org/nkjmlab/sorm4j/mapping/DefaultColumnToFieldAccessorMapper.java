
package org.nkjmlab.sorm4j.mapping;

import static java.util.Objects.*;
import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.annotation.OrmColumn;
import org.nkjmlab.sorm4j.annotation.OrmGetter;
import org.nkjmlab.sorm4j.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.annotation.OrmSetter;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

/**
 * Default implementation of {@link ColumnToFieldAccessorMapper}
 *
 * @author nkjm
 *
 */

public final class DefaultColumnToFieldAccessorMapper implements ColumnToFieldAccessorMapper {

  private final LoggerContext loggerContext;

  private static final Set<String> IGNORE_METHODS =
      Set.of("NOTIFY", "NOTIFYALL", "WAIT", "TOSTRING", "HASHCODE");

  public DefaultColumnToFieldAccessorMapper() {
    this(SormContext.getDefaultContext().getLoggerContext());
  }

  public DefaultColumnToFieldAccessorMapper(LoggerContext loggerContext) {
    this.loggerContext = loggerContext;
  }

  @Override
  public ColumnToAccessorMapping createMapping(Class<?> objectClass, String columnAliasPrefix) {
    Map<String, FieldAccessor> accessors =
        createAccessors(objectClass, createAcceptableColumnNames(objectClass));
    return new ColumnToAccessorMapping(objectClass, accessors, columnAliasPrefix);
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
        .collect(Collectors.toMap(f -> toCanonicalCase(f.getName()), f -> f));
  }


  private Map<String, Method> getAllGetters(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "get").entrySet().stream()
        .filter(e -> nonNull(isValidGetter(e.getValue())))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private Map<String, Method> getAllMethods(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "").entrySet().stream()
        .filter(e -> !e.getKey().startsWith("GET") && nonNull(isValidGetter(e.getValue(), false)))
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
        .collect(Collectors.toMap(f -> toCanonicalCase(f.getAnnotation(ann).value()), f -> f));
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


  private Set<String> createAcceptableColumnNames(Class<?> objectClass) {
    Set<String> acceptableColumnNames = new HashSet<>();
    acceptableColumnNames.addAll(getAllFields(objectClass).keySet());
    acceptableColumnNames.addAll(getAllGetters(objectClass).keySet());
    acceptableColumnNames.addAll(getAllSetters(objectClass).keySet());
    acceptableColumnNames.addAll(getAllMethods(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatedGettersMap(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    acceptableColumnNames.removeAll(IGNORE_METHODS);
    return acceptableColumnNames;
  }

  private Map<String, FieldAccessor> createAccessors(Class<?> objectClass,
      Set<String> acceptableColumnNames) {
    Map<String, Field> fields = getAllFields(objectClass);
    Map<String, Method> getters = getAllGetters(objectClass);
    Map<String, Method> allMethods = getAllMethods(objectClass);
    Map<String, Method> setters = getAllSetters(objectClass);
    Map<String, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<String, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<String, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    Map<String, FieldAccessor> ret = new HashMap<>();
    for (String acceptableColName : acceptableColumnNames) {
      Field f =
          nonNull(annotatedFields.get(acceptableColName)) ? annotatedFields.get(acceptableColName)
              : fields.get(acceptableColName);
      Method g =
          nonNull(annotatedGetters.get(acceptableColName)) ? annotatedGetters.get(acceptableColName)
              : nonNull(getters.get(acceptableColName)) ? getters.get(acceptableColName)
                  : allMethods.get(acceptableColName);
      Method s =
          nonNull(annotatedSetters.get(acceptableColName)) ? annotatedSetters.get(acceptableColName)
              : setters.get(acceptableColName);

      if (f == null && (g == null && s == null)) {
        loggerContext.getLogger(DefaultColumnToFieldAccessorMapper.class).debug(
            "Skip matching with ColumnName [{}] to field because could not found corresponding field.",
            acceptableColName);
      } else {
        ret.put(acceptableColName, new FieldAccessor(acceptableColName, f, g, s));
      }
    }
    return ret;
  }


}
