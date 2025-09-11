package org.nkjmlab.sorm4j.table.definition.annotation.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a {@code CHECK} constraint for a table.
 *
 * <p>This annotation can be applied to a class that represents a database table in order to declare
 * a table-level {@code CHECK} constraint. The value should be a valid SQL expression that evaluates
 * to {@code TRUE} for all rows of the table.
 *
 * <p>For example:
 *
 * <pre>{@code
 * @Check("age >= 0")
 * public class Person {
 *   private int age;
 * }
 * }</pre>
 *
 * <p>Multiple {@code CHECK} constraints can be defined by using the repeatable form:
 *
 * <pre>{@code
 * @Check("score >= 0")
 * @Check("score <= 100")
 * public class ExamResult {
 *   private int score;
 * }
 * }</pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CheckConstraint {
  String value();
}
