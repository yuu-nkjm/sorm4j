package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

class OrmImplTest {

  private static final String PLAYERS1 = "players1";

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }


  @Test
  void testReadAll() {
    Sorm logSorm = Sorm.create(sorm.getDataSource(), SormContext.builder(sorm.getContext())
        .setLoggerContext(LoggerContext.builder().enableAll().build()).build());
    logSorm.insert(PLAYER_ALICE);
    assertThat(logSorm.selectAll(Player.class)).contains(PLAYER_ALICE);
  }


  @Test
  void testReadByPrimaryKey() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.selectByPrimaryKey(Player.class, PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTParameterizedSql() {
    sorm.insert(PLAYER_ALICE);
    assertThat(
        sorm.readFirst(Player.class, ParameterizedSql.parse("select * from players limit 1")))
            .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTStringObjectArray() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readFirst(Player.class, "select * from players limit ?", 1))
        .isEqualTo(PLAYER_ALICE);
  }


  @Test
  void testReadListClassOfTParameterizedSql() {
    sorm.insert(PLAYER_ALICE);
    assertThat(
        sorm.readList(Player.class, ParameterizedSql.parse("select * from players limit 1")).get(0))
            .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadListClassOfTStringObjectArray() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readList(Player.class, "select * from players limit ?", 1).get(0))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTParameterizedSql() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readOne(Player.class, ParameterizedSql.parse("select * from players limit 1")))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTStringObjectArray() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readOne(Player.class, "select * from players limit ?", 1))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testExists() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.exists(PLAYER_ALICE)).isTrue();
    assertThat(sorm.exists("guests", PLAYER_ALICE)).isFalse();
  }

  @Test
  void testDeleteListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
    sorm.delete(List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(0);

  }

  @Test
  void testDeleteT() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(1);
    sorm.delete(PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(0);
  }

  @Test
  void testDeleteTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
    sorm.delete(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(0);
  }

  @Test
  void testDeleteOnStringListOfT() {
    sorm.getTable(Player.class, PLAYERS1).insert(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
    sorm.deleteIn(PLAYERS1, List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(0);
  }

  @Test
  void testDeleteOnStringT() {
    sorm.insertIn(PLAYERS1, PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(1);
    sorm.deleteIn(PLAYERS1, PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(0);
  }

  @Test
  void testDeleteOnStringTArray() {
    sorm.insertIn(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
    sorm.deleteIn(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(0);
  }

  @Test
  void testDeleteAll() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
    sorm.deleteAll(Player.class);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(0);
  }

  @Test
  void testDeleteAllIn() {
    sorm.insertIn(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
    sorm.deleteAllIn(PLAYERS1);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(0);
  }

  @Test
  void testInsertListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
  }


  @Test
  void testInsertAndGetListOfT() {
    InsertResult<Player> ret = sorm.insertAndGet(List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(ret.getObject().id).isEqualTo(2);
  }

  @Test
  void testInsertAndGetT() {
    InsertResult<Player> ret = sorm.insertAndGet(PLAYER_ALICE);
    assertThat(ret.getObject().id).isEqualTo(1);
  }

  @Test
  void testInsertAndGetTArray() {
    InsertResult<Player> ret = sorm.insertAndGet(PLAYER_ALICE, PLAYER_BOB);
    assertThat(ret.getObject().id).isEqualTo(2);
  }

  @Test
  void testInsertAndGetOnStringListOfT() {
    InsertResult<Player> ret = sorm.insertAndGetIn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(ret.getObject().id).isEqualTo(2);
  }

  @Test
  void testInsertAndGetOnStringT() {
    InsertResult<Player> ret = sorm.insertAndGetIn("players", PLAYER_ALICE);
    assertThat(ret.getObject().id).isEqualTo(1);
  }

  @Test
  void testInsertAndGetOnStringTArray() {
    sorm.insertAndGetIn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testInsertOnStringListOfT() {
    sorm.insertIn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertOnStringT() {
    sorm.insertIn("players", PLAYER_ALICE);
  }

  @Test
  void testInsertOnStringTArray() {
    sorm.insertIn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testmergeListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    sorm.merge(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testmergeT() {
    sorm.insert(PLAYER_ALICE);
    sorm.merge(PLAYER_ALICE);
  }

  @Test
  void testmergeTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.merge(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testmergeInStringListOfT() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.mergeIn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testmergeInStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.mergeIn("players", PLAYER_ALICE);
  }

  @Test
  void testmergeInStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.mergeIn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testUpdateListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    sorm.update(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateT() {
    sorm.insert(PLAYER_ALICE);
    sorm.update(PLAYER_ALICE);
  }

  @Test
  void testUpdateTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.update(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testUpdateOnStringListOfT() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.updateIn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateOnStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.updateIn("players", PLAYER_ALICE);
  }

  @Test
  void testUpdateOnStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.updateIn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testGetTableName() {
    sorm.getTableName(Player.class);
  }

  @Test
  void testGetTableMetaDataClassOfQ() {
    sorm.getTableMetaData(Player.class);
  }

  @Test
  void testGetTableMetaDataClassOfQString() {
    sorm.getTableMetaData("players");
  }

  @Test
  void testOpenMapStream() {
    sorm.acceptHandler(conn -> {
      try (Stream<RowMap> stream = conn.stream(RowMap.class, "select * from players")) {
        assertThat(stream.collect(Collectors.toList()).size()).isEqualTo(0);
      }
    });
    sorm.acceptHandler(conn -> {
      try (Stream<RowMap> stream =
          conn.stream(RowMap.class, ParameterizedSql.of("select * from players"))) {
        assertThat(stream.collect(Collectors.toList()).size()).isEqualTo(0);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Test
  void testInsertMapIn() {
    Map<String, Object> map = Map.of("id", 99, "name", "Test", "address", "Chiba");
    sorm.insertMapIn("players", map);
    sorm.deleteAllIn("players");
    sorm.insertMapIn("players", List.of(map));
    sorm.deleteAllIn("players");
    sorm.insertMapIn("players", new Map[] {map});
  }

  @Test
  void testAcceptPreparedStatementHandler() {
    sorm.executeQuery(con -> con.prepareStatement("select * from players"),
        sorm.getRowMapper(Player.class));
  }

  @Test
  void testApplyPreparedStatementHandler() {
    sorm.executeQuery(con -> con.prepareStatement("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteQueryParameterizedSqlResultSetTraverserOfT() {
    sorm.executeQuery(ParameterizedSql.parse("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteQueryParameterizedSqlRowMapperOfT() {
    sorm.executeQuery(ParameterizedSql.parse("select * from players"),
        sorm.getRowMapper(Player.class));

    sorm.executeQuery(ParameterizedSql.parse("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteUpdateStringObjectArray() {
    sorm.executeUpdate("insert into players values(?,?,?)", 9, "A", "B");
  }

  @Test
  void testExecuteUpdateParameterizedSql() {
    sorm.executeUpdate(ParameterizedSql.parse("insert into players values(?,?,?)", 9, "A", "B"));
  }

}
