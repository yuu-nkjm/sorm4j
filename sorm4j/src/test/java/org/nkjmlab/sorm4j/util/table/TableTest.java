package org.nkjmlab.sorm4j.util.table;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.result.Tuple;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

class TableTest {
  private static final String SELECT_FROM_PLAYERS_WHERE_ID_SQL = "select * from players where id=?";
  private static final ParameterizedSql SELECT_FROM_PLAYERS_WHERE_ID_PSQL =
      ParameterizedSql.of(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1);

  private TableWithSchema<Player> playersTable;
  private TableWithSchema<Guest> guestsTable;
  private TableWithSchema<Sport> sportsTable;

  @BeforeEach
  void setUp() {
    Sorm sorm = createNewContextSorm();
    playersTable = createPlayersTable(sorm);
    guestsTable = createGuestsTable(sorm);
    sportsTable = createSportsTable(sorm);

  }

  @Test
  void testGetTableSchema() {
    playersTable.getTableSchema();
  }

  @Test
  void testGetValueType() {
    playersTable.getValueType();
  }

  @Test
  void testGetSorm() {
    playersTable.getOrm();
  }

  @Test
  void testCreateTableAndIndexesIfNotExists() {
    playersTable.createTableIfNotExists().createIndexesIfNotExists();
  }

  @Test
  void testCreateTableIfNotExists() {
    playersTable.createTableIfNotExists();
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getTableNames()).contains("GUESTS");
  }

  @Test
  void testCreateIndexesIfNotExists() {
    playersTable.createTableIfNotExists().createIndexesIfNotExists();
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getJdbcIndexesMetaData().toString())
        .contains("INDEX_IN_GUESTS_ON_NAME");
  }

  @Test
  void testDropTableIfExists() {
    playersTable.createTableIfNotExists();
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getTableNames()).contains("GUESTS");
  }

  @Test
  void testReadAll() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readAll().size()).isEqualTo(1);
  }

  @Test
  void testReadByPrimaryKey() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.findByPrimaryKey(PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadFirstParameterizedSql() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readFirst(SELECT_FROM_PLAYERS_WHERE_ID_PSQL).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadFirstStringObjectArray() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readFirst(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadListParameterizedSql() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readList(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadListStringObjectArray() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readList(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadOneParameterizedSql() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readOne(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadOneStringObjectArray() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readOne(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testGetRowMapper() {
    assertThat(playersTable.getRowMapper()).isNotNull();
  }

  @Test
  void testGetResultSetTraverser() {
    assertThat(playersTable.getResultSetTraverser()).isNotNull();
  }

  @Test
  void testExists() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.exists(PLAYER_ALICE));
  }

  @Test
  void testDeleteListOfT() {
    playersTable.insertOn(PLAYER_ALICE);
    playersTable.deleteOn(List.of(PLAYER_ALICE));
    assertThat(playersTable.getOrm().readOne(Integer.class,
        SelectSql.selectStarFrom(playersTable.getTableName())));
  }

  @Test
  void testDeleteT() {
    fail("Not yet implemented");
  }

  @Test
  void testDeleteTArray() {
    fail("Not yet implemented");
  }

  @Test
  void testDeleteAll() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertListOfT() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertT() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertTArray() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertAndGetListOfT() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertAndGetT() {
    fail("Not yet implemented");
  }

  @Test
  void testInsertAndGetTArray() {
    fail("Not yet implemented");
  }

  @Test
  void testMergeListOfT() {
    fail("Not yet implemented");
  }

  @Test
  void testMergeT() {
    fail("Not yet implemented");
  }

  @Test
  void testMergeTArray() {
    fail("Not yet implemented");
  }

  @Test
  void testUpdateListOfT() {
    fail("Not yet implemented");
  }

  @Test
  void testUpdateT() {
    fail("Not yet implemented");
  }

  @Test
  void testUpdateTArray() {
    fail("Not yet implemented");
  }

  @Test
  void testGetRowToMapMapper() {
    fail("Not yet implemented");
  }

  @Test
  void testGetResultSetToMapTraverser() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapFirstParameterizedSql() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapFirstStringObjectArray() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapListParameterizedSql() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapListStringObjectArray() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapOneParameterizedSql() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapOneStringObjectArray() {
    fail("Not yet implemented");
  }

  @Test
  void testGetTableName() {
    fail("Not yet implemented");
  }

  @Test
  void testGetTableMetaData() {
    fail("Not yet implemented");
  }

  @Test
  void testExecuteQueryParameterizedSqlResultSetTraverserOfS() {
    fail("Not yet implemented");
  }

  @Test
  void testExecuteQueryParameterizedSqlRowMapperOfS() {
    fail("Not yet implemented");
  }

  @Test
  void testExecuteUpdateStringObjectArray() {
    fail("Not yet implemented");
  }

  @Test
  void testExecuteUpdateParameterizedSql() {
    fail("Not yet implemented");
  }

  @Test
  void testReadListAllMatch() {
    fail("Not yet implemented");
  }

  @Test
  void testReadFirstAllMatch() {
    fail("Not yet implemented");
  }

  @Test
  void testReadOneAllMatch() {
    fail("Not yet implemented");
  }

  @Test
  void testReadMapListAllMatch() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(
        playersTable.readMapListAllMatch(Tuple.of("name", PLAYER_ALICE.getName())).get(0).get("id"))
            .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadMapOneAllMatch() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(playersTable.readMapOneAllMatch(Tuple.of("name", PLAYER_ALICE.getName())).get("id"))
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadMapFirstAllMatch() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(
        playersTable.readMapFirstAllMatch(Tuple.of("name", PLAYER_ALICE.getName())).get("id"))
            .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testGetAllMatchSql() {
    playersTable.insertOn(PLAYER_ALICE);
    assertThat(
        playersTable.findListAllMatch(Tuple.of("name", PLAYER_ALICE.getName())).get(0).getId())
            .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testJoin() {
    playersTable.insertOn(PLAYER_ALICE);
    sportsTable.insertOn(TENNIS);
    assertThat(playersTable.join(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testLeftJoin() {
    playersTable.insertOn(PLAYER_ALICE);
    sportsTable.insertOn(TENNIS);
    assertThat(playersTable.leftJoin(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testGetColumnAliases() {
    assertThat(playersTable.getColumnAliases()).isEqualTo("g");
  }

}
