package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class OrmImplTest {

  private static Sorm sorm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @BeforeEach
  void setUpEach() {
    sorm.deleteAll(Player.class);
  }


  @Test
  void testReadAll() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readAll(Player.class)).contains(PLAYER_ALICE);
  }


  @Test
  void testReadByPrimaryKey() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.readByPrimaryKey(Player.class, PLAYER_ALICE.getId())).isEqualTo(PLAYER_ALICE);
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
    sorm.getRowMapper(Player.class);
  }

  @Test
  void testGetResultSetTraverser() {
    sorm.getResultSetToMapTraverser();
  }

  @Test
  void testExists() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.exists(PLAYER_ALICE)).isTrue();
  }

  @Test
  void testDeleteListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    sorm.delete(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testDeleteT() {
    sorm.insert(PLAYER_ALICE);
    sorm.delete(PLAYER_ALICE);
  }

  @Test
  void testDeleteTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.delete(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testDeleteOnStringListOfT() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.deleteOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testDeleteOnStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.deleteOn("players", PLAYER_ALICE);
  }

  @Test
  void testDeleteOnStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.deleteOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testDeleteAll() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.deleteAll(Player.class);
  }

  @Test
  void testDeleteAllOn() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.deleteAllOn("players");
  }

  @Test
  void testInsertListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
  }


  @Test
  void testInsertAndGetListOfT() {
    sorm.insertAndGet(List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertAndGetT() {
    sorm.insertAndGet(PLAYER_ALICE);
  }

  @Test
  void testInsertAndGetTArray() {
    sorm.insertAndGet(PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testInsertAndGetOnStringListOfT() {
    sorm.insertAndGetOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertAndGetOnStringT() {
    sorm.insertAndGetOn("players", PLAYER_ALICE);
  }

  @Test
  void testInsertAndGetOnStringTArray() {
    sorm.insertAndGetOn("players", PLAYER_ALICE, PLAYER_BOB);
  }

  @Test
  void testInsertOnStringListOfT() {
    sorm.insertOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testInsertOnStringT() {
    sorm.insertOn("players", PLAYER_ALICE);
  }

  @Test
  void testInsertOnStringTArray() {
    sorm.insertOn("players", PLAYER_ALICE, PLAYER_BOB);
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
  void testmergeOnStringListOfT() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.mergeOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testmergeOnStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.mergeOn("players", PLAYER_ALICE);
  }

  @Test
  void testmergeOnStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.mergeOn("players", PLAYER_ALICE, PLAYER_BOB);
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
    sorm.updateOn("players", List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateOnStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.updateOn("players", PLAYER_ALICE);
  }

  @Test
  void testUpdateOnStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.updateOn("players", PLAYER_ALICE, PLAYER_BOB);
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
  void testGetRowToMapMapper() {
    sorm.getRowToMapMapper();
  }

  @Test
  void testGetResultSetToMapTraverser() {
    sorm.getResultSetToMapTraverser();
  }

  @Test
  void testReadMapFirstParameterizedSql() {
    sorm.readMapFirst(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapFirstStringObjectArray() {
    sorm.readMapFirst("select * from players");
  }


  @Test
  void testReadMapListParameterizedSql() {
    sorm.readMapList(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapListStringObjectArray() {
    sorm.readMapList("select * from players");
  }

  @Test
  void testReadMapOneParameterizedSql() {
    sorm.readMapOne(ParameterizedSql.parse("select * from players"));
  }

  @Test
  void testReadMapOneStringObjectArray() {
    sorm.readMapOne("select * from players");
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
