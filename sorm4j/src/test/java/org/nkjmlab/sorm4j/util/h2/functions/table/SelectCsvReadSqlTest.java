package org.nkjmlab.sorm4j.util.h2.functions.table;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;
import org.nkjmlab.sorm4j.util.h2.datasource.H2LocalDataSourceFactory;

class SelectCsvReadSqlTest {

  @Test
  void test0() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();
    Sorm sorm = Sorm.create(H2LocalDataSourceFactory.createTemporalInMemoryDataSource());
    BasicH2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
    table.createTableIfNotExists(
        CsvReadSql.builderForCsvWithoutHeader(file, 2)
            .charset("UTF-16")
            .fieldSeparator("\t")
            .build());
    assertThat(table.selectAll().get(0).id).isEqualTo(1);
  }

  @Test
  void test1() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();

    assertThat(
            SelectCsvReadSql.builder(file)
                .csvColumns("col-1", "col-2")
                .tableColumns("col_1", "col_2")
                .charset("UTF_16")
                .fieldSeparator('\t')
                .mapCsvColumnToTableColumn("`col-1`", "col_1")
                .mapCsvColumnToTableColumn("`col-2`", "col_2")
                .build()
                .getCsvReadAndSelectSql())
        .isEqualTo(
            "select `col-1` as col_1,`col-2` as col_2 from csvread ('"
                + file.getAbsolutePath()
                + "', 'col-1	col-2', stringdecode('charset=UTF-16 fieldDelimiter=null fieldSeparator=\\t'))");
  }

  @Test
  void test2() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();
    assertThat(
            SelectCsvReadSql.builder(file, OrmRecordExample.class)
                .build()
                .getCsvReadAndInsertSql("test_table"))
        .isEqualTo(
            "insert into test_table(ID,NAME) select ID,NAME from csvread ('"
                + file.getAbsolutePath()
                + "', null, stringdecode('charset=UTF-8 fieldDelimiter=null fieldSeparator=,'))");
  }

  @Test
  void test3() {
    File file = new File("file.csv");
    String ret =
        SelectCsvReadSql.builder(file, Item.class)
            .charset(StandardCharsets.UTF_8)
            .fieldSeparator('\t')
            .build()
            .getCsvReadAndSelectSql();
    assertThat(ret)
        .isEqualTo(
            "select parsedatetime(delivery_date, 'y/MM/d') as DELIVERY_DATE,`price/prices` as PRICE from csvread ('"
                + file.getAbsolutePath()
                + "', null, stringdecode('charset=UTF-8 fieldDelimiter=null fieldSeparator=\\t'))");
  }

  @OrmRecord
  public static class Item {
    @CsvColumn("parsedatetime(delivery_date, 'y/MM/d')")
    public LocalDate deliveryDate;

    @CsvColumn("`price/prices`")
    public int price;
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
