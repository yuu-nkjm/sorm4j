package org.nkjmlab.sorm4j.sql.schema;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.schema.TableSchema.Keyword.*;
import static org.nkjmlab.sorm4j.sql.schema.TableSchemaTest.TempGuestTable.Column.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class TableSchemaTest {

  private static TempGuestTable tempGuestTable = new TempGuestTable();
  private static Sorm sorm = SormTestUtils.createSorm();

  static class TempGuestTable {

    public static enum Column {
      ID, NAME, TEL
    }

    private final TableSchema schema;

    public TempGuestTable() {
      this.schema = TableSchema.builder().setTableName("temp_guests").addColumnDefinition(ID, INT)
          .addColumnDefinition(NAME, VARCHAR).addColumnDefinition(TEL, VARCHAR)
          .addUniqueConstraint(TEL).addIndexDefinition(TEL).setPrimaryKey(ID).build();
    }

  }

  @Test
  void testSchemeBuilder() {
    TableSchema schema1 = TableSchema.builder().setTableName("temp_guests")
        .addColumnDefinition(ID.name(), INT).addColumnDefinition(NAME.name(), VARCHAR)
        .addColumnDefinition(TEL.name(), VARCHAR).addUniqueConstraint(TEL.name())
        .addIndexDefinition(TEL.name()).setPrimaryKey(ID.name()).build();

    TableSchema schema2 = TableSchema.builder("temp_guests").addColumnDefinition(ID, INT)
        .addColumnDefinition(NAME, VARCHAR).addColumnDefinition(TEL, VARCHAR)
        .addUniqueConstraint(TEL).addIndexDefinition(TEL).setPrimaryKey(ID).build();

    assertThat(schema1.getTableSchema()).isEqualTo(schema2.getTableSchema());

  }

  @Test
  void testSchemeBuilder2() {
    TableSchema schema1 =
        TableSchema.builder().setTableName("temp_guests").addColumnDefinition(ID.name(), INT)
            .addUniqueConstraint(new String[0]).setPrimaryKey(new String[0]).build();

  }


  @Test
  void testGetColumnNames() {
    assertThat(tempGuestTable.schema.getColumnNames()).containsExactlyInAnyOrder(
        Stream.of(ID, NAME, TEL).map(e -> e.name()).toArray(String[]::new));
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
    tempGuestTable.schema.createTableIfNotExists(sorm);
    tempGuestTable.schema.createIndexesIfNotExists(sorm);
    tempGuestTable.schema.dropTableIfExists(sorm);
  }


}
