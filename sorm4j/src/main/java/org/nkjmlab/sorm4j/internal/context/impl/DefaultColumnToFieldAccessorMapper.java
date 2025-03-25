package org.nkjmlab.sorm4j.internal.context.impl;

import static java.util.Objects.nonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.mapping.annotation.OrmColumn;
import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.mapping.annotation.OrmGetter;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.mapping.annotation.OrmSetter;

/**
 * Default implementation of {@link ColumnToFieldAccessorMapper}
 *
 * @author nkjm
 */
public final class DefaultColumnToFieldAccessorMapper implements ColumnToFieldAccessorMapper {

  private static final Set<String> IGNORE_METHODS =
      Set.of("NOTIFY", "NOTIFYALL", "WAIT", "TOSTRING", "HASHCODE");

  @Override
  public Map<String, ContainerAccessor> createMapping(Class<?> objectClass) {
    Set<String> acceptableColumnNames = createAcceptableColumnNames(objectClass);
    Map<String, Field> fields = getAllFields(objectClass);
    Map<String, Method> getters = getAllGetters(objectClass);
    Map<String, Method> recordAccessors = getAllRecordAccessors(objectClass);
    Map<String, Method> setters = getAllSetters(objectClass);
    Map<String, Field> annotatedFields = getAnnotatedFieldsMap(objectClass);
    Map<String, Method> annotatedGetters = getAnnotatedGettersMap(objectClass);
    Map<String, Method> annotatedSetters = getAnnotatatedSettersMap(objectClass);

    Map<String, ContainerAccessor> ret = new HashMap<>();
    for (String acceptableColName : acceptableColumnNames) {
      Field f = procField(annotatedFields, fields, acceptableColName);
      Method g = procGetter(annotatedGetters, recordAccessors, getters, acceptableColName);
      Method s = procSetter(annotatedSetters, setters, acceptableColName);

      if (f == null && (g == null && s == null)) {
      } else {
        ret.put(acceptableColName, new ContainerAccessor(acceptableColName, f, g, s));
      }
    }
    return ret;
  }

  private Field procField(
      Map<String, Field> annotatedFields, Map<String, Field> fields, String acceptableColName) {
    return Stream.of(annotatedFields.get(acceptableColName), fields.get(acceptableColName))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Method procGetter(
      Map<String, Method> annotatedGetters,
      Map<String, Method> recordAccessors,
      Map<String, Method> getters,
      String acceptableColName) {

    return Stream.of(
            annotatedGetters.get(acceptableColName),
            recordAccessors.get(acceptableColName),
            getters.get(acceptableColName))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Method procSetter(
      Map<String, Method> annotatedSetters, Map<String, Method> setters, String acceptableColName) {
    return Stream.of(annotatedSetters.get(acceptableColName), setters.get(acceptableColName))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Map<String, Method> extractedMethodStartWith(Class<?> objectClass, String prefix) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getMethods())
        .filter(
            m ->
                Objects.isNull(m.getAnnotation(ignoreAnn))
                    && !Modifier.isStatic(m.getModifiers())
                    && m.getName().length() > prefix.length()
                    && m.getName().substring(0, prefix.length()).equals(prefix))
        .collect(
            Collectors.toMap(
                m ->
                    SormContext.getDefaultCanonicalStringCache()
                        .toCanonicalName(m.getName().substring(prefix.length())),
                m -> m,
                (v1, v2) -> v2));
  }

  private static Map<String, Field> getAllFields(final Class<?> objectClass) {
    Class<OrmIgnore> ignoreAnn = OrmIgnore.class;
    return Arrays.stream(objectClass.getFields())
        .filter(
            f -> Objects.isNull(f.getAnnotation(ignoreAnn)) && !Modifier.isStatic(f.getModifiers()))
        .collect(
            Collectors.toMap(
                f -> SormContext.getDefaultCanonicalStringCache().toCanonicalName(f.getName()),
                f -> f));
  }

  private Map<String, Method> getAllGetters(Class<?> objectClass) {
    return extractedMethodStartWith(objectClass, "get").entrySet().stream()
        .filter(e -> nonNull(isValidGetter(e.getValue())))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  private Map<String, Method> getAllRecordAccessors(Class<?> objectClass) {
    if (!objectClass.isRecord()) {
      return Collections.emptyMap();
    }
    return Arrays.stream(objectClass.getRecordComponents())
        .collect(Collectors.toMap(e -> e.getName(), e -> e.getAccessor()));
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
        .collect(
            Collectors.toMap(
                m ->
                    SormContext.getDefaultCanonicalStringCache()
                        .toCanonicalName(m.getAnnotation(ann).value()),
                m -> m));
  }

  private Map<String, Field> getAnnotatedFieldsMap(Class<?> objectClass) {
    Class<OrmColumn> ann = OrmColumn.class;
    return Arrays.stream(objectClass.getFields())
        .filter(f -> Objects.nonNull(f.getAnnotation(ann)))
        .collect(
            Collectors.toMap(
                f ->
                    SormContext.getDefaultCanonicalStringCache()
                        .toCanonicalName(f.getAnnotation(ann).value()),
                f -> f));
  }

  private Map<String, Method> getAnnotatedGettersMap(Class<?> objectClass) {
    Class<OrmGetter> ann = OrmGetter.class;
    return Arrays.stream(objectClass.getMethods())
        .filter(m -> Objects.nonNull(m.getAnnotation(ann)) && nonNull(isValidGetter(m)))
        .collect(
            Collectors.toMap(
                m ->
                    SormContext.getDefaultCanonicalStringCache()
                        .toCanonicalName(m.getAnnotation(ann).value()),
                m -> m));
  }

  private Method isValidGetter(Method getter) {
    return (getter == null
            || getter.getName().equals("getClass")
            || getter.getParameterCount() != 0
            || getter.getReturnType() == void.class)
        ? null
        : getter;
  }

  private Method isValidSetter(Method setter) {
    return (setter == null || setter.getParameterCount() != 1) ? null : setter;
  }

  private Set<String> createAcceptableColumnNames(Class<?> objectClass) {
    Set<String> acceptableColumnNames = new HashSet<>();
    acceptableColumnNames.addAll(getAllFields(objectClass).keySet());
    acceptableColumnNames.addAll(getAllGetters(objectClass).keySet());
    acceptableColumnNames.addAll(getAllSetters(objectClass).keySet());
    acceptableColumnNames.addAll(getAllRecordAccessors(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatedFieldsMap(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatedGettersMap(objectClass).keySet());
    acceptableColumnNames.addAll(getAnnotatatedSettersMap(objectClass).keySet());

    acceptableColumnNames.removeAll(IGNORE_METHODS);
    return acceptableColumnNames;
  }

  /**
   * Gets column alias prefix based on {@link OrmColumnAliasPrefix} annotation. It will converted as
   * the canonical name. If the give object class has no {@link OrmColumnAliasPrefix} annotation,
   * the column alias prefix is <code>
   * objectClass.getSimpleName() as the canonical name.</code>
   *
   * @param objectClass
   * @return
   */
  @Override
  public String getColumnAliasPrefix(Class<?> objectClass) {
    return Optional.ofNullable(objectClass.getAnnotation(OrmColumnAliasPrefix.class))
        .map(a -> SormContext.getDefaultCanonicalStringCache().toCanonicalName(a.value()))
        .orElse(
            SormContext.getDefaultCanonicalStringCache()
                .toCanonicalName(objectClass.getSimpleName()));
  }
}
