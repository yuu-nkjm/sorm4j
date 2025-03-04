package org.nkjmlab.sorm4j.util.h2;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.h2.table.definition.H2SimpleDefinedTable;

class H2BasicTableMappedOrmTest {

  @Test
  void test2() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    H2SimpleDefinedTable<Example> table = new H2SimpleDefinedTable<>(sorm, Example.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();
    table.getOrm();
    table.getTableName();
    table.getTableDefinition();
    table.getValueType();
    table.insert(new Example(0, "name"));
  }

  @OrmRecord
  public record Example(int id, String name) {}
}
