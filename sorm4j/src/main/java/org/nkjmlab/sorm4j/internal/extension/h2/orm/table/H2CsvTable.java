package org.nkjmlab.sorm4j.internal.extension.h2.orm.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.H2TableBase;

@Experimental
public class H2CsvTable extends H2TableBase<RowMap> {

  private final Sorm sorm;
  private final CsvRead csvRead;
  private final String tableName;

  public H2CsvTable(Sorm sorm, CsvRead csvRead, String tableName) {
    super(sorm, RowMap.class, tableName);
    this.sorm = sorm;
    this.csvRead = csvRead;
    this.tableName = tableName;
  }

  public void buildTableFromFile() {
    String sql = "CREATE TABLE " + tableName + " AS SELECT * FROM " + csvRead.getSql();
    sorm.execute(sql);
  }
}
