package org.nkjmlab.sorm4j.util.table_def.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines multi-column primary key.
 *
 * <p>For example,
 *
 * <pre><code>
 * @PrimaryKeyColumns({"user_id", "item_id"})
 *
 * </pre></code>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrimaryKeyColumns {
  String[] value();
}
