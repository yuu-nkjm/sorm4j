package org.nkjmlab.sorm4j.sql.schema;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.schema.TableSchema.TableSchemaKeyword.*;
import org.junit.jupiter.api.Test;

class TableSchemaTest {

  private static GuestTable guestTable = new GuestTable();

  private static class GuestTable extends TableSchema {

    private static String ID = "id";
    private static String NAME = "name";
    private static String TEL = "tel";

    public GuestTable() {
      super("guests");
      addColumnDefinition(ID, INT);
      addColumnDefinition(NAME, VARCHAR);
      addColumnDefinition(TEL, VARCHAR);
      addUniqueConstraint(TEL);
      setPrimaryKey(ID);
    }

  }


  @Test
  void testGetColumnNames() {
    assertThat(guestTable.getColumnNames()).contains(GuestTable.ID, GuestTable.NAME,
        GuestTable.TEL);
  }

  @Test
  void testGetName() {
    assertThat(guestTable.getName()).isEqualToIgnoringCase("guests");
  }

  @Test
  void testGetTableSchema() {
    assertThat(guestTable.getTableSchema()).isEqualToIgnoringCase(
        "guests(id int, name varchar, tel varchar, primary key(id), unique(tel))");
  }

  @Test
  void testGetIndexSchema() {
    assertThat(guestTable.getIndexSchema(GuestTable.TEL))
        .isEqualToIgnoringCase("CREATE INDEX IF NOT EXISTS index_guests_tel ON guests(tel)");
  }


}
