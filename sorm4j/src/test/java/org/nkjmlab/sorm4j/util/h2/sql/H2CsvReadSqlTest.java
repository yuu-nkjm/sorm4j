package org.nkjmlab.sorm4j.util.h2.sql;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;

class H2CsvReadSqlTest {

  @Test
  void test() {
    Sorm sorm = Sorm.create("jdbc:h2:mem:sorm;DB_CLOSE_DELAY=-1");
    System.out.println(H2CsvReadSql.builder(new File("test.csv")).build().getCsvReadAndSelectSql());

  }

}
