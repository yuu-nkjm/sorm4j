package org.nkjmlab.sorm4j.util.h2;

import java.io.File;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.TableMappedOrm;
import org.nkjmlab.sorm4j.util.h2.functions.system.CsvWrite;
import org.nkjmlab.sorm4j.util.table_def.WithTableDefinition;

@Experimental
public interface H2TableMappedOrm<T> extends TableMappedOrm<T>, WithTableDefinition, H2Orm {

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
