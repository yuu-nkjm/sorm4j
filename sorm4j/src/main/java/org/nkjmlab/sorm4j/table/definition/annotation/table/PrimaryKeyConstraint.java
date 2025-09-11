package org.nkjmlab.sorm4j.table.definition.annotation.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a multi-column primary key constraint for a table.
 *
 * <p>This annotation is used to define a primary key consisting of multiple columns. It should be
 * applied to a class representing a database table.
 *
 * <p>Example usage:
 *
 * <pre><code>
 * {@literal @}PrimaryKeyConstraint("user_id, item_id")
 * public class Order {
 *     private String userId;
 *     private String itemId;
 * }
 * </code></pre>
 *
 * <p>The specified column names should match the corresponding field names in the class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrimaryKeyConstraint {
  /**
   * Defines the column names that form the primary key. The column names should be specified as a
   * comma-separated string.
   *
   * @return the primary key column names
   */
  String[] value();
}
