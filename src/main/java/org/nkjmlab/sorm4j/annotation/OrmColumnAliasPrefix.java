package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a table name alias for join.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Experimental
public @interface OrmColumnAliasPrefix {

  /**
   * Name of the table mapped to the class.
   */
  String value();

}
