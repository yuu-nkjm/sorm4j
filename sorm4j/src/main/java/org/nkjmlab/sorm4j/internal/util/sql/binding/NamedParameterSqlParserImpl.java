package org.nkjmlab.sorm4j.internal.util.sql.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.impl.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.context.impl.FieldAccessor;
import org.nkjmlab.sorm4j.internal.sql.parameterize.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.parameterize.NamedParameterSqlFactory;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;

/**
 * SQL parser for named parameters. The instance could be convert to {@link ParameterizedSql}.The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 */
public final class NamedParameterSqlParserImpl implements NamedParameterSqlFactory {

  private static final Map<Class<?>, Map<String, FieldAccessor>> nameToFieldMaps =
      new ConcurrentHashMap<>();

  private static final ColumnToFieldAccessorMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnToFieldAccessorMapper();

  private static final Character DEFAULT_PREFIX = ':';
  private static final Character DEFAULT_SUFFIX = null;

  private final String sql;
  private final Character prefix;
  private final Character suffix;
  private final ColumnToFieldAccessorMapper nameToFieldMapper;
  private final Map<String, Object> parameters;
  private Object parametersContainer;

  public NamedParameterSqlParserImpl(
      String sql,
      Character prefix,
      Character suffix,
      ColumnToFieldAccessorMapper nameToFieldMapper) {
    this.sql = sql;
    this.prefix = prefix;
    this.suffix = suffix;
    this.nameToFieldMapper = nameToFieldMapper;
    this.parameters = new HashMap<>();
  }

  public NamedParameterSqlParserImpl(String sql) {
    this(sql, DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_COLUMN_FIELD_MAPPER);
  }

  @Override
  public NamedParameterSqlFactory bind(Map<String, Object> namedParams) {
    this.parameters.putAll(namedParams);
    return this;
  }

  @Override
  public NamedParameterSqlFactory bind(String key, Object value) {
    this.parameters.put(key, value);
    return this;
  }

  @Override
  public NamedParameterSqlFactory bind(Object parametersContainer) {
    this.parametersContainer = parametersContainer;
    return this;
  }

  @Override
  public ParameterizedSql create() {
    // Ordered by position in the sentence
    TreeMap<Integer, Object> orderdParams = new TreeMap<>();
    String resultSql = this.sql;

    List<String> parameterNameList = createParameters();

    for (String parameterName : parameterNameList) {
      String namedPlaceholder = prefix + parameterName + (suffix != null ? suffix : "");
      int pos = resultSql.indexOf(namedPlaceholder);
      if (pos == -1) {
        continue;
      }
      if (parametersContainer != null) {
        FieldAccessor acc = getAccessor(parameterName);
        if (acc != null) {
          orderdParams.put(pos, Try.getOrElseNull(() -> acc.get(parametersContainer)));
        }
        if (parameters.containsKey(parameterName)) {
          orderdParams.put(pos, parameters.get(parameterName));
        }
        if (acc != null || parameters.containsKey(parameterName)) {
          resultSql = resultSql.replace(namedPlaceholder, "?");
        }
      } else {
        if (parameters.containsKey(parameterName)) {
          orderdParams.put(pos, parameters.get(parameterName));
          resultSql = resultSql.replace(namedPlaceholder, "?");
        }
      }
    }
    return ParameterizedSqlImpl.of(resultSql, orderdParams.values().toArray());
  }

  private List<String> createParameters() {
    final char[] arry = sql.toCharArray();
    final List<String> ret = new ArrayList<>();

    int i = 0;
    while (i < arry.length) {
      if (arry[i] == prefix) {
        int j = i + 1;
        for (; j < arry.length; j++) {
          char c = arry[j];
          if (!isNamedParameterElement(c) || c == suffix) {
            break;
          }
        }
        ret.add(sql.substring(i + 1, j));
        i = j + 1;
      } else {
        i++;
      }
    }
    return ret;
  }

  private boolean isNamedParameterElement(char c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '_';
  }

  private FieldAccessor getAccessor(String parameterName) {
    final Class<?> objectClass = parametersContainer.getClass();
    return nameToFieldMaps
        .computeIfAbsent(objectClass, k -> nameToFieldMapper.createMapping(objectClass))
        .get(SormContext.getDefaultCanonicalStringCache().toCanonicalName(parameterName));
  }
}
