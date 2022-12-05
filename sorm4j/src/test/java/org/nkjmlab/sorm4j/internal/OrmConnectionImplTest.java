package org.nkjmlab.sorm4j.internal;

import static java.sql.Connection.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.sql.NamedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.OrderedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.command.Command;

class OrmConnectionImplTest {
  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testDelete() {
    sorm.acceptHandler(conn -> {
      conn.insert(PLAYER_ALICE);
      assertThat(conn.exists(PLAYER_ALICE)).isTrue();
      assertThat(conn.exists(PLAYER_BOB)).isFalse();
      assertThat(conn.exists("players", PLAYER_ALICE)).isTrue();
      assertThat(conn.exists("players", PLAYER_BOB)).isFalse();
      conn.readFirst(Guest.class, "select * from players");
    });
  }

  @Test
  void testJoin() {
    sorm.insert(GUEST_ALICE, GUEST_BOB);
    sorm.insert(PLAYER_ALICE, PLAYER_BOB);
    sorm.insert(SormTestUtils.SOCCER);
    sorm.insert(SormTestUtils.TENNIS);

    List<Tuple2<Guest, Player>> result =
        sorm.joinOn(Guest.class, Player.class, "guests.id=players.id");

    assertThat(result.get(0).getT1().getClass()).isEqualTo(Guest.class);
    assertThat(result.get(0).getT2().getClass()).isEqualTo(Player.class);
    assertThat(result.get(0).toString()).contains("Alice");

    List<Tuple3<Guest, Player, Sport>> result1 = sorm.joinOn(Guest.class, Player.class, Sport.class,
        "guests.id=players.id", "players.id=sports.id");

    assertThat(result1.get(0).getT1().getClass()).isEqualTo(Guest.class);
    assertThat(result1.get(0).getT1().getName()).isEqualTo(GUEST_ALICE.getName());
    assertThat(result1.get(0).getT2().getClass()).isEqualTo(Player.class);
    assertThat(result1.get(0).getT2().getName()).isEqualTo(PLAYER_ALICE.getName());
    assertThat(result1.get(0).getT3().getClass()).isEqualTo(Sport.class);
    assertThat(result1.get(0).getT3().getName()).isEqualTo(TENNIS.getName());
    assertThat(result1.get(0).toString()).contains("Alice");

  }

  @Test
  void testTupleList() {
    sorm.acceptHandler(m -> {
      m.insert(GUEST_ALICE, GUEST_BOB);
      m.insert(PLAYER_ALICE, PLAYER_BOB);
      m.insert(SormTestUtils.SOCCER);
      m.insert(SormTestUtils.TENNIS);

      List<Tuple2<Guest, Player>> result = m.readTupleList(Guest.class, Player.class,
          "select g.id as gid, g.name as gname, g.address as gaddress, p.id as pid, p.name as pname, p.address as paddress from guests g join players p on g.id=p.id");

      assertThat(result.get(0).getT1().getClass()).isEqualTo(Guest.class);
      assertThat(result.get(0).getT2().getClass()).isEqualTo(Player.class);
      assertThat(result.get(0).toString()).contains("Alice");

      List<Tuple3<Guest, Player, Sport>> result1 =
          m.readTupleList(Guest.class, Player.class, Sport.class,
              "select g.id as gid, g.name as gname, g.address as gaddress, "
                  + "p.id as pid, p.name as pname, p.address as paddress, "
                  + "s.id sportdotid, s.name sportdotname " + "from guests g "
                  + "join players p on g.id=p.id " + "join sports s on g.id=s.id");

      assertThat(result1.get(0).getT1().getClass()).isEqualTo(Guest.class);
      assertThat(result1.get(0).getT1().getName()).isEqualTo(GUEST_ALICE.getName());
      assertThat(result1.get(0).getT2().getClass()).isEqualTo(Player.class);
      assertThat(result1.get(0).getT2().getName()).isEqualTo(PLAYER_ALICE.getName());
      assertThat(result1.get(0).getT3().getClass()).isEqualTo(Sport.class);
      assertThat(result1.get(0).getT3().getName()).isEqualTo(TENNIS.getName());
      assertThat(result1.get(0).toString()).contains("Alice");


    });
  }

