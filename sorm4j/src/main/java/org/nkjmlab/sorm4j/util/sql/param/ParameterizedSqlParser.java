package org.nkjmlab.sorm4j.util.sql.param;

import java.util.Map;
import java.util.TreeMap;

import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.common.ParameterizedSql;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;

public interface ParameterizedSqlParser {
  /**
   * Parse to {@link ParameterizedSql} objects.
   *
   * @return
   */
  ParameterizedSql parse();

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
    String ret =
        ParameterizedStringFormatter.newString(
            sql,
            "{?}",
            parameters.length,
            index -> parameters[index] == null ? null : parameters[index].toString());
    if (ret.contains("{?}")) {
      Object[] params = {sql, parameters};
      throw new SormException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "Could not embed all parameters. sql={},parameters={}", params));
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

    parameters.keySet().stream()
        .forEach(
            key -> {
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
