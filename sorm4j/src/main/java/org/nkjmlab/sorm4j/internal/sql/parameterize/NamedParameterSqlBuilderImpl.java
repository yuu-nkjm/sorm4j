package org.nkjmlab.sorm4j.internal.sql.parameterize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.impl.ContainerAccessor;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.sql.parameterize.NamedParameterSqlBuilder;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;
import org.nkjmlab.sorm4j.util.function.exception.Try;

/**
 * SQL parser for named parameters. The instance could be convert to {@link ParameterizedSql}.The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public final class NamedParameterSqlBuilderImpl implements NamedParameterSqlBuilder {

  private static final String DEFAULT_NAMED_PARAMETER_PREFIX = ":";
  private static final Pattern pattern =
      Pattern.compile(DEFAULT_NAMED_PARAMETER_PREFIX + "([a-zA-Z0-9_]+)");

  private static final Map<Class<?>, Map<String, ContainerAccessor>> nameToFieldMaps =
      new ConcurrentHashMap<>();

  private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnToFieldAccessorMapper();

  private final String sql;
  private final RowMap parameters;
  private final List<String> parameterNames;

  public NamedParameterSqlBuilderImpl(String sql) {
    this.sql = sql;
    this.parameters = RowMap.of();
    this.parameterNames = extractParameterNames(sql);
  }

  @Override
  public NamedParameterSqlBuilder bindParameters(Map<String, Object> namedParams) {
    this.parameters.putAll(namedParams);
    return this;
  }

  @Override
  public NamedParameterSqlBuilder bindParameter(String key, Object value) {
    this.parameters.put(key, value);
    return this;
  }

  @Override
  public NamedParameterSqlBuilder bindParameters(Object parametersContainer) {
    parameterNames.forEach(
        parameterName -> {
          ContainerAccessor acc = getAccessor(parametersContainer, parameterName);
          if (acc != null) {
            parameters.put(parameterName, Try.getOrElseNull(() -> acc.get(parametersContainer)));
          }
        });
    return this;
  }

  @Override
  public ParameterizedSql build() {
    List<Object> orderedParams = parameterNames.stream().map(e -> getParam(e)).toList();
    return ParameterizedSqlImpl.of(
        replaceNamedParameterToPlaceholder(sql), orderedParams.toArray());
  }

  private Object getParam(String parameterName) {
    return parameters.get(parameterName);
  }

  private ContainerAccessor getAccessor(Object parametersContainer, String parameterName) {
    final Class<?> objectClass = parametersContainer.getClass();
    return nameToFieldMaps
        .computeIfAbsent(objectClass, k -> DEFAULT_COLUMN_FIELD_MAPPER.createMapping(objectClass))
        .get(SormContext.getDefaultCanonicalStringCache().toCanonicalName(parameterName));
  }

  private static List<String> extractParameterNames(String sql) {
    List<String> parameterNames = new ArrayList<>();
    Matcher matcher = pattern.matcher(sql);

    while (matcher.find()) {
      parameterNames.add(matcher.group(1));
    }
    return parameterNames;
  }

  private String replaceNamedParameterToPlaceholder(String sql) {

    Matcher matcher = pattern.matcher(sql);
    StringBuffer replacedSql = new StringBuffer();

    while (matcher.find()) {
      matcher.appendReplacement(replacedSql, "?");
    }
    matcher.appendTail(replacedSql);

    return replacedSql.toString();
  }
}
