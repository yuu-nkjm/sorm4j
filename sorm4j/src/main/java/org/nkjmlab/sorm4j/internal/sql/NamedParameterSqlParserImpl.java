package org.nkjmlab.sorm4j.internal.sql;

import static org.nkjmlab.sorm4j.internal.util.StringCache.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.FieldAccessor;
import org.nkjmlab.sorm4j.context.NameToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.NamedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * SQL parser for named parameters. The instance could be convert to {@link ParameterizedSql}.The
 * class could treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public final class NamedParameterSqlParserImpl implements NamedParameterSqlParser {

  private static final Map<Class<?>, Map<String, FieldAccessor>> nameToFieldMaps =
      new ConcurrentHashMap<>();

  private static final NameToFieldAccessorMapper DEFAULT_COLUMN_FIELD_MAPPER =
      new DefaultColumnToFieldAccessorMapper();

  private static final char DEFAULT_PREFIX = ':';
  private static final char DEFAULT_SUFFIX = Character.MIN_VALUE;

  private final String sql;
  private final char prefix;
  private final char suffix;
  private final NameToFieldAccessorMapper nameToFieldMapper;
  private final Map<String, Object> parameters;
  private Object bean;

  public NamedParameterSqlParserImpl(String sql, char prefix, char suffix,
      NameToFieldAccessorMapper nameToFieldMapper) {
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
  public NamedParameterSqlParser bindAll(Map<String, Object> namedParams) {
    this.parameters.putAll(namedParams);
    return this;
  }

  @Override
  public NamedParameterSqlParser bind(String key, Object value) {
    this.parameters.put(key, value);
    return this;
  }

  @Override
  public NamedParameterSqlParser bindBean(Object bean) {
    this.bean = bean;
    return this;
  }


  @Override
  public ParameterizedSql parse() {
    // Ordered by position in the sentence
    TreeMap<Integer, Object> orderdParams = new TreeMap<>();
    String resultSql = this.sql;

    List<String> parameterNameList = createParameters();

    for (String parameterName : parameterNameList) {
      String namedPlaceholder = prefix + parameterName;
      namedPlaceholder = suffix != 0 ? namedPlaceholder + suffix : namedPlaceholder;
      int pos = resultSql.indexOf(namedPlaceholder);
      if (pos == -1) {
        continue;
      }
      if (bean != null) {
        FieldAccessor acc = getAccessor(parameterName);
        if (acc != null) {
          orderdParams.put(pos, Try.getOrElseNull(() -> acc.get(bean)));
        }
        if (parameters.containsKey(parameterName)) {
          orderdParams.put(pos, parameters.get(parameterName));
        }
        if (acc != null || parameters.containsKey(parameterName)) {
          resultSql = resultSql.replaceAll(namedPlaceholder, "?");
        }
      } else {
        if (parameters.containsKey(parameterName)) {
          orderdParams.put(pos, parameters.get(parameterName));
          resultSql = resultSql.replaceAll(namedPlaceholder, "?");
        }
      }
    }
    return ParameterizedSqlImpl.parse(resultSql, orderdParams.values().toArray());
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
    final Class<?> objectClass = bean.getClass();
    return nameToFieldMaps
        .computeIfAbsent(objectClass, k -> nameToFieldMapper.createMapping(objectClass))
        .get(toCanonicalCase(parameterName));
  }

}
