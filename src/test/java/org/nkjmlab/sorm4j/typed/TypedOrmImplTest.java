package org.nkjmlab.sorm4j.typed;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class TypedOrmImplTest {

  private static Sorm sorm;
  private static TypedOrm<Player> orm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    orm = sorm.getOrm().type(Player.class);
  }

  @BeforeEach
  void setUpEach() {
    orm.deleteAll();
  }


  @Test
  void testReadAll() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readAll()).contains(PLAYER_ALICE);
  }


  @Test
  void testReadByPrimaryKey() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readByPrimaryKey(PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readFirst(ParameterizedSql.parse("select * from players limit 1")))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadFirstClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readFirst("select * from players limit ?", 1)).isEqualTo(PLAYER_ALICE);
  }


  @Test
  void testReadListClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readList(ParameterizedSql.parse("select * from players limit 1")).get(0))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadListClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readList("select * from players limit ?", 1).get(0)).isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTParameterizedSql() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readOne(ParameterizedSql.parse("select * from players limit 1")))
        .isEqualTo(PLAYER_ALICE);
  }

  @Test
  void testReadOneClassOfTStringObjectArray() {
    orm.insert(PLAYER_ALICE);
    assertThat(orm.readOne("select * from players limit ?", 1)).isEqualTo(PLAYER_ALICE);
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
    orm.getRowMapper();
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
    orm.deleteAll();
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
    orm.getTableName();
  }

  @Test
  void testGetTableMetaDataClassOfQ() {
    orm.getTableMetaData();
  }

  @Test
  void testGetTableMetaDataClassOfQString() {
    orm.getTableMetaData("players");
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
    orm.executeQuery(ParameterizedSql.parse("select * from players"), orm.getResultSetTraverser());
  }

  @Test
  void testExecuteQueryParameterizedSqlRowMapperOfT() {
    orm.executeQuery(ParameterizedSql.parse("select * from players"), orm.getRowMapper());
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
  void testUnType() {
    orm.untype();
  }

}
