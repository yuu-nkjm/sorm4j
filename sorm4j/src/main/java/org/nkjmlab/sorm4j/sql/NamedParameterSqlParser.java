package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterSqlParserImpl;

/**
 * SQL parser for named parameters.
 *
 * <p>
 * Following characters could be used for named parameters.
 *
 * <pre>
 * <code>
 * ('a' &lt;= c &amp;&amp; c &lt;= 'z') || ('A' &lt;= c &amp;&amp; c &lt;= 'Z') || ('0' &lt;= c &amp;&amp; c &lt;= '9') || c == '_';
 * </code>
 * </pre>
 *
 * @author yuu_nkjm
 *
 */
@Experimental
public interface NamedParameterSqlParser extends ParameterizedSqlParser {

  /**
   * Binds key-value pairs to named parameters in a SQL statement.
   *
   * @param keyValuePairOfNamedParameters
   * @return
   */
  NamedParameterSqlParser bindAll(Map<String, Object> keyValuePairOfNamedParameters);

  /**
   * Binds a key-value pair to named parameters in a SQL statement.
   *
   * @param key
   * @param value
   * @return
   */
  NamedParameterSqlParser bind(String key, Object value);

  /**
   * Binds a bean. The field names map to keys of parameter by {@link ColumnToFieldAccessorMapper}.
   *
   * @param bean
   * @return
   */
  NamedParameterSqlParser bindBean(Object bean);

  /**
   * Creates {@link NamedParameterSqlParser} object. the named parameters should have the given prefix and
   * suffix.
   *
   * @param sql
   * @param prefix
   * @param suffix
   * @return
   */
  @Experimental
  static NamedParameterSqlParser of(String sql, char prefix, char suffix,
      ColumnToFieldAccessorMapper columnFieldMapper) {
    return new NamedParameterSqlParserImpl(sql, prefix, suffix, columnFieldMapper);
  }

  /**
   * Creates {@link NamedParameterSqlParser} object.
   *
   * @param sql
   * @return
   */

  static NamedParameterSqlParser of(String sql) {
    return new NamedParameterSqlParserImpl(sql);
  }

  /**
   * Creates {@link NamedParameterSqlParser} object with parameters.
   *
   * @param sql
   * @param namedParameters
   * @return
   */
  static ParameterizedSql parse(String sql, Map<String, Object> namedParameters) {
    return NamedParameterSqlParser.of(sql).bindAll(namedParameters).parse();
  }


}
