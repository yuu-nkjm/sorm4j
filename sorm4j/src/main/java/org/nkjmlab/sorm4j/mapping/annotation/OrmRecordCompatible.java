package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class follows a record-compatible design by defining a canonical constructor.
 *
 * <p>This annotation is intended for classes that are not records but have a constructor equivalent
 * to a record's canonical constructor. It is not required for record classes in Java 17 or later.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OrmRecordCompatible {}
