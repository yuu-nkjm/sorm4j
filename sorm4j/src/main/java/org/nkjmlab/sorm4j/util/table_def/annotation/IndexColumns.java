package org.nkjmlab.sorm4j.util.table_def.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines multi-column index.
 *
 * <p>For example,
 *
 * <pre><code>
 * @IndexColumns({"user_id", "item_id"})
 *
 * </pre></code>
 */
@Repeatable(RepeatableIndexColumns.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexColumns {

  /** Name of the index */
  String[] value();
}
