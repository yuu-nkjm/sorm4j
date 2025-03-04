package org.nkjmlab.sorm4j.util.h2.table;

import java.io.File;

import org.nkjmlab.sorm4j.table.TableOrm;
import org.nkjmlab.sorm4j.util.h2.functions.system.CsvWrite;

public interface H2TableOrm<T> extends TableOrm<T> {

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
