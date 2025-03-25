package org.nkjmlab.sorm4j.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated constructor is compatible with a Java {@code record}'s canonical
 * constructor.
 *
 * <p>This annotation is used in SORM frameworks to designate a constructor that should be used when
 * mapping database records to Java objects. It signifies that the constructor's parameters must
 * match the class's field definitions in both type and order.
 *
 * <h3>Usage</h3>
 *
 * <pre>{@code
 * public class User {
 *   private final String name;
 *   private final int age;
 *
 *   @OrmRecordCompatibleConstructor
 *   public User(String name, int age) {
 *       this.name = name;
 *       this.age = age;
 *   }
 * }
 * }</pre>
 *
 * <h3>Constraints</h3>
 *
 * <ul>
 *   <li>The parameter types and names must align exactly with the declared fields of the class.
 *   <li>The order of parameters must strictly follow the field declaration order.
 *   <li>The constructor must not be overloaded with different parameter orders.
 * </ul>
 *
 * <p>This annotation is particularly useful when working with immutable objects that are not
 * declared as {@code record} but follow the same initialization constraints.
 *
 * <h3>Retention & Target</h3>
 *
 * <ul>
 *   <li><b>Retention:</b> {@code RUNTIME} - The annotation is available at runtime for
 *       reflection-based ORM mapping.
 *   <li><b>Target:</b> {@code CONSTRUCTOR} - The annotation can only be applied to constructors.
 * </ul>
 *
 * @author nkjm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface OrmRecordCompatibleConstructor {}
