package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a getter method that maps a field to a database column.
 *
 * <p>This annotation is applied to a getter method to define the corresponding column name in the
 * database. It is useful when the method name does not directly match the column name.
 *
 * <pre>
 * CREATE TABLE players (name VARCHAR(255));
 * </pre>
 *
 * Then, the following Java class can be used to map the "name" column to the "userName" field using
 * a getter method:
 *
 * <pre>
 * public class Player {
 *   private String userName;
 *
 *   {@literal @}OrmGetter("name")
 *   public String getUserName() {
 *       return userName;
 *   }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OrmGetter {

  /**
   * The name of the database column that corresponds to the annotated getter method.
   *
   * @return the column name
   */
  String value();
}
