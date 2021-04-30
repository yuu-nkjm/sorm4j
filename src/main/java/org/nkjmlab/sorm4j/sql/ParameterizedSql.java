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

  /**
   * Gets the SQL string after binding parameter.
   *
   * @return
   */
  @Experimental
  String getBindedSql();

  /**
   * Creates {@link ParameterizedSql} object of the given SQL string. When you use a SQL statement
   * with parameter, use {@link #parse} method.
   *
   * @param sql without parameter.
   * @return
   */
  static ParameterizedSql of(String sql) {
    return ParameterizedSqlImpl.of(sql);
  }

  /**
   * Creates {@link ParameterizedSql} object of the given SQL string and parameters. The given
   * parameters should be simple ordered parameters. When you use special parameters, use
   * {@link #parse} method.
   *
   * @param sql
   * @param parameters ordered parameters without special parameters (e.g. named parameter, list
   *        parameter and embedded parameter)
   * @return
   */
  static ParameterizedSql of(String sql, Object... parameters) {
    return ParameterizedSqlImpl.of(sql, parameters);
  }


  /**
   * Parses the given SQL and ordered parameters which could include special parameters (e.g. list
   * parameter and embedded parameter).
   *
   * @param sql
   * @param parameters
   * @return
   */
  static ParameterizedSql parse(String sql, Object... parameters) {
    return OrderedParameterSql.parse(sql, parameters);
  }

  /**
   * Parses the given SQL and named parameters which could include special parameters (e.g. list
   * parameter and embedded parameter).
   *
   * @param sql
   * @param parameters
   * @return
   */
  static ParameterizedSql parse(String sql, Map<String, Object> parameters) {
    return NamedParameterSql.parse(sql, parameters);
  }

  /**
   * Embeds the given parameters to the give SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  @Experimental
  public static String embedParameter(String sql, Object... parameters) {
    if (parameters == null || parameters.length == 0) {
      return sql;
    }
    return StringUtils.replacePlaceholder(sql, "{?}", parameters.length,
        index -> parameters[index] == null ? null : parameters[index].toString());
  }

  /**
   * Embeds the given parameters to the give SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  @Experimental
  public static String embedParameter(String sql, Map<String, Object> parameters) {
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
    Object[] _parameters = orderdParams.values().toArray();

    return StringUtils.replacePlaceholder(_sql, "{?}", _parameters.length,
        index -> _parameters[index] == null ? null : _parameters[index].toString());
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
