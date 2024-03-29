package org.nkjmlab.sorm4j.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.sql.NamedParameterSqlParserImpl;

/**
 * SQL parser for named parameters.
 *
 * <p>Example.
 *
 * <pre><code>
 * String sql = "select * from customer where id=:id and address=:address";
 *
 * ParameterizedSql statement =
 *     NamedParameterSql.parse(sql, Map.of("id",1, "address","Kyoto"));
 * // or
 * ParameterizedSql statement1 =
 *     NamedParameterSql.of(sql).bind("id", 1).bind("address", "Kyoto").parse();
 *
 * List<Customer> customers =  sorm.readList(Customer.class, statement);
 * </code></pre>
 *
 * @author yuu_nkjm
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
   * Creates {@link NamedParameterSqlParser} object. the named parameters should have the given
   * prefix and suffix.
   *
   * @param sql
   * @param prefix
   * @param suffix
   * @param columnFieldMapper
   * @return
   */
  @Experimental
  static NamedParameterSqlParser of(
      String sql, char prefix, char suffix, ColumnToFieldAccessorMapper columnFieldMapper) {
    return new NamedParameterSqlParserImpl(sql, prefix, suffix, columnFieldMapper);
  }

  /**
   * @param sql
   * @param columnFieldMapper
   * @return
   */
  @Experimental
  static NamedParameterSqlParser of(String sql, ColumnToFieldAccessorMapper columnFieldMapper) {
    return of(sql, ':', Character.MIN_VALUE, columnFieldMapper);
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
