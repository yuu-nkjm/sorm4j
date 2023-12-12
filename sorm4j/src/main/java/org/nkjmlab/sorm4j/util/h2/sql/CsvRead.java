package org.nkjmlab.sorm4j.util.h2.sql;

import static org.nkjmlab.sorm4j.util.h2.internal.H2Keyword.wrapSingleQuote;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CsvRead {

  private final String sql;

  public CsvRead(String sql) {
    this.sql = sql;
  }

  @Override
  public String toString() {
    return sql;
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

  public static CsvRead.Builder builderForCsvWithHeader(File csvFile) {
    return new CsvRead.Builder().csvFile(csvFile);
  }

  /**
   * @param csvFile
   * @param csvColumnsCount count of columns in csv file.
   * @return
   */
  public static CsvRead.Builder builderForCsvWithoutHeader(File csvFile, int csvColumnsCount) {
    return new CsvRead.Builder()
        .csvFile(csvFile)
        .columns(IntStream.range(0, csvColumnsCount).mapToObj(i -> "COL_" + i).toList());
  }

  static CsvRead.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, String> options = new TreeMap<>();
    private File csvFile;
    private List<String> columns;

    public CsvRead.Builder columns(List<String> columns) {
      this.columns = columns;
      return this;
    }

    public CsvRead.Builder csvFile(File csvFile) {
      this.csvFile = csvFile;
      return this;
    }

    public CsvRead.Builder fieldSeparator(String val) {
      options.put("fieldSeparator", val);
      return this;
    }

    public CsvRead.Builder fieldDelimiter(String val) {
      options.put("fieldDelimiter", val);
      return this;
    }

    public CsvRead.Builder charset(String val) {
      options.put("charset", val);
      return this;
    }

    public CsvRead build() {
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

      return new CsvRead("csvread (" + String.join(", ", l) + ")");
    }
  }
}
