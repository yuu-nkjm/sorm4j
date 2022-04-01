package org.nkjmlab.sorm4j.util.h2;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvFunctions;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvReadSql;
import org.nkjmlab.sorm4j.util.table_def.TableWithDefinition;

@Experimental
public interface H2Table<T> extends TableWithDefinition<T> {

  default H2CsvReadSql.Builder csvReadSqlBuilder(File csvFile) {
    return H2CsvReadSql.builder(csvFile, getValueType());
  }

  default String getReadCsvWithHeaderSql(File csvFile, Charset charset, char separator) {
    H2CsvReadSql.Builder builder =
        csvReadSqlBuilder(csvFile).setCharset(charset).setFieldSeparator(separator);
    return builder.build().getCsvReadAndSelectSql();
  }

  default List<T> readCsvWithHeader(File csvFile) {
    return readCsvWithHeader(csvFile, StandardCharsets.UTF_8, ',');
  }

  default List<T> readCsvWithHeader(File csvFile, Charset charset, char separator) {
    try {
      return getOrm().readList(getValueType(),
          getReadCsvWithHeaderSql(csvFile, charset, separator));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Error occurs in: " + getReadCsvWithHeaderSql(csvFile, charset, separator), e);
    }
  }

  default File writeCsv(File toFile) {
    return writeCsv(toFile, "select * from " + getTableName());
  }

  default File writeCsv(File toFile, String selectSql) {
    getOrm().executeUpdate(
        H2CsvFunctions.getCallCsvWriteSql(toFile, selectSql, StandardCharsets.UTF_8, ','));
    return toFile;
  }
}
