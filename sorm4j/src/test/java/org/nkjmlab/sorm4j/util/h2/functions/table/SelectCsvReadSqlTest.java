package org.nkjmlab.sorm4j.util.h2.functions.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecord;
import org.nkjmlab.sorm4j.util.h2.datasource.H2DataSourceFactory;
import org.nkjmlab.sorm4j.util.h2.sql.builder.InsertSelectCsvReadSql;
import org.nkjmlab.sorm4j.util.h2.sql.builder.SelectCsvReadSql;
import org.nkjmlab.sorm4j.util.h2.sql.builder.annotation.CsvColumn;
import org.nkjmlab.sorm4j.util.h2.sql.builder.annotation.SkipCsvRead;
import org.nkjmlab.sorm4j.util.h2.table.definition.H2SimpleDefinedTable;

@SuppressWarnings("deprecation")
class SelectCsvReadSqlTest {
  @Test
  void testBuilderWithCsvRead() {
    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder = SelectCsvReadSql.builder(mockCsvRead);
    SelectCsvReadSql selectCsvReadSql = builder.build();

    assertEquals("select * from csvread('data.csv')", selectCsvReadSql.getSql());
    assertTrue(selectCsvReadSql.getSelectColumns().isEmpty());
  }

  @Test
  void testBuilderWithCsvReadAndValueType() {
    class TestClass {
      @CsvColumn("col1")
      private String column1;

      @SkipCsvRead private String column2;
    }

    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder = SelectCsvReadSql.builder(mockCsvRead, TestClass.class);
    SelectCsvReadSql selectCsvReadSql = builder.build();

    String expectedSql = "select col1 as COLUMN1,null as COLUMN2 from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
    assertEquals(
        List.of("col1 as COLUMN1", "null as COLUMN2"), selectCsvReadSql.getSelectColumns());
  }

  @Test
  void testBuilderWithMappingAndColumns() {
    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder =
        SelectCsvReadSql.builder(mockCsvRead)
            .mapCsvColumnToTableColumn("col1", "column1")
            .tableColumns("column1", "column2");
    SelectCsvReadSql selectCsvReadSql = builder.build();

    String expectedSql = "select col1 as column1,column2 from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
    assertEquals(List.of("col1 as column1", "column2"), selectCsvReadSql.getSelectColumns());
  }

  @Test
  void test0() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();
    Sorm sorm = Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
    H2SimpleDefinedTable<OrmRecordExample> table =
        new H2SimpleDefinedTable<>(sorm, OrmRecordExample.class);
    table.createTableIfNotExists(
        CsvRead.builderForCsvWithoutHeader(file, 2).charset("UTF-16").fieldSeparator("\t").build());
    assertThat(table.selectAll().get(0).id).isEqualTo(1);
  }

  @Test
  void test1() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();

    assertThat(
            SelectCsvReadSql.builder(
                    CsvRead.builderForCsvWithoutHeader(file, List.of("col-1", "col-2"))
                        .charset("UTF-16")
                        .fieldSeparator("\t")
                        .build())
                .tableColumns("col_1", "col_2")
                .mapCsvColumnToTableColumn("`col-1`", "col_1")
                .mapCsvColumnToTableColumn("`col-2`", "col_2")
                .build()
                .getSql())
        .isEqualTo(
            "select `col-1` as col_1,`col-2` as col_2 from csvread('"
                + file.getAbsolutePath()
                + "', 'col-1	col-2', stringdecode('charset=UTF-16 fieldSeparator=\\t'))");
  }

  @Test
  void test2() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();
    assertThat(
            InsertSelectCsvReadSql.builder(
                    "test_table",
                    SelectCsvReadSql.builder(
                            CsvRead.builderForCsvWithoutHeader(file, List.of("ID", "NAME")).build(),
                            OrmRecordExample.class)
                        .build())
                .build()
                .getSql())
        .isEqualTo(
            "insert into test_table(ID,NAME) select ID,NAME from csvread('"
                + file.getAbsolutePath()
                + "', 'ID,NAME', null)");
  }

  @Test
  void test3() {
    File file = new File("file.csv");
    String ret =
        SelectCsvReadSql.builder(
                CsvRead.builderForCsvWithHeader(file)
                    .charset(StandardCharsets.UTF_8.toString())
                    .fieldSeparator("\t")
                    .build(),
                Item.class)
            .build()
            .getSql();
    assertThat(ret)
        .isEqualTo(
            "select parsedatetime(delivery_date, 'y/MM/d') as DELIVERY_DATE,`price/prices` as PRICE from csvread('"
                + file.getAbsolutePath()
                + "', null, stringdecode('charset=UTF-8 fieldSeparator=\\t'))");
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
