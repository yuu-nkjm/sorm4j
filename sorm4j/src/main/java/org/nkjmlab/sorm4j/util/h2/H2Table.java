package org.nkjmlab.sorm4j.util.h2;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvFunctions;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvReadSql;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;
import org.nkjmlab.sorm4j.util.table_def.TableWithDefinition;

@Experimental
public interface H2Table<T> extends TableWithDefinition<T> {

  /**
   * @param <T>
   * @param orm
   * @param valueType
   * @return
   */
  static <T> H2Table<T> create(Sorm orm, Class<T> valueType) {
    return new BasicH2Table<>(orm, valueType);
  }

  static <T> H2Table<T> create(Sorm orm, Class<T> valueType, String tableName) {
    return new BasicH2Table<>(
        orm, valueType, TableDefinition.builder(valueType, tableName).build());
  }

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
}
