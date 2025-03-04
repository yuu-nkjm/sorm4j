package org.nkjmlab.sorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a record for mapping. This annotation is not necessary in Java 17 or later. It is
 * retained for backward compatibility.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Deprecated
public @interface OrmRecord {}
