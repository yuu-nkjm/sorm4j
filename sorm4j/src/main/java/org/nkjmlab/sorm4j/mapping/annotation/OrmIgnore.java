package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a field or method should be ignored in ORM mapping.
 *
 * <p>This annotation is applied to a field or a method to indicate that it should be excluded from
 * database mapping. Fields marked with this annotation will not be mapped to any database column,
 * and methods will not be considered for getter or setter-based mapping.
 *
 * <p>Example usage:
 *
 * <pre>
 * CREATE TABLE players (name VARCHAR(255));
 * </pre>
 *
 * The following Java class maps the "name" column while ignoring the "password" field:
 *
 * <pre>
 * public class Player {
 *   public String name;
 *
 *   {@literal @}OrmIgnore
 *   public String password;
 * }
 * </pre>
 *
 * Methods can also be ignored to prevent them from being used in getter/setter-based mappings:
 *
 * <pre>
 * public class Player {
 *   private String name;
 *   private String password;
 *
 *   public String getName() {
 *       return name;
 *   }
 *
 *   {@literal @}OrmIgnore
 *   public String getPassword() {
 *       return "*****";
 *   }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface OrmIgnore {}
