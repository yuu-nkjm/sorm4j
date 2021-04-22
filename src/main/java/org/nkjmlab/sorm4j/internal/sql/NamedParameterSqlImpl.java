package org.nkjmlab.sorm4j.internal.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.nkjmlab.sorm4j.extension.Accessor;
import org.nkjmlab.sorm4j.extension.ColumnFieldMapper;
import org.nkjmlab.sorm4j.extension.Configurator;
import org.nkjmlab.sorm4j.internal.mapping.ColumnToAccessorMap;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * SQL with named parameters. The instance could be convert to {@link ParameterizedSql}.The class could
 * treat {@link List} parameter.
 *
 * @author nkjm
 *
 */
public class NamedParameterSqlImpl implements NamedParameterSql {
  private static final char DEFAULT_PREFIX = ':';
  private static final char DEFAULT_SUFFIX = 0;

  private final String sql;
  private final char prefix;
  private final char suffix;
  private final ColumnFieldMapper columnFieldMapper;
  private final Map<String, Object> parameters = new HashMap<>();
  private Object bean;

  public NamedParameterSqlImpl(String sql, char prefix, char suffix,
      ColumnFieldMapper columnFieldMapper) {
    this.sql = sql;
    this.prefix = prefix;
    this.suffix = suffix;
    this.columnFieldMapper = columnFieldMapper;
  }

  public NamedParameterSqlImpl(String sql) {
    this(sql, DEFAULT_PREFIX, DEFAULT_SUFFIX, Configurator.DEFAULT_COLUMN_FIELD_MAPPER);
  }

  @Override
  public NamedParameterSql bindAll(Map<String, Object> namedParams) {
    namedParams.entrySet().stream().forEach(e -> bind(e.getKey(), e.getValue()));
    return this;
  }

  @Override
  public NamedParameterSql bind(String key, Object value) {
    this.parameters.put(key, value);
    return this;
  }

  @Override
  public NamedParameterSql bindBean(Object bean) {
    this.bean = bean;
    return this;
  }


  @Override
  public ParameterizedSql parse() {
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
        Accessor acc = getAccessor(parameterName);
        if (acc != null) {
          orderdParams.put(pos, Try.getOrNull(() -> acc.get(bean)));
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

  private static final Map<Class<?>, ColumnToAccessorMap> columnToAccessorMaps =
      new ConcurrentHashMap<>();

  private Accessor getAccessor(String parameterName) {
    final Class<?> objectClass = bean.getClass();
    return columnToAccessorMaps.computeIfAbsent(objectClass,
        k -> new ColumnToAccessorMap(objectClass, columnFieldMapper.createAccessors(objectClass)))
        .get(parameterName);
  }



}
