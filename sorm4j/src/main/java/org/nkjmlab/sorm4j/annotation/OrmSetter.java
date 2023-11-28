package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Defines a setter method for mapping column to field. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OrmSetter {

  /** Name of the setter mapped to the field. */
  String value();
}
