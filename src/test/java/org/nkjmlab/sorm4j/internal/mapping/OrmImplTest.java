package org.nkjmlab.sorm4j.internal.mapping;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class OrmImplTest {

  private static Sorm sorm;
  private static Orm orm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    orm = sorm.getOrm();
  }

  @BeforeEach
  void setUpEach() {
    orm.deleteAll(Player.class);
  }


  @Test
  void testReadAll() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readAll(Player.class)).contains(PLAYER_ALICE);
  }


  @Test
  void testReadByPrimaryKey() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readByPrimaryKey(Player.class, PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readFirst(Player.class, ParameterizedSql.parse("select * from players limit 1")))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readFirst(Player.class, "select * from players limit ?", 1))
        .isEqualTo(PLAYER_ALICE);
  }


  @Test
  void testReadListClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(
        orm.readList(Player.class, ParameterizedSql.parse("select * from players limit 1")).get(0))
            .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadListClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readList(Player.class, "select * from players limit ?", 1).get(0))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readOne(Player.class, ParameterizedSql.parse("select * from players limit 1")))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readOne(Player.class, "select * from players limit ?", 1))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2ClassOfT3ParameterizedSql() {
    // fail("Not yet implemented");
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2ClassOfT3StringObjectArray() {
    // fail("Not yet implemented");
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2ParameterizedSql() {
    // fail("Not yet implemented");
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2StringObjectArray() {
    // fail("Not yet implemented");
  }

  @Test
  void testGetRowMapper() {
    orm.getRowMapper(Player.class);
  }

  @Test
  void testGetResultSetTraverser() {
    orm.getResultSetToMapTraverser();
  }

  @Test
  void testExists() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.exists(PLAYER_ALICE)).isTrue();
  }

  @Test
  void testDeleteListOfT() {
    orm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    orm.delete(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testDeleteT() {
    orm.insert(PLAYER_ALICE);
    orm.delete(PLAYER_ALICE);
  }

  @Test
  void testDeleteTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.delete(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testDeleteOnStringListOfT() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.deleteOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testDeleteOnStringT() {
    orm.insert(PLAYER_ALICE);
    orm.deleteOn("players", PLAYER_ALICE);
  }

  @Test
  void testDeleteOnStringTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.deleteOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testDeleteAll() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.deleteAll(Player.class);
  }

  @Test
  void testDeleteAllOn() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.deleteAllOn("players");
  }

  @Test
  void testInsertListOfT() {
    orm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
  }


  @Test
  void testInsertAndGetListOfT() {
    orm.insertAndGet(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertAndGetT() {
    orm.insertAndGet(PLAYER_ALICE);
  }

  @Test
  void testInsertAndGetTArray() {
    orm.insertAndGet(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testInsertAndGetOnStringListOfT() {
    orm.insertAndGetOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertAndGetOnStringT() {
    orm.insertAndGetOn("players", PLAYER_ALICE);
  }

  @Test
  void testInsertAndGetOnStringTArray() {
    orm.insertAndGetOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testInsertOnStringListOfT() {
    orm.insertOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertOnStringT() {
    orm.insertOn("players", PLAYER_ALICE);
  }

  @Test
  void testInsertOnStringTArray() {
    orm.insertOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testmergeListOfT() {
    orm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    orm.merge(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testmergeT() {
    orm.insert(PLAYER_ALICE);
    orm.merge(PLAYER_ALICE);
  }

  @Test
  void testmergeTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.merge(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testmergeOnStringListOfT() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.mergeOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testmergeOnStringT() {
    orm.insert(PLAYER_ALICE);
    orm.mergeOn("players", PLAYER_ALICE);
  }

  @Test
  void testmergeOnStringTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.mergeOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testUpdateListOfT() {
    orm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    orm.update(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateT() {
    orm.insert(PLAYER_ALICE);
    orm.update(PLAYER_ALICE);
  }

  @Test
  void testUpdateTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.update(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testUpdateOnStringListOfT() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.updateOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateOnStringT() {
    orm.insert(PLAYER_ALICE);
    orm.updateOn("players", PLAYER_ALICE);
  }

  @Test
  void testUpdateOnStringTArray() {
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.updateOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testGetTableName() {
    orm.getTableName(Player.class);
  }

  @Test
  void testGetTableMetaDataClassOfQ() {
    orm.getTableMetaData(Player.class);
  }

  @Test
  void testGetTableMetaDataClassOfQString() {
    orm.getTableMetaData(Player.class, "players");
  }

  @Test
  void testGetRowToMapMapper() {
    orm.getRowToMapMapper();
  }

  @Test
  void testGetResultSetToMapTraverser() {
    orm.getResultSetToMapTraverser();
  }

  @Test
  void testReadMapFirstParameterizedSql() {
    orm.readMapFirst(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapFirstStringObjectArray() {
    orm.readMapFirst("select * from players");
  }


  @Test
  void testReadMapListParameterizedSql() {
    orm.readMapList(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapListStringObjectArray() {
    orm.readMapList("select * from players");
  }

  @Test
  void testReadMapOneParameterizedSql() {
    orm.readMapOne(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapOneStringObjectArray() {
    orm.readMapOne("select * from players");
  }

  @Test
  void testAcceptPreparedStatementHandler() {
    orm.acceptPreparedStatementHandler(ParameterizedSql.parse("select * from players"),
        stmt -> stmt.execute());
  }

  @Test
  void testApplyPreparedStatementHandler() {
    orm.applyPreparedStatementHandler(ParameterizedSql.parse("select * from players"),
        stmt -> stmt.execute());
  }

  @Test
  void testExecuteQueryParameterizedSqlResultSetTraverserOfT() {
    orm.executeQuery(ParameterizedSql.parse("select * from players"),
        orm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteQueryParameterizedSqlRowMapperOfT() {
    orm.executeQuery(ParameterizedSql.parse("select * from players"),
        orm.getRowMapper(Player.class));
  }

  @Test
  void testExecuteUpdateStringObjectArray() {
    orm.executeUpdate("insert into players values(?,?,?)", 9, "A", "B");
  }

  @Test
  void testExecuteUpdateParameterizedSql() {
    orm.executeUpdate(ParameterizedSql.parse("insert into players values(?,?,?)", 9, "A", "B"));
  }


  @Test
  void testType() {
    orm.type(Player.class);
  }

}
