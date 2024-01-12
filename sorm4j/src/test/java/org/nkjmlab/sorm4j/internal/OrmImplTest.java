package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class OrmImplTest {

  private static final String PLAYERS1 = "players1";
  private static final String GUEST = "guests";

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testReadAll() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.selectAll(Player.class)).contains(PLAYER_ALICE);

    assertThatThrownBy(
            () ->
                sorm.readFirst(
                    LocalDateTime.class,
                    ParameterizedSqlParser.parse("select count(*) from players limit 1")))
        .isInstanceOf(SormException.class);
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
            sorm.readFirst(
                Player.class, ParameterizedSqlParser.parse("select * from players limit 1")))
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
            sorm.readList(
                    Player.class, ParameterizedSqlParser.parse("select * from players limit 1"))
                .get(0))
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
    assertThat(
            sorm.readOne(
                Player.class, ParameterizedSqlParser.parse("select * from players limit 1")))
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
    sorm.insertInto(PLAYERS1, PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(1);
    sorm.deleteIn(PLAYERS1, PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(0);
  }

  @Test
  void testDeleteOnStringTArray() {
    sorm.insertInto(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
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
    sorm.insertInto(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
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
    InsertResult ret = sorm.insertAndGet(List.of(GUEST_ALICE, GUEST_BOB));
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(2);
  }

  @Test
  void testInsertAndGetT() {
    InsertResult ret = sorm.insertAndGet(GUEST_ALICE);
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(1);
  }

  @Test
  void testInsertAndGetTArray() {
    InsertResult ret = sorm.insertAndGet(GUEST_ALICE, GUEST_BOB);
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(2);
  }

  @Test
  void testInsertAndGetOnStringListOfT() {
    InsertResult ret = sorm.insertAndGetIn(GUEST, List.of(GUEST_ALICE, GUEST_BOB));
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(2);
  }

  @Test
  void testInsertAndGetOnStringT() {
    InsertResult ret = sorm.insertAndGetIn(GUEST, GUEST_ALICE);
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(1);
  }

  @Test
  void testInsertAndGetOnStringTArray() {
    InsertResult ret = sorm.insertAndGetIn(GUEST, GUEST_ALICE, GUEST_BOB);
    assertThat(ret.getGeneratedKeys().get("id")).isEqualTo(2);
  }

  @Test
  void testInsertOnStringListOfT() {
    sorm.insertInto(PLAYERS1, List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
  }

  @Test
  void testInsertOnStringT() {
    sorm.insertInto(PLAYERS1, PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(1);
  }

  @Test
  void testInsertOnStringTArray() {
    sorm.insertInto(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
  }

  @Test
  void testmergeListOfT() {
    sorm.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
    sorm.merge(List.of(PLAYER_ALICE, PLAYER_CAROL));
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(3);
  }

  @Test
  void testmergeT() {
    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(1);
    sorm.merge(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
  }

  @Test
  void testmergeTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(2);
    sorm.merge(PLAYER_ALICE, PLAYER_CAROL);
    assertThat(sorm.getTable(Player.class).count()).isEqualTo(3);
  }

  @Test
  void testmergeInStringListOfT() {
    sorm.insertInto(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(2);
    sorm.mergeIn(PLAYERS1, List.of(PLAYER_ALICE, PLAYER_CAROL));
    assertThat(sorm.getTable(Player.class, PLAYERS1).count()).isEqualTo(3);
  }

  @Test
  void testmergeInStringT() {
    assertThat(sorm.getTable(Player.class));

    sorm.insert(PLAYER_ALICE);
    sorm.mergeIn(PLAYERS1, PLAYER_ALICE);
  }

  @Test
  void testmergeInStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.mergeIn(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
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
    sorm.updateWith(PLAYERS1, List.of(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testUpdateOnStringT() {
    sorm.insert(PLAYER_ALICE);
    sorm.updateWith(PLAYERS1, PLAYER_ALICE);
  }

  @Test
  void testUpdateOnStringTArray() {
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.updateWith(PLAYERS1, PLAYER_ALICE, PLAYER_BOB);
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
    sorm.getJdbcTableMetaData(PLAYERS1);
  }

  @Test
  void testOpenMapStream() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(PLAYER_ALICE);
          ResultSetStream<RowMap> stream =
              conn.stream(RowMap.class, ParameterizedSql.of("select * from players"));
          int ret = stream.apply(strm -> strm.collect(Collectors.toList()).size());
          assertThat(ret).isEqualTo(1);

          ResultSetStream<Player> strm1 = conn.streamAll(Player.class);
          int ret1 = strm1.apply(strm -> strm.collect(Collectors.toList()).size());
          assertThat(ret1).isEqualTo(1);

          assertThat(conn.getTableSql(PLAYERS1).toString()).contains("PLAYERS1");

          assertThat(conn.mapToTable(Player.class).count()).isEqualTo(1);
          conn.mapToTable(Player.class, PLAYERS1).insert(PLAYER_ALICE, PLAYER_BOB, PLAYER_CAROL);

          assertThat(conn.mapToTable(Player.class, PLAYERS1).count()).isEqualTo(3);
        });
  }

  @Test
  void testInsertMapIn() {

    RowMap map = RowMap.of("id", 99, "name", "Test", "address", "Chiba");
    sorm.insertMapInto(PLAYERS1, map);
    sorm.deleteAllIn(PLAYERS1);
    sorm.insertMapInto(PLAYERS1, List.of(map));
    sorm.deleteAllIn(PLAYERS1);
    sorm.insertMapInto(PLAYERS1, new RowMap[] {map});
  }

  @Test
  void testAcceptPreparedStatementHandler() {
    sorm.executeQuery(
        con -> con.prepareStatement("select * from players"), sorm.getRowMapper(Player.class));
  }

  @Test
  void testApplyPreparedStatementHandler() {
    sorm.executeQuery(
        con -> con.prepareStatement("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteQueryParameterizedSqlResultSetTraverserOfT() {
    sorm.executeQuery(
        ParameterizedSqlParser.parse("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteQueryParameterizedSqlRowMapperOfT() {
    sorm.executeQuery(
        ParameterizedSqlParser.parse("select * from players"), sorm.getRowMapper(Player.class));

    sorm.executeQuery(
        ParameterizedSqlParser.parse("select * from players"),
        sorm.getResultSetTraverser(Player.class));
  }

  @Test
  void testExecuteUpdateStringObjectArray() {
    sorm.executeUpdate("insert into players values(?,?,?)", 9, "A", "B");
  }

  @Test
  void testExecuteUpdateParameterizedSql() {
    sorm.executeUpdate(
        ParameterizedSqlParser.parse("insert into players values(?,?,?)", 9, "A", "B"));
  }
}
