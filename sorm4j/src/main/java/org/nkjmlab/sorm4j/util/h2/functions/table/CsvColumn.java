package org.nkjmlab.sorm4j.util.h2.functions.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;

/**
 * Mapping a csv column to a table column.
 *
 * <h5>Example.</h5>
 *
 * <pre>
 * <code>
 *   @OrmRecord
 * public static class Item {
 * &#64;CsvColumn("parsedatetime(delivery_date, 'y/MM/d')")
 * public LocalDate deliveryDate;
 * &#64;CsvColumn("`price/prices`")
 * public int price;
 * }
 * </code>
 * </pre>
 *
 * {@link BasicH2Table#getReadCsvWithHeaderSql(java.io.File, java.nio.charset.Charset, char)}
 * generates the following SQL statement.
 *
 * <pre>
 * <code>select parsedatetime(delivery_date, 'y/MM/d') as DELIVERY_DATE,`price/prices` as PRICE from csvread('file.csv',null,'charset=UTF-8 fieldSeparator='||char(9))</code>
 * </pre>
 */
@Experimental
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CsvColumn {

  String value();
}
