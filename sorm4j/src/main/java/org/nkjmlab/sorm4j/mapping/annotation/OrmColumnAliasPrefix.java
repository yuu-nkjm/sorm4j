package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nkjmlab.sorm4j.Orm;

/**
 * Defines a prefix for field alias. When "name" field mapped to "name" column and the value of
 * {@link OrmColumnAliasPrefix}} is "customer", "customername" column also mapped to "name" field.
 * It is mainly designed for {@link Orm#readTupleList} method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrmColumnAliasPrefix {

  /** Prefix for field alias. */
  String value();
}
