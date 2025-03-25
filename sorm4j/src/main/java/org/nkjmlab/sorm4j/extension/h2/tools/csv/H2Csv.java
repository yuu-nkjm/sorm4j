package org.nkjmlab.sorm4j.extension.h2.tools.csv;

import org.h2.tools.Csv;

public interface H2Csv {

  /**
   * A builder class for creating an {@link H2Csv} instance with customized settings.
   *
   * <p>This class allows for a fluent API to configure various CSV properties before building the
   * final {@link H2Csv} object.
   *
   * <pre>
   * H2Csv csvConfig = new H2Csv.Builder()
   *     .fieldSeparatorWrite(";")
   *     .caseSensitiveColumnNames(true)
   *     .build();
   * </pre>
   */
  abstract static class Builder<T, B extends Builder<T, B>> {
    protected final Csv csv = new Csv();

    public Builder<T, B> caseSensitiveColumnNames(boolean caseSensitiveColumnNames) {
      csv.setCaseSensitiveColumnNames(caseSensitiveColumnNames);
      return this;
    }

    public Builder<T, B> fieldSeparatorWrite(String fieldSeparatorWrite) {
      csv.setFieldSeparatorWrite(fieldSeparatorWrite);
      return this;
    }

    public Builder<T, B> fieldSeparatorRead(char fieldSeparatorRead) {
      csv.setFieldSeparatorRead(fieldSeparatorRead);
      return this;
    }

    public Builder<T, B> lineCommentCharacter(char lineComment) {
      csv.setLineCommentCharacter(lineComment);
      return this;
    }

    public Builder<T, B> fieldDelimiter(char fieldDelimiter) {
      csv.setFieldDelimiter(fieldDelimiter);
      return this;
    }

    public Builder<T, B> escapeCharacter(char escapeCharacter) {
      csv.setEscapeCharacter(escapeCharacter);
      return this;
    }

    public Builder<T, B> lineSeparator(String lineSeparator) {
      csv.setLineSeparator(lineSeparator);
      return this;
    }

    public Builder<T, B> quotedNulls(boolean quotedNulls) {
      csv.setQuotedNulls(quotedNulls);
      return this;
    }

    public Builder<T, B> nullString(String nullString) {
      csv.setNullString(nullString);
      return this;
    }

    public Builder<T, B> preserveWhitespace(boolean preserveWhitespace) {
      csv.setPreserveWhitespace(preserveWhitespace);
      return this;
    }

    public Builder<T, B> writeColumnHeader(boolean writeColumnHeader) {
      csv.setWriteColumnHeader(writeColumnHeader);
      return this;
    }

    public abstract T build();
  }
}
