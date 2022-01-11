package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OrmConstructor can be associated with constructor parameters and specify the names of the column.
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
