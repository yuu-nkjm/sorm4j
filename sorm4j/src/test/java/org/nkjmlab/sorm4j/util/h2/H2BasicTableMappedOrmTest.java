package org.nkjmlab.sorm4j.util.h2;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class H2BasicTableMappedOrmTest {

  @Test
  void test2() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    try (OrmConnection con = sorm.open()) {
      H2BasicTableMappedOrm<Example> table = new H2BasicTableMappedOrm<>(con, Example.class);
      table.dropTableIfExists();
      table.createTableIfNotExists();
      table.getOrm();
      table.getTableName();
      table.getTableDefinition();
      table.getValueType();
      table.insert(new Example(0, "name"));
    }
  }

  @OrmRecord
  public record Example(int id, String name) {}
}
