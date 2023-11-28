package org.nkjmlab.sorm4j.util.h2.sql;

import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.annotation.OrmRecord;

class H2CsvReadSqlTest {

  @Test
  void test() throws URISyntaxException {
    File file = Paths.get(H2CsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();

    assertThat(
            H2CsvReadSql.builder(file)
                .setCsvColumns("col-1", "col-2")
                .setTableColumns("col_1", "col_2")
                .setCharset("UTF_16")
                .setFieldSeparator('\t')
                .mapCsvColumnToTableColumn("`col-1`", "col_1")
                .mapCsvColumnToTableColumn("`col-2`", "col_2")
                .build()
                .getCsvReadAndSelectSql())
        .isEqualTo(
            "select `col-1` as col_1,`col-2` as col_2 from csvread('"
                + file.getAbsolutePath()
                + "','COL-1'||char(9)||'COL-2','charset=UTF-16'||' fieldSeparator='||char(9))");

    assertThat(
            H2CsvReadSql.builder(file, OrmRecordExample.class)
                .build()
                .getCsvReadAndInsertSql("test_table"))
        .isEqualTo(
            "insert into test_table(ID,NAME) select ID,NAME from csvread('"
                + file.getAbsolutePath()
                + "',null,'charset=UTF-8'||' fieldSeparator='||char(44))");
  }

  @OrmRecord
  public static class OrmRecordExample {
    private final int id;
    private final String name;

    public OrmRecordExample(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }
}
