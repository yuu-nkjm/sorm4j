package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a table mapping for a given class.
 *
 * <p>For example, by default, a class named {@code User} is mapped to a table named {@code USERS}.
 * However, {@code USERS} might be a reserved table name used by the RDBMS. In such cases, this
 * annotation can be used to explicitly map the {@code User} class to a different table name, such
 * as {@code MY_USERS}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrmTable {

  /** Name of the table mapped to the class. */
  String value();
}
