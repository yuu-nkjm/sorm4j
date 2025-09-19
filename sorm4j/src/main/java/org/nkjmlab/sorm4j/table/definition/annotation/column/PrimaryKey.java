package org.nkjmlab.sorm4j.table.definition.annotation.column;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field, constructor parameter, or record component parameter as part of the primary key.
 *
 * <p>This annotation is used to indicate that a field, a constructor parameter, or a record
 * component parameter represents a primary key in a database table. It can be applied to:
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
 *     {@literal @}PrimaryKey
 *     private String userId;
 *
 *     public User({@literal @}PrimaryKey String userId) {
 *         this.userId = userId;
 *     }
 * }
 * </code></pre>
 *
 * <p>Example usage with a record:
 *
 * <pre><code>
 * public record User(
 *     {@literal @}PrimaryKey String userId,
 *     String name
 * ) {}
 * </code></pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface PrimaryKey {}
