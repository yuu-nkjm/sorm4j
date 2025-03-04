package org.nkjmlab.sorm4j.table;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.table.TableSchemaTest.TempGuestTable.Column.*;
import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class TableSchemaTest {

  private static TempGuestTable tempGuestTable = new TempGuestTable();
  private static Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();

  static class TempGuestTable {

    public static enum Column {
      ID,
      NAME,
      TEL
    }

    private final TableDefinition schema;

    public TempGuestTable() {
      this.schema =
          TableDefinition.builder("temp_guests")
              .addColumnDefinition(ID, INT)
              .addColumnDefinition(NAME, VARCHAR)
              .addColumnDefinition(TEL, VARCHAR)
              .addUniqueConstraint(TEL)
              .addIndexDefinition(TEL)
              .setPrimaryKey(ID)
              .build();
    }
  }

  @Test
  void testSchemeBuilder() {
    TableDefinition schema1 =
        TableDefinition.builder("temp_guests")
            .addColumnDefinition(ID.name(), INT)
            .addColumnDefinition(NAME.name(), VARCHAR)
            .addColumnDefinition(TEL.name(), VARCHAR)
            .addUniqueConstraint(TEL.name())
            .addIndexDefinition(TEL.name())
            .setPrimaryKey(ID.name())
            .build();

    TableDefinition schema2 =
        TableDefinition.builder("temp_guests")
            .addColumnDefinition(ID, INT)
            .addColumnDefinition(NAME, VARCHAR)
            .addColumnDefinition(TEL, VARCHAR)
            .addUniqueConstraint(TEL)
            .addIndexDefinition(TEL)
            .setPrimaryKey(ID)
            .build();

    assertThat(schema1.getTableNameAndColumnDefinitions())
        .isEqualTo(schema2.getTableNameAndColumnDefinitions());
  }

  @Test
  void testSchemeBuilder2() {
    TableDefinition.builder("temp_guests")
        .addColumnDefinition(ID.name(), INT)
        .addUniqueConstraint(new String[0])
        .setPrimaryKey(new String[0])
        .build();
  }

  @Test
  void testGetColumnNames() {
    assertThat(tempGuestTable.schema.getColumnNames())
        .containsExactlyInAnyOrder(
            Stream.of(ID, NAME, TEL).map(e -> e.name()).toArray(String[]::new));
  }

  @Test
  void testGetName() {
    assertThat(tempGuestTable.schema.getTableName()).isEqualToIgnoringCase("temp_guests");
  }

  @Test
  void testGetTableSchema() {
    assertThat(tempGuestTable.schema.getTableNameAndColumnDefinitions())
        .isEqualToIgnoringCase(
            "temp_guests(id int, name varchar, tel varchar, primary key(id), unique(tel))");
  }

  @Test
  void testGetIndexSchema() {
    assertThat(tempGuestTable.schema.getCreateIndexIfNotExistsStatements().get(0))
        .isEqualToIgnoringCase(
            "CREATE INDEX IF NOT EXISTS index_in_temp_guests_on_tel ON temp_guests(tel)");
  }

  @Test
  void testCreate() {
    tempGuestTable.schema.createTableIfNotExists(sorm);
    tempGuestTable.schema.createIndexesIfNotExists(sorm);
    tempGuestTable.schema.dropTableIfExistsCascade(sorm);
    tempGuestTable.schema.dropTableIfExists(sorm);
    tempGuestTable.schema.createTableIfNotExists(sorm).createIndexesIfNotExists(sorm);
  }
}
