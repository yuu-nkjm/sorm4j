package org.nkjmlab.sorm4j.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_BOB;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.TENNIS;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createGuestsTable;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createPlayersTable;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSormWithNewContext;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSormWithNewDatabaseAndCreateTables;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSportsTable;
import static org.nkjmlab.sorm4j.util.sql.statement.SelectSql.selectCountFrom;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.handler.ConsumerHandler;
import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.container.RowMap;
import org.nkjmlab.sorm4j.container.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.container.sql.TableName;
import org.nkjmlab.sorm4j.table.orm.SimpleDefinedTable;
import org.nkjmlab.sorm4j.table.orm.SimpleTable;
import org.nkjmlab.sorm4j.table.orm.Table;
import org.nkjmlab.sorm4j.table.orm.TableConnection;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.sql.statement.JoinSql;

class TableTest {
  private static final String SELECT_FROM_PLAYERS_WHERE_ID_SQL = "select * from players where id=?";
  private static final ParameterizedSql SELECT_FROM_PLAYERS_WHERE_ID_PSQL =
      ParameterizedSql.of(SELECT_FROM_PLAYERS_WHERE_ID_SQL, 1);

  private SimpleDefinedTable<Player> playersTable;
  private SimpleDefinedTable<Sport> sportsTable;

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
    playersTable.getTableDefinition();

