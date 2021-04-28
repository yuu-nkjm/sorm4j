package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import java.util.TreeMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.sql.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;
import org.nkjmlab.sorm4j.internal.util.StringUtils;


/**
 * This class represents a SQL statement with ordered parameters.
 *
 * @author nkjm
 *
 */

public interface ParameterizedSql {

  /**
   * Gets this SQL statement.
   *
   * @return
   */
  String getSql();

  /**
   * Gets this ordered parameters.
   *
   * @return
   */
  Object[] getParameters();

  @Experimental
  String getBindedSql();


  @Experimental
  static ParameterizedSql parse(String sql, Object... parameters) {
    return OrderedParameterSql.parse(sql, parameters);
  }

  @Experimental
  static ParameterizedSql parse(String sql, Map<String, Object> parameters) {
    return NamedParameterSql.parse(sql, parameters);
  }

  @Experimental
  public static String embededParameter(String sql, Object... parameters) {
    if (parameters == null || parameters.length == 0) {
      return sql;
    }
    return StringUtils.replacePlaceholder(sql, "{?}", parameters.length,
        index -> SqlUtils.literal(parameters[index]));
  }

  @Experimental
  public static String embededParameter(String sql, Map<String, Object> parameters) {
    if (parameters == null || parameters.size() == 0) {
      return sql;
    }
    TreeMap<Integer, Object> orderdParams = new TreeMap<>();

    parameters.keySet().stream().forEach(key -> {
      int pos = sql.indexOf("{:" + key + "}");
      if (pos == -1) {
        return;
      }
      orderdParams.put(pos, parameters.get(key));
    });
    String _sql = sql.replaceAll("\\{:.*?\\}", "{?}");
    Object[] _params = orderdParams.values().toArray();

    return StringUtils.replacePlaceholder(_sql, "{?}", _params.length,
        index -> SqlUtils.literal(_params[index]));
  }



  /**
   * Creates {@link ParameterizedSql} object from the given SQL string. When you use a SQL statement
   * with parameter, use {@link NamedParameterSql}, {@link OrderedParameterSql}.
   *
   * @param sql without parameter.
   * @return
   */
  static ParameterizedSql from(String sql) {
    return ParameterizedSqlImpl.parse(sql);
  }

  /**
   * Convert the given arguments to SQL literal.
   *
   * @param element
   * @return
   */
  @Experimental
  static String literal(Object element) {
    return SqlUtils.literal(element);
  }

  /**
   * Returns single quoted expression. If it includes single quotations, they will be escaped.
   *
   * @param expr
   * @return
   */
  @Experimental
  static String quote(String expr) {
    return SqlUtils.quote(expr);
  }

}
