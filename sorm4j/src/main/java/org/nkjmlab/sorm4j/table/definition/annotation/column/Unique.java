package org.nkjmlab.sorm4j.table.definition.annotation.column;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field, constructor parameter, or record component parameter as having a unique
 * constraint.
 *
 * <p>This annotation is used to indicate that a field, a constructor parameter, or a record
 * component parameter should have a uniqueness constraint in the database table definition. It can
 * be applied to:
 *
 * <ul>
 *   <li>Fields in a class
 *   <li>Constructor parameters
 *   <li>Record component parameters
 * </ul>
 *
 * <p>Example usage with a class:
 *
 * <pre><code>
 * public class User {
 *     {@literal @}Unique
 *     private String email;
 *
 *     public User({@literal @}Unique String email) {
 *         this.email = email;
 *     }
 * }
 * </code></pre>
 *
 * <p>Example usage with a record:
 *
 * <pre><code>
 * public record User(
 *     {@literal @}Unique String email,
 *     String name
 * ) {}
 * </code></pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Unique {}
