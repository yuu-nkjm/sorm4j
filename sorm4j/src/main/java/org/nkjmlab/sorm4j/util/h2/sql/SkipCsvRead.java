package org.nkjmlab.sorm4j.util.h2.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The csvread function of the H2 database skips columns based on their annotations.
 *
 * @author nkjm
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface SkipCsvRead {

}
