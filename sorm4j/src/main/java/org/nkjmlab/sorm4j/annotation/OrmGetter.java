package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Defines a getter method for mapping column to field. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OrmGetter {

  /** Name of the getter mapped to the field. */
  String value();
}
