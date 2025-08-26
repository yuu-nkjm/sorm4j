package org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.nkjmlab.sorm4j.internal.extension.h2.orm.table.definition.H2SimpleDefinedTable;

/**
 * Maps a CSV column to a corresponding table column in SQL queries.
 *
 * <p>This annotation is used to specify how a CSV column should be interpreted in an SQL query,
 * allowing custom expressions to be applied when reading data from a CSV file. It can be applied to
 * class fields or constructor parameters to define explicit mappings.
 *
 * <h5>Example Usage</h5>
 *
 * <pre>
 * <code>
 *   @OrmRecord
 *   public static class Item {
 *       // Maps the CSV column 'delivery_date' using a SQL date parsing function.
 *       @CsvColumn("parsedatetime(delivery_date, 'y/MM/d')")
 *       public LocalDate deliveryDate;
 *
 *       // Maps the CSV column 'price/prices' directly to the table column 'price'.
 *       @CsvColumn("`price/prices`")
 *       public int price;
 *   }
 * </code>
 * </pre>
 *
 * When used with {@link H2SimpleDefinedTable#getReadCsvWithHeaderSql(java.io.File,
 * java.nio.charset.Charset, char)}, the following SQL statement is generated:
 *
 * <pre>
 * <code>
 * SELECT parsedatetime(delivery_date, 'y/MM/d') AS DELIVERY_DATE, `price/prices` AS PRICE
 * FROM CSVREAD('file.csv', NULL, 'charset=UTF-8 fieldSeparator=' || CHAR(9))
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CsvColumnExpression {

  /**
   * Specifies the SQL expression or column name to be used for this field.
   *
   * @return The SQL representation of the mapped CSV column.
   */
  String value();
}
