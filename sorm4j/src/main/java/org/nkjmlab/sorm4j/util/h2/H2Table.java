package org.nkjmlab.sorm4j.util.h2;

import java.io.File;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;
import org.nkjmlab.sorm4j.util.h2.commands.ScriptSql;
import org.nkjmlab.sorm4j.util.h2.functions.system.CsvWrite;
import org.nkjmlab.sorm4j.util.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.util.h2.grammar.ScriptCompressionEncryption;

@Experimental
public interface H2Table<T> extends Table<T>, H2Orm {

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
   * @param selectSql
   * @return
   */
  default File writeCsv(File toFile, String selectSql) {
    getOrm().executeUpdate("call " + CsvWrite.builder(toFile).query(selectSql).build().getSql());
    return toFile;
  }

  default void scriptTableTo(File destFile, boolean includeDrop) {
    String sql =
        ScriptSql.builder()
            .drop(includeDrop)
            .to(destFile)
            .addTable(getTableName())
            .build()
            .getSql();
    getOrm().execute(sql);
  }

  default void scriptTableTo(File destFile, boolean includeDrop, String password) {
    String sql =
        ScriptSql.builder()
            .drop(includeDrop)
            .to(destFile)
            .addTable(getTableName())
            .scriptCompressionEncryption(
                ScriptCompressionEncryption.defaultBuilder(password).build())
            .build()
            .getSql();
    getOrm().execute(sql);
  }

  default int insertCsv(CsvRead csvRead) {
    return getOrm().executeUpdate("insert into " + getTableName() + " select * from " + csvRead);
  }
}
