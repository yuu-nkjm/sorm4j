package org.nkjmlab.sorm4j.util.command;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;

class CommandTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testAcceptPreparedStatementHandler() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
          conn.executeQuery(
              con -> {
                PreparedStatement stmt = con.prepareStatement("select * from guests where id=?");
                stmt.setInt(1, 1);
                return stmt;
              },
              conn.getRowMapper(Guest.class));
        });
  }

  @Test
  void testApplyPreparedStatementHandler() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(SormTestUtils.PLAYER_ALICE));

          conn.executeQuery(
              con -> {
                PreparedStatement stmt = con.prepareStatement("select * from guests where id=?");
                stmt.setInt(1, 1);
                return stmt;
              },
              conn.getResultSetTraverser(RowMap.class));

          Command.create(conn, "select * from guests where id=:id", Map.of("id", 1));
        });
  }

  @Test
  void testExecuteQueryFunctionHandlerOfResultSetT() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
          Player p =
              Command.create(conn, "select * from players where id=?", 1)
                  .executeQuery(conn.getResultSetTraverser(Player.class))
                  .get(0);
          assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
        });
  }

  @Test
  void testExecuteQueryRowMapperOfT() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
          List<Player> p =
              Command.create(conn, "select * from players where id=?", 1)
                  .executeQuery(conn.getResultSetTraverser(Player.class));
          assertThat(p.size()).isEqualTo(1);
        });
  }

  @Test
  void testExecuteUpdate() {
    sorm.acceptHandler(
        conn -> {
          int m =
              Command.create(
                      conn,
                      "insert into players values(?,?,?)",
                      PLAYER_CAROL.getId(),
                      PLAYER_CAROL.getName(),
                      PLAYER_CAROL.readAddress())
                  .executeUpdate();
          assertThat(m).isEqualTo(1);
        });
  }

  @Test
  void testReadFirst() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
          Player p = Command.create(conn, "select * from players").readFirst(Player.class);
          assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
        });
  }

  @Test
  void testReadList() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
          Player p = Command.create(conn, "select * from players").readList(Player.class).get(0);
          assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
        });
  }

  @Test
  void testReadOne() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
          Player p = Command.create(conn, "select * from players").readOne(Player.class);
          assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
        });
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
          conn.insert(List.of(GUEST_ALICE, GUEST_BOB));

          String sql =
              ParameterizedSqlParser.embedParameter(
                  "select {?}, {?} from players join guests on players.id=guests.id where players.id=?",
                  String.join(",", conn.getTableMetaData(Player.class).getColumnAliases()),
                  String.join(",", conn.getTableMetaData(Guest.class).getColumnAliases()));

          List<Tuple2<Player, Guest>> ret =
              Command.create(conn, sql, PLAYER_ALICE.getId())
                  .readTupleList(Player.class, Guest.class);
          assertThat(ret.get(0).getT1().getName()).isEqualTo(PLAYER_ALICE.getName());
          assertThat(ret.get(0).getT2().getName()).isEqualTo(GUEST_ALICE.getName());
        });
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2ClassOfT3() {
    sorm.acceptHandler(
        conn -> {
          conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
          conn.insert(List.of(GUEST_ALICE, GUEST_BOB));
          conn.insert(List.of(SOCCER, TENNIS));

          String sql =
              ParameterizedSqlParser.embedParameter(
                  "select {?}, {?}, {?} from players join guests on players.id=guests.id "
                      + " join sports on players.id=sports.id "
                      + " where players.id=?",
                  String.join(",", conn.getTableMetaData(Player.class).getColumnAliases()),
                  String.join(",", conn.getTableMetaData(Guest.class).getColumnAliases()),
                  String.join(",", conn.getTableMetaData(Sport.class).getColumnAliases()));

          List<Tuple3<Player, Guest, Sport>> ret =
              Command.create(conn, sql, PLAYER_ALICE.getId())
                  .readTupleList(Player.class, Guest.class, Sport.class);
          assertThat(ret.get(0).getT1().getName()).isEqualTo(PLAYER_ALICE.getName());
          assertThat(ret.get(0).getT2().getName()).isEqualTo(GUEST_ALICE.getName());
          assertThat(ret.get(0).getT3().getName()).isEqualTo(TENNIS.getName());
        });
  }

  @Test
  void testOrderedRequest() {
    AtomicInteger id = new AtomicInteger(10);
    int row =
        sorm.applyHandler(
            conn ->
                Command.create(conn, "insert into players values(?,?,?)")
                    .addParameter(id.incrementAndGet(), "Frank", "Tokyo")
                    .executeUpdate());
    assertThat(row).isEqualTo(1);

    List<Player> ret =
        sorm.applyHandler(
            conn ->
                Command.create(conn, "select * from players where id=? and name=?")
                    .addParameter(id.get(), "Frank")
                    .executeQuery(conn.getResultSetTraverser(Player.class)));

    assertThat(ret.size()).isEqualTo(1);

    row =
        sorm.applyHandler(
            conn ->
                Command.create(conn, "insert into players values(?,?,?)")
                    .addParameter(id.incrementAndGet())
                    .addParameter("Frank")
                    .addParameter("Tokyo")
                    .executeUpdate());
    assertThat(row).isEqualTo(1);

    ret =
        sorm.applyHandler(
            conn ->
                Command.create(conn, "select * from players where id=?")
                    .addParameter(id.get())
                    .executeQuery(conn.getRowMapper(Player.class)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testCommand() {

    sorm.applyHandler(
        conn ->
            Command.create(
                    conn, ParameterizedSqlParser.parse("select * from players where id=?", 1))
                .readList(Player.class));

    sorm.applyHandler(
        conn ->
            Command.create(
                    conn,
                    ParameterizedSqlParser.parse(
                        "select * from players where id=:id", Map.of("id", 1)))
                .readList(Player.class));
    sorm.applyHandler(conn -> conn.getTableMetaData("players"));
  }
}
