package org.nkjmlab.sorm4j.extension.h2.orm.table;

import java.io.File;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.h2.functions.system.CsvWrite;
import org.nkjmlab.sorm4j.internal.extension.h2.orm.table.H2SimpleTable;
import org.nkjmlab.sorm4j.table.orm.Table;

public interface H2Table<T> extends Table<T> {
  /**
   * Write all rows to csv file.
   *
   * @param toFile
   * @return
   */
  default void writeCsv(File toFile) {
    writeCsv(CsvWrite.builder(toFile).query("select * from " + getTableName()).build());
  }

  /**
   * Write selected rows to csv file.
   *
   * @param csvWrite
   * @return
   */
  default void writeCsv(CsvWrite csvWrite) {
    getOrm().executeUpdate("call " + csvWrite.getSql());
  }

  public static <T> H2Table<T> of(Sorm orm, Class<T> valueType) {
    return new H2SimpleTable<>(orm, valueType);
  }

  public static <T> H2Table<T> of(Sorm orm, Class<T> valueType, String tableName) {
    return new H2SimpleTable<>(orm, valueType, tableName);
  }
}
