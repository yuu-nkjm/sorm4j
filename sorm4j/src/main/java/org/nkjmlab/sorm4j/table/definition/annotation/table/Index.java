package org.nkjmlab.sorm4j.table.definition.annotation.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines index.
 *
 * <p>For example,
 *
 * <pre><code>
 * @Index({"user_id"})                 // single-column index
 * @Index({"user_id", "item_id"})      // composite  index
 * </pre></code>
 */
@Documented
@Repeatable(Indexes.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Index {

  /** Name of the index */
  String[] value();
}
