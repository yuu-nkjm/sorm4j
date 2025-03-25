package org.nkjmlab.sorm4j.extension.datatype.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Jackson-compatible JSON column.
 *
 * <p>This annotation indicates that the annotated class should be processed as a JSON column when
 * interacting with the database. It helps ORM frameworks recognize and handle JSON serialization
 * and deserialization correctly.
 *
 * <p><strong>Usage Example:</strong>
 *
 * <pre>{@code
 * @OrmJacksonColumn
 * public class MyJsonClass {
 *     private String name;
 *     private int age;
 * }
 * }</pre>
 *
 * <p><strong>Retention Policy:</strong> This annotation is retained at runtime, allowing
 * reflection-based processing.
 *
 * <p><strong>Target:</strong> This annotation can only be applied to classes ({@link
 * ElementType#TYPE}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OrmJacksonColumn {}
