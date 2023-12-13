package org.nkjmlab.sorm4j.util.h2.sql;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class CsvReadSql {

  private final String sql;

  public CsvReadSql(String sql) {
    this.sql = sql;
  }

  @Override
  public String toString() {
    return sql;
  }

  private static String wrapSingleQuote(Object str) {
    return str == null ? null : "'" + str + "'";
  }

  private static String escape(String s) {
    return s.replace("\\", "\\\\")
        .replace("\b", "\\b")
        .replace("\t", "\\t")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\f", "\\f")
        .replace("\'", "\\'")
        .replace("\"", "\\\"");
  }

  public static CsvReadSql.Builder builderForCsvWithHeader(File csvFile) {
    return new CsvReadSql.Builder().csvFile(csvFile);
  }

  /**
   * @param csvFile
   * @param csvColumnsCount count of columns in csv file.
   * @return
   */
  public static CsvReadSql.Builder builderForCsvWithoutHeader(File csvFile, int csvColumnsCount) {
    return new CsvReadSql.Builder()
        .csvFile(csvFile)
        .columns(IntStream.range(0, csvColumnsCount).mapToObj(i -> "COL_" + i).toList());
  }

  static CsvReadSql.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, String> options = new TreeMap<>();
    private File csvFile;
    private List<String> columns;

    public CsvReadSql.Builder columns(List<String> columns) {
      this.columns = columns;
      return this;
    }

    public CsvReadSql.Builder csvFile(File csvFile) {
      this.csvFile = csvFile;
      return this;
    }

    public CsvReadSql.Builder fieldSeparator(String val) {
      options.put("fieldSeparator", val);
      return this;
    }

    public CsvReadSql.Builder fieldDelimiter(String val) {
      options.put("fieldDelimiter", val);
      return this;
    }

    public CsvReadSql.Builder charset(String val) {
      options.put("charset", val);
      return this;
    }

    public CsvReadSql build() {
      String columnsString =
          columns == null
              ? null
              : String.join(options.getOrDefault("fieldSeparator", ","), columns);

      String optionsString =
          options.size() == 0
              ? null
              : String.join(
                  " ",
                  options.entrySet().stream()
                      .map(en -> en.getKey() + "=" + escape(en.getValue()))
                      .toList());
      List<String> l =
          Stream.of(
                  wrapSingleQuote(csvFile.getAbsolutePath().toString()),
                  columnsString == null ? null : wrapSingleQuote(columnsString),
                  optionsString == null
                      ? null
                      : "stringdecode(" + wrapSingleQuote(optionsString) + ")")
              .toList();

      return new CsvReadSql("csvread (" + String.join(", ", l) + ")");
    }
  }
}
