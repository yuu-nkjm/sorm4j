package org.nkjmlab.sorm4j.table.definition.annotation.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a unique constraint on one or more columns in a database table definition.
 *
 * <p>This annotation is used to define a unique constraint at the table level, ensuring that the
 * specified column or combination of columns contains only unique values across all rows. It should
 * be applied to a class representing a database table.
 *
 * <p>This annotation is repeatable, allowing multiple unique constraints to be specified for
 * different column combinations within the same table.
 *
 * <p>Example usage:
 *
 * <pre><code>
 * {@literal @}UniqueConstraint({"email"})
 * {@literal @}UniqueConstraint({"user_id", "item_id"})
 * public class User {
 *     private String email;
 *     private String userId;
 *     private String itemId;
 * }
 * </code></pre>
 *
 * <p>Alternatively, multiple constraints can be grouped using {@link UniqueConstraints}:
 *
 * <pre><code>
 * {@literal @}RepeatableUniqueConstraint({
 *     {@literal @}UniqueConstraint({"email"}),
 *     {@literal @}UniqueConstraint({"user_id", "item_id"})
 * })
 * public class User {
 *     private String email;
 *     private String userId;
 *     private String itemId;
 * }
 * </code></pre>
 */
@Documented
@Repeatable(UniqueConstraints.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UniqueConstraint {
  /**
   * Defines the column names that form the unique constraint. The column names should be specified
   * as an array of strings.
   *
   * @return the column names that must be unique
   */
  String[] value();
}
