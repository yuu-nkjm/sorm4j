package org.nkjmlab.sorm4j.util.h2.functions.table;

import static org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils.escapeJavaString;
import static org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils.wrapSingleQuote;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.grammar.CsvOptions;

@Experimental
public class CsvRead {

  private final String sql;
  private final List<String> csvColumns;

  public CsvRead(String sql, List<String> csvColumns) {
    this.sql = sql;
    this.csvColumns = csvColumns;
  }

  public String getSql() {
    return sql;
  }

  @Override
  public String toString() {
    return sql;
  }

  public static CsvRead.Builder builderForCsvWithHeader(File csvFile) {
    return new CsvRead.Builder().file(csvFile);
  }

  public static Builder builderForCsvWithoutHeader(File csvFile, List<String> csvColumns) {
    return new CsvRead.Builder().file(csvFile).columns(csvColumns);
  }

  /**
   * @param csvFile
   * @param csvColumnsCount count of columns in csv file.
   * @return
   */
  public static CsvRead.Builder builderForCsvWithoutHeader(File csvFile, int csvColumnsCount) {
    return builderForCsvWithoutHeader(
        csvFile, IntStream.range(0, csvColumnsCount).mapToObj(i -> "COL_" + i).toList());
  }

  static CsvRead.Builder builder() {
    return new Builder();
  }

  public List<String> getCsvColumns() {
    return csvColumns;
  }

  public static class Builder {

    private File file;
    private List<String> columns;
    private final org.nkjmlab.sorm4j.util.h2.grammar.CsvOptions.Builder csvOptionsBuilder =
        new CsvOptions.Builder();

    public CsvRead.Builder columns(List<String> columns) {
      this.columns = columns;
      return this;
    }

    public CsvRead.Builder file(File file) {
      this.file = file;
      return this;
    }

    public CsvRead.Builder caseSensitiveColumnNames(String val) {
      csvOptionsBuilder.caseSensitiveColumnNames(val);
      return this;
    }

    public CsvRead.Builder charset(String val) {
      csvOptionsBuilder.charset(val);
      return this;
    }

    public CsvRead.Builder escape(String val) {
      csvOptionsBuilder.escape(val);
      return this;
    }

    public CsvRead.Builder fieldDelimiter(String val) {
      csvOptionsBuilder.fieldDelimiter(val);
      return this;
    }

    public CsvRead.Builder fieldSeparator(String val) {
      csvOptionsBuilder.fieldSeparator(val);
      return this;
    }

    public CsvRead.Builder lineComment(String val) {
      csvOptionsBuilder.lineComment(val);
      return this;
    }

    public CsvRead.Builder lineSeparator(String val) {
      csvOptionsBuilder.lineSeparator(val);
      return this;
    }

    public CsvRead.Builder nullString(String val) {
      csvOptionsBuilder.nullString(val);
      return this;
    }

    public CsvRead.Builder quotedNulls(String val) {
      csvOptionsBuilder.quotedNulls(val);
      return this;
    }

    public CsvRead.Builder preserveWhitespace(String val) {
      csvOptionsBuilder.preserveWhitespace(val);
      return this;
    }

    public CsvRead.Builder writeColumnHeader(String val) {
      csvOptionsBuilder.writeColumnHeader(val);
      return this;
    }

    public CsvRead build() {
      CsvOptions csvOptions = csvOptionsBuilder.build();

      String columnsString =
          columns == null ? null : String.join(csvOptions.getFieldSeparator(), columns);

      List<String> l =
          Stream.of(
                  wrapSingleQuote(file.getAbsolutePath().toString()),
                  columnsString == null ? null : wrapSingleQuote(columnsString),
                  csvOptions == null
                      ? null
                      : "stringdecode(" + wrapSingleQuote(csvOptions.getSql()) + ")")
              .toList();

      return new CsvRead("csvread(" + String.join(", ", l) + ")", columns);
    }
  }
}
