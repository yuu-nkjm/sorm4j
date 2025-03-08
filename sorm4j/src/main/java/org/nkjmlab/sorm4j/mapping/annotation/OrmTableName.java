package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the database table name for a given class.
 *
 * <p>By default, the table name is inferred from the class name using the following rules:
 *
 * <ul>
 *   <li>If the class has an {@link OrmTableName} annotation, its value is used as the table name.
 *   <li>Otherwise, the class name is converted to a canonical form (upper snake case).
 *   <li>The following variations are considered as table name candidates, in order:
 *       <ul>
 *         <li>The canonical form of the class name (e.g., {@code OrderItem => ORDER_ITEM}).
 *         <li>The canonical form with "S" appended (e.g., {@code OrderItem => ORDER_ITEMS}).
 *         <li>The canonical form with "ES" appended if needed (e.g., {@code Address => ADDRESSES}).
 *         <li>If the class name ends with "Y", the suffix is replaced with "IES" (e.g., {@code
 *             Category => CATEGORIES}).
 *       </ul>
 * </ul>
 *
 * <p>If the automatically inferred table name does not match the actual table name in the database,
 * this annotation can be used to explicitly specify the table name.
 *
 * <p>Example usage:
 *
 * <pre>
 * {@literal @}OrmTableName("MY_USERS")
 * public class User {
 *   private int id;
 *   private String name;
 * }
 * </pre>
 *
 * In this case, the {@code User} class is explicitly mapped to the {@code MY_USERS} table instead
 * of the default candidates such as {@code USER}, {@code USERS}, or {@code USERES}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrmTableName {

  /**
   * The name of the database table that corresponds to the annotated class.
   *
   * @return the table name
   */
  String value();
}
