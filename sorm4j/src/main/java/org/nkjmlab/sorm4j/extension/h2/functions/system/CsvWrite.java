package org.nkjmlab.sorm4j.extension.h2.functions.system;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.extension.h2.grammar.CsvOptions;
import org.nkjmlab.sorm4j.extension.h2.tools.csv.H2CsvWriter;
import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

/**
 * A wrapper for the H2 {@code CSVWRITE} function.
 *
 * <p>The {@code CSVWRITE} function writes the result of a SQL query into a CSV file. The target
 * file is overwritten if it already exists. The function supports various options such as character
 * set, field separator, and whether to include column headers. All options can be configured via
 * the nested {@link Builder} class.
 *
 * <p>By default:
 *
 * <ul>
 *   <li>The file is created in the current working directory if only a file name is specified.
 *   <li>The default charset is the system's default charset.
 *   <li>The default field separator is a comma (",").
 *   <li>The default line separator is the system's {@code line.separator} property.
 *   <li>NULL values are written as empty fields, unless configured otherwise.
 * </ul>
 *
 * <p>The return value of the generated SQL is the number of rows written to the CSV file. In some
 * environments, administrator rights are required to execute {@code CSVWRITE}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * CsvWrite csv = CsvWrite.builder(new File("data/test.csv"))
 *     .query("SELECT * FROM TEST")
 *     .charset("UTF-8")
 *     .fieldSeparator("|")
 *     .build();
 *
 * // Generates SQL like:
 * // CALL CSVWRITE('data/test.csv', 'SELECT * FROM TEST', 'charset=UTF-8 fieldSeparator=|');
 * }</pre>
 *
 * @see <a href="https://www.h2database.com/html/functions.html#csvwrite">H2 CSVWRITE</a>
 * @see H2CsvWriter
 */
public class CsvWrite {

  private final String sql;

  public CsvWrite(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  @Override
  public String toString() {
    return sql;
  }

  public static CsvWrite.Builder builder(File file) {
    return new CsvWrite.Builder().file(file);
  }

  public static CsvWrite.Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private File file;
    private String query;
    private final org.nkjmlab.sorm4j.extension.h2.grammar.CsvOptions.Builder csvOptionsBuilder =
        new CsvOptions.Builder();

    /**
     * Sets the SQL query string that will be executed to produce the result set for CSV writing.
     *
     * @param query the SQL query string to be written into the CSV file
     * @return this builder instance
     */
    public CsvWrite.Builder query(String query) {
      this.query = query;
      return this;
    }

    public CsvWrite.Builder sql(String sql) {
      return query(sql);
    }

    public CsvWrite.Builder file(File file) {
      this.file = file;
      return this;
    }

    public CsvWrite.Builder caseSensitiveColumnNames(boolean caseSensitiveColumnNames) {
      csvOptionsBuilder.caseSensitiveColumnNames(caseSensitiveColumnNames);
      return this;
    }

    public CsvWrite.Builder charset(String val) {
      csvOptionsBuilder.charset(val);
      return this;
    }

    public CsvWrite.Builder escape(String val) {
      csvOptionsBuilder.escape(val);
      return this;
    }

    public CsvWrite.Builder fieldDelimiter(String val) {
      csvOptionsBuilder.fieldDelimiter(val);
      return this;
    }

    public CsvWrite.Builder fieldSeparator(String val) {
      csvOptionsBuilder.fieldSeparator(val);
      return this;
    }

    public CsvWrite.Builder lineComment(String val) {
      csvOptionsBuilder.lineComment(val);
      return this;
    }

    public CsvWrite.Builder lineSeparator(String val) {
      csvOptionsBuilder.lineSeparator(val);
      return this;
    }

    public CsvWrite.Builder nullString(String val) {
      csvOptionsBuilder.nullString(val);
      return this;
    }

    public CsvWrite.Builder quotedNulls(boolean val) {
      csvOptionsBuilder.quotedNulls(val);
      return this;
    }

    public CsvWrite.Builder preserveWhitespace(boolean val) {
      csvOptionsBuilder.preserveWhitespace(val);
      return this;
    }

    public CsvWrite.Builder writeColumnHeader(boolean val) {
      csvOptionsBuilder.writeColumnHeader(val);
      return this;
    }

    public CsvWrite build() {
      CsvOptions csvOptions = csvOptionsBuilder.build();

      List<String> l =
          Stream.of(
                  SqlStringUtils.quote(file.getAbsolutePath().toString()),
                  SqlStringUtils.quote(query),
                  csvOptions == null || csvOptions.getSql() == null
                      ? null
                      : "stringdecode(" + SqlStringUtils.quote(csvOptions.getSql()) + ")")
              .toList();

      return new CsvWrite("csvwrite(" + String.join(", ", l) + ")");
    }
  }
}
