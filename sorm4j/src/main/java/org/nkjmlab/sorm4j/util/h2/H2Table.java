package org.nkjmlab.sorm4j.util.h2;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;
import org.nkjmlab.sorm4j.util.h2.functions.system.SystemFunctions;
import org.nkjmlab.sorm4j.util.h2.functions.table.CsvReadSql;
import org.nkjmlab.sorm4j.util.h2.internal.H2Keyword;

@Experimental
public interface H2Table<T> extends Table<T>, H2Orm {


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
            SystemFunctions.getCallCsvWriteSql(
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
                wrapSingleQuote(destFile.getAbsolutePath()),
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
                wrapSingleQuote(destFile.getAbsolutePath()),
                H2Keyword.scriptCompressionEncryption(password),
                "table",
                getTableName()));
  }

  default int insertCsv(CsvReadSql csvRead) {
    return getOrm().executeUpdate("insert into " + getTableName() + " select * from " + csvRead);
  }

  private static String wrapSingleQuote(Object str) {
    return str == null ? null : "'" + str + "'";
  }
}
