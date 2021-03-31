package org.nkjmlab.sorm4j.internal.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */
public final class SqlStatementImpl implements SqlStatement {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private SqlStatementImpl(String sql, Object... parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }


  @Override
  public String toString() {
    return "sql=[" + sql + "]" + ((parameters == null || parameters.length == 0) ? ""
        : ", parameters=" + Arrays.toString(parameters) + "");
  }

  @Override
  public final String getSql() {
    return sql;
  }

  @Override
  public final Object[] getParameters() {
    return parameters;
  }


  public static SqlStatement from(String sql, Object... parameters) {
    SqlStatement st = new SqlStatementImpl(sql, parameters);
    if (parameters.length == 0) {
      return st;
    }
    st = sql.contains(LIST_PLACEHOLDER) ? procListPlaceholder(sql, parameters) : st;
    st = sql.contains(EMBEDDED_PLACEHOLDER)
        ? procEmbeddedPlaceholder(st.getSql(), st.getParameters())
        : st;
    return st;
  }

  private static final char LIST_PLACEHOLDER_PREFIX = '<';
  private static final char LIST_PLACEHOLDER_SUFFIX = '>';
  private static final String LIST_PLACEHOLDER =
      LIST_PLACEHOLDER_PREFIX + "?" + LIST_PLACEHOLDER_SUFFIX;

  private static SqlStatement procListPlaceholder(String sql, Object[] parameters) {
    final char[] arry = sql.toCharArray();
    final List<Integer> listParametersIndexs = new ArrayList<>();
    int parameterIndex = 0;

    for (int i = 0; i < arry.length; i++) {
      char c = arry[i];
      if (c == '?' && i - 1 >= 0 && arry[i - 1] == LIST_PLACEHOLDER_PREFIX && i + 1 < arry.length
          && arry[i + 1] == LIST_PLACEHOLDER_SUFFIX) {
        listParametersIndexs.add(parameterIndex);
        parameterIndex++;
      } else if (c == '?') {
        parameterIndex++;
      }
    }
    String expanddedSql = sql;

    for (int i = 0; i < listParametersIndexs.size(); i++) {
      int index = expanddedSql.indexOf(LIST_PLACEHOLDER);
      List<?> parameter = (List<?>) parameters[listParametersIndexs.get(i)];
      expanddedSql = expanddedSql.subSequence(0, index)
          + "?,".repeat(parameter.size()).substring(0, 2 * parameter.size() - 1)
          + expanddedSql.substring(index + 3, expanddedSql.length());
    }
    List<Object> afterEmbeddedParams = new ArrayList<>();
    for (int i = 0; i < parameters.length; i++) {
      if (listParametersIndexs.contains(i)) {
        List<?> parameter = (List<?>) parameters[i];
        parameter.forEach(p -> afterEmbeddedParams.add(p));
      } else {
        afterEmbeddedParams.add(parameters[i]);
      }
    }

    return new SqlStatementImpl(expanddedSql, afterEmbeddedParams.toArray());

  }

  private static final char EMBEDDED_PLACEHOLDER_PREFIX = '$';
  private static final char EMBEDDED_PLACEHOLDER_SUFFIX = '$';
  private static final String EMBEDDED_PLACEHOLDER =
      EMBEDDED_PLACEHOLDER_PREFIX + "?" + EMBEDDED_PLACEHOLDER_SUFFIX;

  private static SqlStatement procEmbeddedPlaceholder(String sql, Object[] parameters) {

    final char[] arry = sql.toCharArray();
    final List<Integer> embeddedParametersIndexs = new ArrayList<>();
    int parameterIndex = 0;

    for (int i = 0; i < arry.length; i++) {
      char c = arry[i];
      if (c == '?' && i - 1 >= 0 && arry[i - 1] == EMBEDDED_PLACEHOLDER_PREFIX
          && i + 1 < arry.length && arry[i + 1] == EMBEDDED_PLACEHOLDER_SUFFIX) {
        embeddedParametersIndexs.add(parameterIndex);
        parameterIndex++;
      } else if (c == '?') {
        parameterIndex++;
      }
    }
    String embeddedSql = sql;

    for (int i = 0; i < embeddedParametersIndexs.size(); i++) {
      int index = embeddedSql.indexOf(EMBEDDED_PLACEHOLDER);
      embeddedSql = embeddedSql.subSequence(0, index)
          + SqlUtils.literal(parameters[embeddedParametersIndexs.get(i)])
          + embeddedSql.substring(index + 3, embeddedSql.length());
    }
    List<Object> afterEmbeddedParams = new ArrayList<>();
    for (int i = 0; i < parameters.length; i++) {
      if (!embeddedParametersIndexs.contains(i)) {
        afterEmbeddedParams.add(parameters[i]);
      }
    }

    return new SqlStatementImpl(embeddedSql, afterEmbeddedParams.toArray());
  }


}