    SimpleTable<Guest> gt = new SimpleTable<>(sorm, Guest.class);
    assertThat(gt.getTableName()).isEqualTo("GUESTS");
    assertThat(gt.getValueType()).isEqualTo(Guest.class);
  }

  @Test
  void testTableCon() {
    try (OrmConnection con = playersTable.getOrm().open()) {
      TableConnection<Player> c = TableConnection.of(con, Player.class);
      c.count();
    }
    try (OrmConnection ocon = playersTable.getOrm().open();
        TableConnection<Player> con = playersTable.toTableConnection(ocon)) {
      con.count();
    }
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
        .contains(TableName.of("PLAYERS"));
  }

  @Test
  void testCreateIndexesIfNotExists() {
    playersTable.createTableIfNotExists().createIndexesIfNotExists();
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getJdbcIndexesMetaData().toString())
        .contains("INDEX_IN_GUESTS_ON_NAME");
  }

  @Test
  void testDropTableIfExists() {
    playersTable.createTableIfNotExists();
    assertThat(playersTable.getOrm().getJdbcDatabaseMetaData().getTableNames())
        .contains(TableName.of("PLAYERS"));
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
    playersTable.insertAndGet(List.of(PLAYER_BOB));

    playersTable.updateByPrimaryKey(RowMap.of("name", "UPDATED_ALICE"), 1);
    assertThat(playersTable.selectByPrimaryKey(1).getName()).isEqualTo("UPDATED_ALICE");

    playersTable.delete(List.of(PLAYER_ALICE));
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(1);
  }

  @Test
  void testDeleteT() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.update(PLAYER_ALICE);
    playersTable.merge(PLAYER_ALICE);
    playersTable.delete(PLAYER_ALICE);
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(0);
    playersTable.insertAndGet(PLAYER_BOB);
    RowMap rm1 = RowMap.of("id", 111, "name", "name1", "address", "address1");
    RowMap rm2 = RowMap.of("id", 112, "name", "name2", "address", "address2");
    RowMap rm3 = RowMap.of("id", 113, "name", "name3", "address", "address3");
    playersTable.insertMapIn(rm1);
    assertThat(playersTable.exists(111)).isTrue();
    playersTable.insertMapIn(List.of(rm2, rm3));
  }

  @Test
  void testDeleteByPrimaryKey() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.insert(PLAYER_BOB);
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(2);
    playersTable.deleteByPrimaryKey(PLAYER_ALICE.id);
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(1);
  }

  @Test
  void testUpdateByPrimaryKey() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.insert(PLAYER_BOB);
    playersTable.updateByPrimaryKey(RowMap.of("name", "AAA"), PLAYER_ALICE.id);
    playersTable.updateByPrimaryKey(RowMap.of("name", "bbb"), PLAYER_BOB.id);

    assertThat(playersTable.selectByPrimaryKey(PLAYER_ALICE.id).getName()).isEqualTo("AAA");
    assertThat(playersTable.selectByPrimaryKey(PLAYER_BOB.id).getName()).isEqualTo("bbb");
  }

  @Test
  void testDeleteByPrimaryKeyIn() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.insert(PLAYER_BOB);
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(2);
    playersTable.deleteByPrimaryKeyIn(playersTable.getTableName(), PLAYER_ALICE.id);
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(1);
  }

  @Test
  void testDeleteTArray() {
    playersTable.insert(new Player[] {PLAYER_ALICE});
    playersTable.update(new Player[] {PLAYER_ALICE});
    playersTable.merge(new Player[] {PLAYER_ALICE});
    playersTable.delete(new Player[] {PLAYER_ALICE});
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(0);
    playersTable.insertAndGet(new Player[] {PLAYER_BOB});
  }

  @Test
  void testDeleteAll() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.deleteAll();
    assertThat(
            playersTable
                .getOrm()
                .readOne(Integer.class, selectCountFrom(playersTable.getTableName())))
        .isEqualTo(0);
  }

  @Test
  void testGetTableMetaData() {
    assertThat(playersTable.getTableMetaData()).isNotNull();
  }

  @Test
  void testSelectFirstAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectFirstAllEqual("name", PLAYER_ALICE.getName()).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testSelectOneAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectOneAllEqual("name", PLAYER_ALICE.getName()).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testSelectListAllEqual() {
    playersTable.insert(PLAYER_ALICE);
    assertThat(playersTable.selectListAllEqual("name", PLAYER_ALICE.getName()).get(0).getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testJoin() {
    playersTable.insert(PLAYER_ALICE);
    sportsTable.insert(TENNIS);
    assertThat(playersTable.joinOn(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());

    assertThat(playersTable.joinUsing(sportsTable, "id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());

    JoinSql.builder(playersTable.getOrm().getTableMetaData(Player.class));
  }

  @Test
  void testLeftJoin() {
    playersTable.insert(PLAYER_ALICE);
    sportsTable.insert(TENNIS);
    assertThat(playersTable.leftJoinOn(sportsTable, "players.id=sports.id").get(0).getT1().getId())
        .isEqualTo(PLAYER_ALICE.getId());
  }

  @Test
  void testStream() {
    playersTable.insert(PLAYER_ALICE);
    playersTable.streamAll().accept(stream -> stream.collect(Collectors.toList()));

    int ret = playersTable.streamAll().apply(stream -> stream.collect(Collectors.toList())).size();
    assertThat(ret).isEqualTo(1);
    List<Player> ret1 =
        playersTable.stream(ParameterizedSql.of("select * from players"))
            .apply(stream -> stream.collect(Collectors.toList()));
    assertThat(ret1.size()).isEqualTo(1);
  }

  @Test
  void testCreate() {
    Sorm mockSorm = mock(Sorm.class);
    Class<?> valueType = Player.class;

    Table<?> table = Table.create(mockSorm, valueType);

    assertNotNull(table);
    assertTrue(table instanceof Table);
  }

  @Test
  void testOpen() {
    Sorm mockSorm = mock(Sorm.class);
    OrmConnection mockOrmConnection = mock(OrmConnection.class);
    when(mockSorm.open()).thenReturn(mockOrmConnection);

    Table<?> table = Table.create(mockSorm, Player.class);
    TableConnection<?> tableConnection = table.open();

    assertNotNull(tableConnection);
    assertTrue(tableConnection instanceof TableConnection);
  }

  @Test
  void testAcceptHandler() {
    @SuppressWarnings("unchecked")
    ConsumerHandler<TableConnection<Player>> handler = mock(ConsumerHandler.class);
    assertDoesNotThrow(() -> playersTable.acceptHandler(handler));
  }

  @Test
  void testAcceptHandlerExp() {
    @SuppressWarnings("unchecked")
    ConsumerHandler<TableConnection<Player>> handler = mock(ConsumerHandler.class);

    assertDoesNotThrow(() -> playersTable.acceptHandler(handler));
  }

  @Test
  void testApplyHandler() {
    FunctionHandler<TableConnection<Player>, String> handler = conn -> "result";
    assertEquals("result", playersTable.applyHandler(handler));
  }

  @Test
  void testAcceptHandlerWithException() throws Exception {
    Sorm orm = mock(Sorm.class);
    OrmConnection conn = mock(OrmConnection.class);
    @SuppressWarnings("unchecked")
    ConsumerHandler<TableConnection<Object>> handler = mock(ConsumerHandler.class);

    when(orm.open()).thenReturn(conn);
    doThrow(new RuntimeException("Test Exception")).when(handler).accept(any());

    Table<Object> table = Table.create(orm, Object.class);

    Exception exception = assertThrows(RuntimeException.class, () -> table.acceptHandler(handler));

    assertEquals("Test Exception", exception.getMessage());
  }

  @Test
  void testApplyHandlerWithException() throws Exception {
    Sorm orm = mock(Sorm.class);
    OrmConnection conn = mock(OrmConnection.class);
    @SuppressWarnings("unchecked")
    FunctionHandler<TableConnection<Object>, String> handler = mock(FunctionHandler.class);

    when(orm.open()).thenReturn(conn);
    when(handler.apply(any())).thenThrow(new RuntimeException("Test Exception"));

    Table<Object> table = Table.create(orm, Object.class);

    Exception exception = assertThrows(RuntimeException.class, () -> table.applyHandler(handler));

    assertEquals("Test Exception", exception.getMessage());
  }
}
