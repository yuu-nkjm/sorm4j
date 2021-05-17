package org.nkjmlab.sorm4j.sql.schema;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.schema.TableSchemaKeyword.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class TableSchemaTest {

  private static TempGuestTable tempGuestTable = new TempGuestTable();
  private static Sorm sorm = SormTestUtils.createSorm();

  private static class TempGuestTable {

    private static String ID = "id";
    private static String NAME = "name";
    private static String TEL = "tel";
    private final TableSchema schema;

    public TempGuestTable() {
      this.schema =
          new TableSchema.Builder().setTableName("temp_guests").addColumnDefinition(ID, INT)
              .addColumnDefinition(NAME, VARCHAR).addColumnDefinition(TEL, VARCHAR)
              .addUniqueConstraint(TEL).addIndexColumn(TEL).setPrimaryKey(ID).build();
    }

  }


  @Test
  void testGetColumnNames() {
    assertThat(tempGuestTable.schema.getColumnNames()).contains(TempGuestTable.ID,
        TempGuestTable.NAME, TempGuestTable.TEL);
  }

  @Test
  void testGetName() {
    assertThat(tempGuestTable.schema.getTableName()).isEqualToIgnoringCase("temp_guests");
  }

  @Test
  void testGetTableSchema() {
    assertThat(tempGuestTable.schema.getTableSchema()).isEqualToIgnoringCase(
        "temp_guests(id int, name varchar, tel varchar, primary key(id), unique(tel))");
  }

  @Test
  void testGetIndexSchema() {
    assertThat(tempGuestTable.schema.getCreateIndexIfNotExistsStatements().get(0))
        .isEqualToIgnoringCase(
            "CREATE INDEX IF NOT EXISTS index_temp_guests_tel ON temp_guests(tel)");
  }


  @Test
  void testCreate() {
    tempGuestTable.schema.createTableIfNotExists(sorm.getOrm());
    tempGuestTable.schema.createIndexesIfNotExists(sorm.getOrm());
    tempGuestTable.schema.dropTableIfExists(sorm.getOrm());
  }


}
