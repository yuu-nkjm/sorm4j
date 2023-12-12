package org.nkjmlab.sorm4j.util.h2;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;
import org.nkjmlab.sorm4j.util.h2.internal.H2Keyword;
import org.nkjmlab.sorm4j.util.h2.sql.CsvRead;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvFunctions;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvReadSql;

@Experimental
public interface H2Table<T> extends Table<T>, H2Orm {

  default H2CsvReadSql.Builder csvReadSqlBuilder(File csvFile) {
    return H2CsvReadSql.builder(csvFile, getValueType());
  }

  default String getReadCsvWithHeaderSql(
      File csvFile, Charset charset, char fieldSeparator, Character fieldDelimiter) {
    H2CsvReadSql.Builder builder =
        csvReadSqlBuilder(csvFile)
            .setCharset(charset)
            .setFieldSeparator(fieldSeparator)
            .setFieldDelimiter(fieldDelimiter);
    return builder.build().getCsvReadAndSelectSql();
  }

  default List<T> readCsvWithHeader(File csvFile) {
    return readCsvWithHeader(csvFile, StandardCharsets.UTF_8, ',', null);
  }

  default List<T> readCsvWithHeader(
      File csvFile, Charset charset, char fieldSeparator, Character fieldDelimiter) {
    try {
      return getOrm()
          .readList(
              getValueType(),
              getReadCsvWithHeaderSql(csvFile, charset, fieldSeparator, fieldDelimiter));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Error occurs in: "
              + getReadCsvWithHeaderSql(csvFile, charset, fieldSeparator, fieldDelimiter),
          e);
    }
  }

  default File writeCsv(File toFile) {
    return writeCsv(toFile, "select * from " + getTableName());
  }

  default File writeCsv(File toFile, String selectSql) {
    return writeCsv(toFile, selectSql, StandardCharsets.UTF_8, ',', null);
  }

  default File writeCsv(
      File toFile,
      String selectSql,
      Charset charset,
      char fieldSeparator,
      Character fieldDelimiter) {
    getOrm()
        .executeUpdate(
            H2CsvFunctions.getCallCsvWriteSql(
                toFile, selectSql, charset, fieldSeparator, fieldDelimiter));
    return toFile;
  }

  default void scriptTableTo(File destFile, boolean includeDrop) {
    getOrm()
        .execute(
            String.join(
                " ",
                "script",
                H2Keyword.drop(includeDrop),
                "to",
                H2Keyword.wrapSingleQuote(destFile.getAbsolutePath()),
                "table",
                getTableName()));
  }

  default void scriptTableTo(File destFile, boolean includeDrop, String password) {
    getOrm()
        .execute(
            String.join(
                " ",
                "script",
                H2Keyword.drop(includeDrop),
                "to",
                H2Keyword.wrapSingleQuote(destFile.getAbsolutePath()),
                H2Keyword.scriptCompressionEncryption(password),
                "table",
                getTableName()));
  }

  default int insertCsv(CsvRead csvRead) {
    return getOrm().executeUpdate("insert into " + getTableName() + " select * from " + csvRead);
  }
}
