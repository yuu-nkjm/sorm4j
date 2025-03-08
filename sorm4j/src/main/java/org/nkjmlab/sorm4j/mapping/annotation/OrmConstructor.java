package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a constructor should be used for object mapping in ORM and defines the
 * corresponding column names for its parameters.
 *
 * <p>This annotation is applied to a constructor to indicate that it should be used for mapping
 * database records to objects. The {@code value} attribute specifies the names of the database
 * columns that correspond to the constructor parameters.
 *
 * <p>Example usage:
 *
 * <pre>
 * public class Guest {
 *   private final int id;
 *   private final String name;
 *   private final String address;
 *
 *   {@literal @}OrmConstructor({"id", "name", "address"})
 *   public Guest(int id, String name, String address) {
 *       this.id = id;
 *       this.name = name;
 *       this.address = address;
 *   }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface OrmConstructor {

  /**
   * The names of the database columns that correspond to the constructor parameters. The order of
   * column names should match the order of constructor parameters.
   *
   * @return the column names
   */
  String[] value();
}
