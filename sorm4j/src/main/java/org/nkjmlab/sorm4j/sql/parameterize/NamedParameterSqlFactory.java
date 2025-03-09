package org.nkjmlab.sorm4j.sql.parameterize;

import java.util.Map;

import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.internal.util.sql.binding.NamedParameterSqlParserImpl;

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
public interface NamedParameterSqlFactory extends ParameterizedSqlFactory {

  /**
   * Binds key-value pairs to named parameters in a SQL statement.
   *
   * @param keyValuePairOfNamedParameters
   * @return
   */
  NamedParameterSqlFactory bind(Map<String, Object> keyValuePairOfNamedParameters);

  /**
   * Binds a key-value pair to named parameters in a SQL statement.
   *
   * @param key
   * @param value
   * @return
   */
  NamedParameterSqlFactory bind(String key, Object value);

  /**
   * Binds a bean. The field names map to keys of parameter by {@link ColumnToFieldAccessorMapper}.
   *
   * @param bean
   * @return
   */
  NamedParameterSqlFactory bind(Object bean);

  /**
   * Creates {@link NamedParameterSqlFactory} object. the named parameters should have the given
   * prefix and suffix.
   *
   * @param sql
   * @param prefix
   * @param suffix
   * @param columnFieldMapper
   * @return
   */
  @Experimental
  static NamedParameterSqlFactory of(
      String sql, char prefix, char suffix, ColumnToFieldAccessorMapper columnFieldMapper) {
    return new NamedParameterSqlParserImpl(sql, prefix, suffix, columnFieldMapper);
  }

  /**
   * @param sql
   * @param columnFieldMapper
   * @return
   */
  @Experimental
  static NamedParameterSqlFactory of(String sql, ColumnToFieldAccessorMapper columnFieldMapper) {
    return of(sql, ':', Character.MIN_VALUE, columnFieldMapper);
  }

  /**
   * Creates {@link NamedParameterSqlFactory} object.
   *
   * @param sql
   * @return
   */
  static NamedParameterSqlFactory of(String sql) {
    return new NamedParameterSqlParserImpl(sql);
  }

  /**
   * Creates {@link NamedParameterSqlFactory} object with parameters.
   *
   * @param sql
   * @param namedParameters
   * @return
   */
  static ParameterizedSql create(String sql, Map<String, Object> namedParameters) {
    return NamedParameterSqlFactory.of(sql).bind(namedParameters).create();
  }
}
