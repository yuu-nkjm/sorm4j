package org.nkjmlab.sorm4j.extension.h2.sql.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTable;
import org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation.CsvColumnExpression;
import org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation.CsvIgnore;

class SelectCsvReadSqlTest {
  @Test
  void testBuilderWithCsvRead() {
    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder = SelectCsvReadSql.builder(mockCsvRead);
    SelectCsvReadSql selectCsvReadSql = builder.build();

    assertEquals("select * from csvread('data.csv')", selectCsvReadSql.getSql());
  }

  @Test
  void testBuilderWithCsvReadAndValueType() {
    class TestClass {
      @CsvColumnExpression("col1")
      private String column1;

      @CsvIgnore private String column2;
    }

    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder =
        SelectCsvReadSql.builder(mockCsvRead).valueType(TestClass.class);
    SelectCsvReadSql selectCsvReadSql = builder.build();

    String expectedSql = "select col1 as COLUMN1, null as COLUMN2 from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
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

    String expectedSql = "select col1 as column1, column2 from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
  }

  @Test
  void test0() throws URISyntaxException {
    File file = Paths.get(SelectCsvReadSqlTest.class.getResource("test.tsv").toURI()).toFile();
    Sorm sorm = Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
    H2DefinedTable<OrmRecordExample> table = H2DefinedTable.of(sorm, OrmRecordExample.class);
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
            "select `col-1` as col_1, `col-2` as col_2 from csvread('"
                + file.getAbsolutePath()
                + "', 'col-1\tcol-2', stringdecode('charset=UTF-16 fieldSeparator=\\t'))");
  }

  @Test
  void test3() {
    File file = new File("file.csv");
    String ret =
        SelectCsvReadSql.builder(
                CsvRead.builderForCsvWithHeader(file)
                    .charset(StandardCharsets.UTF_8.toString())
                    .fieldSeparator("\t")
                    .build())
            .valueType(Item.class)
            .build()
            .getSql();
    assertThat(ret)
        .isEqualTo(
            "select parsedatetime(delivery_date, 'y/MM/d') as DELIVERY_DATE, `price/prices` as PRICE from csvread('"
                + file.getAbsolutePath()
                + "', null, stringdecode('charset=UTF-8 fieldSeparator=\\t'))");
  }

  @Test
  void testBuilderWithMultipleMappings() {
    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder =
        SelectCsvReadSql.builder(mockCsvRead)
            .mapCsvColumnToTableColumn("UPPER(name)", "name_upper")
            .mapCsvColumnToTableColumn("LENGTH(description)", "desc_length")
            .tableColumns("name_upper", "desc_length", "price");
    SelectCsvReadSql selectCsvReadSql = builder.build();

    String expectedSql =
        "select UPPER(name) as name_upper, LENGTH(description) as desc_length, price from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
  }

  @Test
  void testBuilderWithCsvIgnoreAndExpression() {
    class TestClass {
      @CsvColumnExpression("LOWER(email)")
      private String email;

      @CsvIgnore private String password;
    }

    CsvRead mockCsvRead = mock(CsvRead.class);
    when(mockCsvRead.getSql()).thenReturn("csvread('data.csv')");

    SelectCsvReadSql.Builder builder =
        SelectCsvReadSql.builder(mockCsvRead).valueType(TestClass.class);
    SelectCsvReadSql selectCsvReadSql = builder.build();

    String expectedSql = "select LOWER(email) as EMAIL, null as PASSWORD from csvread('data.csv')";
    assertEquals(expectedSql, selectCsvReadSql.getSql());
  }

  public static class Item {
    @CsvColumnExpression("parsedatetime(delivery_date, 'y/MM/d')")
    public LocalDate deliveryDate;

    @CsvColumnExpression("`price/prices`")
    public int price;
  }

  public record OrmRecordExample(int id, String name) {}
}
