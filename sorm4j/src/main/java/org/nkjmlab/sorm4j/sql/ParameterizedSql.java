package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import java.util.TreeMap;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.internal.sql.ParameterizedSqlImpl;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;


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
    return OrderedParameterSqlParser.parse(sql, parameters);
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
    return NamedParameterSqlParser.parse(sql, parameters);
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
    String ret = ParameterizedStringUtils.newString(sql, "{?}", parameters.length,
        index -> parameters[index] == null ? null : parameters[index].toString());
    if (ret.contains("{?}")) {
      throw new SormException(ParameterizedStringUtils
          .newString("Could not embed all parameters. sql={},parameters={}", sql, parameters));
    } else {
      return ret;
    }
  }

  /**
   * Embeds the given parameters to the give SQL string. The given parameters must contain
   * everything to be embedded. If any of the given parameters are not embedded, they will be
   * ignored.
   *
   * @param sql
   * @param parameters
   * @return
   */
  @Experimental
  public static String embedParameter(String sql, Map<String, Object> parameters) {
    // Ordered by position in the sentence
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

    return embedParameter(_sql, _parameters);
  }



}
