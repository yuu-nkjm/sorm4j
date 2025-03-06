package org.nkjmlab.sorm4j.extension.h2.orm.table;

import java.io.File;

import org.nkjmlab.sorm4j.extension.h2.functions.system.CsvWrite;
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
}
