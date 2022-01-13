package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Location;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.result.Tuple3;

class CommandTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testAcceptPreparedStatementHandler() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
      conn.executeQuery(con -> {
        PreparedStatement stmt = con.prepareStatement("select * from guests where id=?");
        stmt.setInt(1, 1);
        return stmt;
      }, conn.getRowMapper(Guest.class));
    });
  }

  @Test
  void testApplyPreparedStatementHandler() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));

      conn.executeQuery(con -> {
        PreparedStatement stmt = con.prepareStatement("select * from guests where id=?");
        stmt.setInt(1, 1);
        return stmt;
      }, conn.getResultSetToMapTraverser());

      conn.createCommand("select * from guests where id=:id", Map.of("id", 1));

    });
  }

  @Test
  void testExecuteQueryFunctionHandlerOfResultSetT() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
      Player p = conn.createCommand("select * from players where id=?", 1)
          .executeQuery(conn.getResultSetTraverser(Player.class)).get(0);
      assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
    });
  }

  @Test
  void testExecuteQueryRowMapperOfT() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
      List<Player> p = conn.createCommand("select * from players where id=?", 1)
          .executeQuery(conn.getResultSetTraverser(Player.class));
      assertThat(p.size()).isEqualTo(1);
    });
  }

  @Test
  void testExecuteUpdate() {
    sorm.accept(conn -> {
      int m = conn.createCommand("insert into players values(?,?,?)", PLAYER_CAROL.getId(),
          PLAYER_CAROL.getName(), PLAYER_CAROL.readAddress()).executeUpdate();
      assertThat(m).isEqualTo(1);
    });
  }

  @Test
  void testReadFirst() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Player p = conn.createCommand("select * from players").readFirst(Player.class);
      assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
    });
  }

  @Test
  void testReadLazy() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Player p =
          conn.createCommand("select * from players").readStream(Player.class).toList().get(0);
      assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
    });
  }

  @Test
  void testReadList() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Player p = conn.createCommand("select * from players").readList(Player.class).get(0);
      assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
    });
  }

  @Test
  void testReadOne() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
      Player p = conn.createCommand("select * from players").readOne(Player.class);
      assertThat(p).isEqualTo(SormTestUtils.PLAYER_ALICE);
    });
  }

  @Test
  void testReadMapFirst() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Map<String, Object> map = conn.createCommand("select * from players").readMapFirst();
      assertThat(map.get("name")).isEqualTo(SormTestUtils.PLAYER_ALICE.getName());
    });
  }

  @Test
  void testReadMapLazy() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Map<String, Object> map =
          conn.createCommand("select * from players").readMapStream().toList().get(0);
      assertThat(map.get("name")).isEqualTo(SormTestUtils.PLAYER_ALICE.getName());
    });
  }

  @Test
  void testReadMapList() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      Map<String, Object> map = conn.createCommand("select * from players").readMapList().get(0);
      assertThat(map.get("name")).isEqualTo(SormTestUtils.PLAYER_ALICE.getName());
    });
  }

  @Test
  void testReadMapOne() {
    sorm.accept(conn -> {
      conn.insert(List.of(SormTestUtils.PLAYER_ALICE));
      Map<String, Object> map = conn.createCommand("select * from players").readMapOne();
      assertThat(map.get("name")).isEqualTo(SormTestUtils.PLAYER_ALICE.getName());
    });
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      conn.insert(List.of(GUEST_ALICE, GUEST_BOB));

      String sql = ParameterizedSql.embedParameter(
          "select {?}, {?} from players join guests on players.id=guests.id where players.id=?",
          conn.getTableMetaData(Player.class).getColumnAliases(),
          conn.getTableMetaData(Guest.class).getColumnAliases());

      List<Tuple2<Player, Guest>> ret =
          conn.createCommand(sql, PLAYER_ALICE.getId()).readTupleList(Player.class, Guest.class);
      assertThat(ret.get(0).getT1().getName()).isEqualTo(PLAYER_ALICE.getName());
      assertThat(ret.get(0).getT2().getName()).isEqualTo(GUEST_ALICE.getName());

    });
  }

  @Test
  void testReadTupleListClassOfT1ClassOfT2ClassOfT3() {
    sorm.accept(conn -> {
      conn.insert(List.of(PLAYER_ALICE, PLAYER_BOB));
      conn.insert(List.of(GUEST_ALICE, GUEST_BOB));
      conn.insert(List.of(LOCATION_TOKYO, LOCATION_KYOTO));

      String sql = ParameterizedSql.embedParameter(
          "select {?}, {?}, {?} from players join guests on players.id=guests.id "
              + " join locations on players.id=locations.id " + " where players.id=?",
          conn.getTableMetaData(Player.class).getColumnAliases(),
          conn.getTableMetaData(Guest.class).getColumnAliases(),
          conn.getTableMetaData(Location.class).getColumnAliases());

      List<Tuple3<Player, Guest, Location>> ret = conn.createCommand(sql, PLAYER_ALICE.getId())
          .readTupleList(Player.class, Guest.class, Location.class);
      assertThat(ret.get(0).getT1().getName()).isEqualTo(PLAYER_ALICE.getName());
      assertThat(ret.get(0).getT2().getName()).isEqualTo(GUEST_ALICE.getName());
      assertThat(ret.get(0).getT3().getName()).isEqualTo(LOCATION_TOKYO.getName());

    });
  }

  @Test
  void testOrderedRequest() {
    AtomicInteger id = new AtomicInteger(10);
    int row = sorm.apply(conn -> conn.createCommand("insert into players values(?,?,?)")
        .addParameter(id.incrementAndGet(), "Frank", "Tokyo").executeUpdate());
    assertThat(row).isEqualTo(1);

    List<Player> ret = sorm.apply(conn -> conn
        .createCommand("select * from players where id=? and name=?")
        .addParameter(id.get(), "Frank").executeQuery(conn.getResultSetTraverser(Player.class)));

    assertThat(ret.size()).isEqualTo(1);

    row = sorm.apply(conn -> conn.createCommand("insert into players values(?,?,?)")
        .addParameter(id.incrementAndGet()).addParameter("Frank").addParameter("Tokyo")
        .executeUpdate());
    assertThat(row).isEqualTo(1);


    ret = sorm.apply(conn -> conn.createCommand("select * from players where id=?")
        .addParameter(id.get()).executeQuery(conn.getRowMapper(Player.class)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testCommand() {

    sorm.apply(
        conn -> conn.createCommand(ParameterizedSql.parse("select * from players where id=?", 1))
            .readList(Player.class));

    sorm.apply(conn -> conn
        .createCommand(
            ParameterizedSql.parse("select * from players where id=:id", Map.of("id", 1)))
        .readList(Player.class));
    sorm.apply(conn -> conn.getRowToMapMapper());
    sorm.apply(conn -> conn.getTableMetaData("players"));
  }


}