  @Test
  void testNamedRequest1() {
    int row = sorm.applyHandler(
        conn -> Command.create(conn, "insert into players values(:id, :name, :address)")
            .bindBean(new Player(1, "Frank", "Tokyo")).executeUpdate());
    assertThat(row).isEqualTo(1);
  }


  @Test
  void testNamedRequest() {
    AtomicInteger id = new AtomicInteger(10);


    int row = sorm.applyHandler(
        conn -> Command.create(conn, "insert into players values(:id, :name, :address)")
            .bindAll(Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
            .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.applyHandler(
        conn -> Command.create(conn, "insert into players values(:id, :name, :address)")
            .bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo")
            .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.applyHandler(conn -> {
      NamedParameterSqlParser sql =
          NamedParameterSqlParser.of("insert into players values(`id`, `name`, `address`)", '`',
              '`', new DefaultColumnToFieldAccessorMapper());
      sql.bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo");
      return conn.executeUpdate(sql.parse());
    });
    assertThat(row).isEqualTo(1);


    var ret = sorm.applyHandler(conn -> Command.create(conn, "select * from players where id=:id")
        .bind("id", id.get()).executeQuery(conn.getResultSetTraverser(Player.class)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testClose() {
    sorm.acceptHandler(m -> {
      m.close();
      try {
        assertThat(m.getJdbcConnection().isClosed()).isTrue();
      } catch (SQLException e) {
        fail();
      }
    });
  }


  @Test
  void testCommint() {
    sorm.acceptHandler(TRANSACTION_READ_COMMITTED, m -> {
      m.insert(PLAYER_ALICE);
      Player p = m.readOne(Player.class, "SELECT * FROM PLAYERS");
      assertThat(p.getName()).isEqualTo(PLAYER_ALICE.getName());
      // auto roll-back;
    });
    sorm.acceptHandler(TRANSACTION_READ_COMMITTED, m -> {
      m.insert(PLAYER_ALICE);
      m.commit();
      m.close();
    });
    sorm.acceptHandler(m -> {
      Player p = m.readOne(Player.class, "SELECT * FROM PLAYERS");
      assertThat(p.getName()).isEqualTo(PLAYER_ALICE.getName());
    });
  }

  @Test
  void testDeleteOnStringT() {
    sorm.acceptHandler(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertIn("players1", a);
      m.deleteIn("players1", a);
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
      m.insertIn("players1", a, b);
      m.deleteIn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteT() {
    sorm.acceptHandler(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insert(a, b);
      m.delete(a, b);
      assertThat(m.selectAll(Player.class).size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(a);
      m.delete(b);
      assertThat(m.selectAll(Player.class).size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(List.of(a, b));
      assertThat(m.selectAll(Player.class).size()).isEqualTo(0);

    });
  }


  @Test
  void testInsertAndGetOnStringT() {
    Guest a = GUEST_ALICE;
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGet(new Guest[] {});
      assertThat(g.countRowsModified()).isEqualTo(0);
    });


    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGet(a);
      assertThat(g.getGeneratedKeys().get("id")).isEqualTo(1);
    });
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGetIn("players1", a);
      assertThat(g.getGeneratedKeys().get("id")).isNull();
    });
  }

  @Test
  void testInsertAndGetOnList() {
    Guest a = GUEST_ALICE;
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGetIn("players1", List.of());
      assertThat(g.toString()).contains("InsertResult");
      assertThat(g.countRowsModified()).isEqualTo(0);
    });
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGet(List.of(a));
      assertThat(g.getGeneratedKeys().get("id")).isEqualTo(1);
    });
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGetIn("guests", List.of(GUEST_BOB));
      assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGet(a, b);
      assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    sorm.acceptHandler(m -> {
      InsertResult g = m.insertAndGetIn("guests", a, b);
      assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    sorm.acceptHandler(m -> {
      Guest a = GUEST_ALICE;
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
      m.deleteAll(Guest.class);
      assertThat(m.readList(Guest.class, "select * from guests").size()).isEqualTo(0);
    });
  }

  @Test
  void testInsertOnStringT() {
    sorm.acceptHandler(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertIn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteAllIn("players1");
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
    sorm.acceptHandler(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertIn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteIn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    sorm.acceptHandler(m -> {
      m.insert(PLAYER_ALICE);
      assertThat(m.executeUpdate("delete from players")).isEqualTo(1);
      m.insert(PLAYER_ALICE, PLAYER_BOB);
      assertThat(m.executeUpdate(ParameterizedSql.of("delete from players", new Object[0])))
          .isEqualTo(2);;

    });
  }


  @Test
  void testMergeError() {
    try {
      sorm.acceptHandler(m -> {
        Guest a = GUEST_ALICE;
        m.merge(a);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Parameter \"#3\" is not set;");
    }
  }

  @Test
  void testmergeInT() {
    sorm.mergeIn("players1", new Player[] {});
    sorm.merge(new Player[] {});

    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;
    sorm.mergeIn("players1", a);
    sorm.mergeIn("players1", a, b);
    assertThat(sorm.readList(Player.class, "select * from players1").size()).isEqualTo(2);

    sorm.mergeIn("players1", List.of(a, b));
    assertThat(sorm.readList(Player.class, "select * from players1").size()).isEqualTo(2);
  }

  @Test
  void testMergesError() {
    try {
      sorm.acceptHandler(m -> {
        Guest a = GUEST_ALICE;
        Guest b = GUEST_BOB;
        m.merge(a, b);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("SQL");
    }
  }

  @Test
  void testMergeT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;
    sorm.merge(a);
    sorm.merge(a, b);
    sorm.merge(List.of(a, b));
    Player c = new Player(a.getId(), "UPDATED", "UPDATED");
    sorm.merge(c, b);
    assertThat(sorm.selectAll(Player.class).size()).isEqualTo(2);
    assertThat(sorm.selectByPrimaryKey(Player.class, a.getId()).readAddress()).isEqualTo("UPDATED");
  }


  @Test
  void testReadAllLazy() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;

    sorm.acceptHandler(m -> {
      m.insert(a, b);

      try {
        m.readFirst(Player.class, "select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }

      try {
        m.readList(Player.class, "select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }



      assertThat(m.readList(Integer.class, "select id from players")).contains(1, 2);


      try {
        m.readList(Integer.class, "select * from players");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("but 1 column was expected to load data into");
      }
    });


  }

  @Test
  void testReadByPrimaryKey() {
    sorm.acceptHandler(m -> {
      Guest a = GUEST_ALICE;
      m.insert(a);
      Guest g = m.selectByPrimaryKey(Guest.class, 1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.acceptHandler(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList(Player.class, "select * from players")).contains(a, b);
      assertThat(m.readList(Player.class, ParameterizedSql.of("select * from players"))).contains(a,
          b);
      assertThat(m.readOne(Player.class,
          OrderedParameterSqlParser.parse("select * from players where id=?", 1))).isEqualTo(a);
      assertThat(m.readOne(Player.class, "select * from players where id=?", 1)).isEqualTo(a);


    });
  }

  @Test
  void testReadOne() {
    try {
      sorm.acceptHandler(m -> {
        Guest a = GUEST_ALICE;
        Guest b = GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(Guest.class,
            OrderedParameterSqlParser.parse("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(Guest.class, ParameterizedSql.of("select * from guests"));
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("non-unique");
    }

  }


  @Test
  void testTransaction() {
    Guest a = GUEST_ALICE;
    sorm.acceptHandler(TRANSACTION_READ_COMMITTED, m -> {
      m.insert(a);
      m.rollback();
    });


    sorm.acceptHandler(TRANSACTION_READ_COMMITTED, m -> {
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(Guest.class, ParameterizedSql.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testTransactionLevel() {
    Sorm orm = Sorm.create(sorm.getDataSource(), SormContext.builder().build());

    orm.acceptHandler(Connection.TRANSACTION_SERIALIZABLE, m -> {
      assertThat(m.getJdbcConnection().getTransactionIsolation())
          .isEqualTo(Connection.TRANSACTION_SERIALIZABLE);
    });
  }


  @Test
  void testUpdateOnT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    sorm.acceptHandler(m -> {
      m.insert(a);
      m.updateIn("players", new Player(a.getId(), "UPDATED", "UPDATED"));
      m.updateIn("players", new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.updateIn("players", List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.selectByPrimaryKey(Player.class, a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.selectByPrimaryKey(Player.class, b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testUpdateT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    sorm.acceptHandler(m -> {
      m.insert(a);
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.update(List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.selectByPrimaryKey(Player.class, a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.selectByPrimaryKey(Player.class, b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

}
