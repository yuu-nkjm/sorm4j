package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines constructor for mapping.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
@Experimental
public @interface OrmConstructor {

  /**
   * Name of the columns.
   */
  String[] value();

}
