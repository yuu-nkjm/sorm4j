package org.nkjmlab.sorm4j.internal.extension.h2.orm.table;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;

class CsvTableTest {

  @Test
  void test() {
    Sorm sorm = Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
    CsvRead csvRead =
        CsvRead.builderForCsvWithHeader(
                new File(CsvTableTest.class.getResource("/test_csv_table.csv").getFile()))
            .build();
    H2CsvTable table = new H2CsvTable(sorm, csvRead, "tests");
    table.buildTableFromFile();
    List<RowMap> actual = table.readList("select * from tests");
    List<RowMap> expected =
        List.of(
            RowMap.of("HOGE", "1", "FOO", "2", "BAR", "3"),
            RowMap.of("HOGE", "2", "FOO", "4", "BAR", "5"));
    assertThat(actual).isEqualTo(expected);
  }
}
