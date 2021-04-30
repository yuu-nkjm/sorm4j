package org.nkjmlab.sorm4j.sql.schema;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.schema.TableSchemaKeyword.*;
import org.junit.jupiter.api.Test;

class TableSchemaTest {

  private static GuestTable guestTable = new GuestTable();

  private static class GuestTable {

    private static String ID = "id";
    private static String NAME = "name";
    private static String TEL = "tel";
    private final TableSchema schema;

    public GuestTable() {
      this.schema = new TableSchema.Builder().setTableName("guests").addColumnDefinition(ID, INT)
          .addColumnDefinition(NAME, VARCHAR).addColumnDefinition(TEL, VARCHAR)
          .addUniqueConstraint(TEL).addIndexColumn(TEL).setPrimaryKey(ID).build();
    }

  }


  @Test
  void testGetColumnNames() {
    assertThat(guestTable.schema.getColumnNames()).contains(GuestTable.ID, GuestTable.NAME,
        GuestTable.TEL);
  }

  @Test
  void testGetName() {
    assertThat(guestTable.schema.getTableName()).isEqualToIgnoringCase("guests");
  }

  @Test
  void testGetTableSchema() {
    assertThat(guestTable.schema.getTableSchema()).isEqualToIgnoringCase(
        "guests(id int, name varchar, tel varchar, primary key(id), unique(tel))");
  }

  @Test
  void testGetIndexSchema() {
    assertThat(guestTable.schema.getCreateIndexStatements().get(0))
        .isEqualToIgnoringCase("CREATE INDEX IF NOT EXISTS index_guests_tel ON guests(tel)");
  }


}
