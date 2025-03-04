package org.nkjmlab.sorm4j.util.h2.grammar;

import java.util.Map;
import java.util.TreeMap;

import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

public class CsvOptions {

  private final String sql;
  private final String fieldSeparator;

  public CsvOptions(String sql, String fieldSeparator) {
    this.sql = sql;
    this.fieldSeparator = fieldSeparator;
  }

  public String getSql() {
    return sql;
  }

  public String getFieldSeparator() {
    return fieldSeparator;
  }

  public Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, String> csvOptions = new TreeMap<>();

    /**
     * caseSensitiveColumnNames (true or false; disabled by default),
     *
     * @param val
     * @return
     */
    public Builder caseSensitiveColumnNames(String val) {
      return putCsvOption("caseSensitiveColumnNames", val);
    }

    /**
     * charset (for example 'UTF-8'),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder charset(String val) {
      return putCsvOption("charset", val);
    }

    /**
     * escape (the character that escapes the field delimiter),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder escape(String val) {
      return putCsvOption("escape", val);
    }

    /**
     * fieldDelimiter (a double quote by default),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder fieldDelimiter(String val) {
      return putCsvOption("fieldDelimiter", val);
    }
    /**
     * fieldSeparator (a comma by default),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder fieldSeparator(String val) {
      return putCsvOption("fieldSeparator", val);
    }

    /**
     * lineComment (disabled by default),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder lineComment(String val) {
      return putCsvOption("lineComment", val);
    }

    /**
     * lineSeparator (the line separator used for writing; ignored for reading),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder lineSeparator(String val) {
      return putCsvOption("lineSeparator", val);
    }

    /**
     * null Support reading existing CSV files that contain explicit null delimiters. Note that an
     * empty, unquoted values are also treated as null.
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder nullString(String val) {
      return putCsvOption("null", val);
    }

    /**
     * quotedNulls (quotes the nullString. true of false; disabled by default),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder quotedNulls(String val) {
      return putCsvOption("quotedNulls", val);
    }
    /**
     * preserveWhitespace (true or false; disabled by default),
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder preserveWhitespace(String val) {
      return putCsvOption("preserveWhitespace", val);
    }
    /**
     * writeColumnHeader (true or false; enabled by default).
     *
     * @param val
     * @return
     */
    public CsvOptions.Builder writeColumnHeader(String val) {
      return putCsvOption("writeColumnHeader", val);
    }

    private Builder putCsvOption(String key, String val) {
      csvOptions.put(key, val);
      return this;
    }

    public CsvOptions build() {

      String optionsString =
          csvOptions.size() == 0
              ? null
              : String.join(
                  " ",
                  csvOptions.entrySet().stream()
                      .map(en -> en.getKey() + "=" + SqlStringUtils.escapeJavaString(en.getValue()))
                      .toList());

      return new CsvOptions(optionsString, csvOptions.getOrDefault("fieldSeparator", ","));
    }
  }
}
