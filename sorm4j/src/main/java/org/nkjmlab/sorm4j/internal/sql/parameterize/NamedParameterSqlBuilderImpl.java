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
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.parameterize.NamedParameterSqlBuilder;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;

/**
 * SQL parser for named parameters. The instance could be convert to {@link ParameterizedSql}.The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public final class NamedParameterSqlBuilderImpl implements NamedParameterSqlBuilder {

  private static final Map<Class<?>, Map<String, ContainerAccessor>> nameToFieldMaps =
      new ConcurrentHashMap<>();

  private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnToFieldAccessorMapper();

  private static final String DEFAULT_NAMED_PARAMETER_PREFIX = ":";

  private final String sql;
  private final Pattern pattern;
  private final ColumnToFieldAccessorMapper nameToFieldMapper;
  private final RowMap parameters;
  private Object parametersContainer;

  public NamedParameterSqlBuilderImpl(String sql, ColumnToFieldAccessorMapper nameToFieldMapper) {
    this.sql = sql;
    this.nameToFieldMapper = nameToFieldMapper;
    this.parameters = RowMap.of();
    this.pattern = Pattern.compile(DEFAULT_NAMED_PARAMETER_PREFIX + "([a-zA-Z0-9_]+)");
  }

  public NamedParameterSqlBuilderImpl(String sql) {
    this(sql, DEFAULT_COLUMN_FIELD_MAPPER);
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
    this.parametersContainer = parametersContainer;
    return this;
  }

  @Override
  public ParameterizedSql build() {
    List<Object> orderedParams = extractParameterNames(sql).stream().map(e -> getParam(e)).toList();
    return ParameterizedSqlImpl.of(
        replaceNamedParameterToPlaceholder(sql), orderedParams.toArray());
  }

  private Object getParam(String parameterName) {
    if (parametersContainer != null) {
      ContainerAccessor acc = getAccessor(parametersContainer, parameterName);
      if (acc != null) {
        return Try.getOrElseNull(() -> acc.get(parametersContainer));
      }
    }
    return parameters.get(parameterName);
  }

  private ContainerAccessor getAccessor(Object parametersContainer, String parameterName) {
    final Class<?> objectClass = parametersContainer.getClass();
    return nameToFieldMaps
        .computeIfAbsent(objectClass, k -> nameToFieldMapper.createMapping(objectClass))
        .get(SormContext.getDefaultCanonicalStringCache().toCanonicalName(parameterName));
  }

  private List<String> extractParameterNames(String sql) {
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
