package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the mapping between a field and a database column.
 *
 * <p>This annotation is applied to a field to define the corresponding column name in the database.
 * It is useful when the field name differs from the column name.
 *
 * <pre>
 * CREATE TABLE players (name VARCHAR(255));
 * </pre>
 *
 * Then, the following Java class can be used to map the "name" column to the "userName" field:
 *
 * <pre>
 * public class Player {
 *   {@literal @}OrmColumn("name")
 *   public String userName;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OrmColumn {

  /**
   * The name of the database column that corresponds to the annotated field.
   *
   * @return the column name
   */
  String value();
}
