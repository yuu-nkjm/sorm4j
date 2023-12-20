package org.nkjmlab.sorm4j.util.h2;

import java.io.File;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;
import org.nkjmlab.sorm4j.util.h2.functions.system.CsvWrite;
import org.nkjmlab.sorm4j.util.table_def.WithTableDefinition;

@Experimental
public interface H2Table<T> extends Table<T>, WithTableDefinition, H2Orm {

  /**
   * Write all rows to csv file.
   *
   * @param toFile
   * @return
   */
  default File writeCsv(File toFile) {
    return writeCsv(toFile, "select * from " + getTableName());
  }

  /**
   * Write selected rows to csv file.
   *
   * @param toFile
   * @param query
   * @return
   */
  default File writeCsv(File toFile, String query) {
    getOrm().executeUpdate("call " + CsvWrite.builder(toFile).query(query).build().getSql());
    return toFile;
  }
}
