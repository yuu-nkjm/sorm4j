package org.nkjmlab.sorm4j.extension.h2.functions.system;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.extension.h2.grammar.CsvOptions;
import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

/** <a href="https://www.h2database.com/html/functions.html#csvwrite">Functions</a> */
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

    public CsvWrite.Builder query(String query) {
      this.query = query;
      return this;
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
