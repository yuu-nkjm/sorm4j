package org.nkjmlab.sorm4j.table.definition.annotation.table;

import java.lang.annotation.*;

/** Container annotation for repeatable {@link CheckConstraint}. */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckConstraints {
  CheckConstraint[] value();
}
