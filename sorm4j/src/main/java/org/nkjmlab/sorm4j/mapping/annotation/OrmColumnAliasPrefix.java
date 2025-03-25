package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nkjmlab.sorm4j.Orm;

/**
 * Specifies a prefix for column alias mapping in ORM queries.
 *
 * <p>By default, column aliases use the pattern <code>TABLE_ALIAS_DOT_</code> as a prefix. For
 * example, in a SQL query that joins multiple tables, the default alias for a column named {@code
 * name} in the {@code players} table with alias {@code p} would be <code>p_dot_name</code>.
 *
 * <p>This annotation allows customization of the alias prefix for a class. When applied, it changes
 * how column names are mapped to fields. If a field named {@code name} is mapped to the database
 * column {@code name}, and the value of {@link OrmColumnAliasPrefix} is set to {@code p}, then the
 * column <code>p_dot_name</code> will also be mapped to the {@code name} field.
 *
 * <p>This is particularly useful for methods like {@link Orm#readTupleList} and {@link Orm#join}
 * where table joins generate prefixed column names.
 *
 * <p>Example usage:
 *
 * <pre>
 * {@literal @}OrmColumnAliasPrefix("p")
 * public class Player {
 *   private int id;
 *   private String name;
 * }
 *
 * {@literal @}OrmColumnAliasPrefix("c")
 * public class Customer {
 *   private int id;
 *   private String address;
 * }
 * </pre>
 *
 * <p>Example SQL query:
 *
 * <pre>
 * SELECT p.id AS p_dot_id, p.name AS p_dot_name,
 *        c.id AS c_dot_id, c.address AS c_dot_address
 * FROM players p
 * JOIN customer c ON p.id = c.id;
 * </pre>
 *
 * In this case, columns prefixed with <code>p_dot_</code> and <code>c_dot_</code> are mapped to the
 * corresponding fields in the {@code Player} and {@code Customer} classes. By using
 * {@code @OrmColumnAliasPrefix("c")}, an alternative alias mapping can be specified.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrmColumnAliasPrefix {

  /**
   * The prefix to use for column alias mapping.
   *
   * @return the alias prefix
   */
  String value();
}
