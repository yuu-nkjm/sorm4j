package org.nkjmlab.sorm4j.util.table;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.*;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Tuple;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.Sport;

class TableTest {
  private static final String SELECT_FROM_PLAYERS_WHERE_ID_SQL = "select * from players where id=?";
  private static final ParameterizedSql SELECT_FROM_PLAYERS_WHERE_ID_PSQL =
      ParameterizedSql.of(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1);

  private TableWithSchema<Player> playersTable;
  private TableWithSchema<Sport> sportsTable;

  @BeforeEach
  void setUp() {
    Sorm sorm = createSormWithNewContext();
    playersTable = createPlayersTable(sorm);
    sportsTable = createSportsTable(sorm);
    createGuestsTable(sorm);

  }

  @Test
  void testGetTableSchema() {
    Sorm sorm = createSormWithNewDatabaseAndCreateTables();
    playersTable.getTableSchema();

    BasicTable<Guest> gt = new BasicTable<>(sorm, Guest.class);
    assertThat(gt.getTableName()).isEqualTo("GUESTS");
    assertThat(gt.getValueType()).isEqualTo(Guest.class);
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
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getTableNames())
        .contains("PLAYERS");
  }

  @Test
  void testCreateIndexesIfNotExists() {
    playersTable.createTableIfNotExists().createIndexesIfNotExists();
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getJdbcIndexesMetaData().toString())
        .contains("INDEX_IN_GUESTS_ON_NAME");

    playersTable.acceptHandler(conn -> conn.streamAll(),
        stream -> stream.collect(Collectors.toList()));
    playersTable.applyHandler(conn -> conn.stream(ParameterizedSql.of("select * from guests")),
        stream -> stream.collect(Collectors.toList()));
  }

  @Test
  void testDropTableIfExists() {
    playersTable.createTableIfNotExists();
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getTableNames())
        .contains("PLAYERS");
  }

  @Test
  void testReadAll() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectAll().size()).isEqualTo(1);
  }

  @Test
  void testReadByPrimaryKey() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectByPrimaryKey(PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstParameterizedSql() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.readFirst(SELECT_FROM_PLAYERS_WHERE_ID_PSQL).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadFirstStringObjectArray() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.readFirst(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).getId())
        .isEqualTo(PLAYER_ALICE.getId());

    assertThat(playersTable.readOne(SELECT_FROM_PLAYERS_WHERE_ID_PSQL).getId())
        .isEqualTo(PLAYER_ALICE.getId());

    assertThat(playersTable.readList(SELECT_FROM_PLAYERS_WHERE_ID_PSQL).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadListParameterizedSql() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.readList(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadListStringObjectArray() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.readList(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadOneParameterizedSql() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.readOne(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testReadOneStringObjectArray() {
    playersTable.insert(PLAYER_ALICE);
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
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.exists(PLAYER_ALICE));
  }

  @Test
  void testDeleteListOfT() {
    playersTable.insert(List.of(PLAYER_ALICE));
    playersTable.update(List.of(PLAYER_ALICE));
    playersTable.merge(List.of(PLAYER_ALICE));
    playersTable.delete(List.of(PLAYER_ALICE));
    assertThat(
        playersTable.getOrm().readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
            .isEqualTo(0);
    playersTable.insertAndGet(List.of(PLAYER_BOB));
  }

  @Test
  void testDeleteT() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.update(PLAYER_ALICE);
    playersTable.merge(PLAYER_ALICE);
    playersTable.delete(PLAYER_ALICE);
    assertThat(
        playersTable.getOrm().readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
            .isEqualTo(0);
    playersTable.insertAndGet(PLAYER_BOB);
  }

  @Test
  void testDeleteTArray() {
    playersTable.insert(new Player[] {PLAYER_ALICE});
    playersTable.update(new Player[] {PLAYER_ALICE});
    playersTable.merge(new Player[] {PLAYER_ALICE});
    playersTable.delete(new Player[] {PLAYER_ALICE});
    assertThat(
        playersTable.getOrm().readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
            .isEqualTo(0);
    playersTable.insertAndGet(new Player[] {PLAYER_BOB});
  }

  @Test
  void testDeleteAll() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.deleteAll();
    assertThat(
        playersTable.getOrm().readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
            .isEqualTo(0);
  }

  @Test
  void testGetTableMetaData() {
    assertThat(playersTable.getTableMetaData()).isNotNull();
  }

  @Test
  void testSelectFirstAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectFirstAllEqual(Tuple.of("name", PLAYER_ALICE.getName())).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testSelectOneAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectOneAllEqual(Tuple.of("name", PLAYER_ALICE.getName())).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testSelectListAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(
        playersTable.selectListAllEqual(Tuple.of("name", PLAYER_ALICE.getName())).get(0).getId())
            .isEqualTo(PLAYER_ALICE.getId());
  }



  @Test
  void testJoin() {
    playersTable.insert(PLAYER_ALICE);
    sportsTable.insert(TENNIS);
    assertThat(playersTable.join(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testLeftJoin() {
    playersTable.insert(PLAYER_ALICE);
    sportsTable.insert(TENNIS);
    assertThat(playersTable.leftJoin(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }
}
