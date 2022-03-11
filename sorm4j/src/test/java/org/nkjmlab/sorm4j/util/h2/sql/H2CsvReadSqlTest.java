package org.nkjmlab.sorm4j.util.h2.sql;

import java.io.File;
import org.junit.jupiter.api.Test;

class H2CsvReadSqlTest {

  @Test
  void test() {
    H2CsvReadSql.builder(new File("test.csv")).build().getCsvReadAndSelectSql();
  }

}
