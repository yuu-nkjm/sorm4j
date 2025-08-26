package org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or parameter to be ignore when reading CSV data in the H2 database.
 *
 * <p>This annotation is used in conjunction with the {@code SlectCsvReadSql} function of H2. When
 * applied to a field or parameter, the corresponding column in the CSV file will be ignored in the
 * generated SQL query.
 *
 * <h5>Example Usage</h5>
 *
 * <pre>
 * <code>
 *   public static class Item {
 *       public String name;
 *
 *       // This column will be skipped when reading CSV data
 *       @CsvIgnoreColumn
 *       public int internalId;
 *   }
 * </code>
 * </pre>
 *
 * The generated SQL query will exclude the {@code internalId} field:
 *
 * <pre>
 * <code>
 * SELECT name FROM CSVREAD('file.csv', NULL, 'charset=UTF-8')
 * </code>
 * </pre>
 *
 * @author nkjm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CsvIgnore {}
