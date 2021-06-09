package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a column mapping to a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OrmColumn {

  /**
   * Name of the column mapped to the field.
   */
  String value();
}
