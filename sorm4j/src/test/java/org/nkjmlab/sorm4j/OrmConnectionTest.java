package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.InsertResult;
import org.nkjmlab.sorm4j.common.Location;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.common.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple3;
import org.nkjmlab.sorm4j.extension.impl.DefaultColumnFieldMapper;
import org.nkjmlab.sorm4j.internal.sql.result.InsertResultImpl;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class OrmConnectionTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testDelete() {
    sorm.accept(conn -> {
      conn.insert(GUEST_ALICE);
      assertThat(conn.exists(GUEST_ALICE)).isTrue();
      assertThat(conn.exists(GUEST_BOB)).isFalse();
    });


  }


  @Test
  void testJoin() {
    sorm.accept(m -> {
      m.insert(GUEST_ALICE, GUEST_BOB);
      m.insert(PLAYER_ALICE, PLAYER_BOB);
      m.insert(SormTestUtils.LOCATION_TOKYO);
      m.insert(SormTestUtils.LOCATION_KYOTO);

      @SuppressWarnings("unused")
      List<Location> gs = m.readList(Location.class, "select * from players");

      List<Tuple2<Guest, Player>> result = m.readTupleList(Guest.class, Player.class,
          "select g.id as gid, g.name as gname, g.address as gaddress, p.id as pid, p.name as pname, p.address as paddress from guests g join players p on g.id=p.id");

      assertThat(result.get(0).getT1().getClass()).isEqualTo(Guest.class);
      assertThat(result.get(0).getT2().getClass()).isEqualTo(Player.class);
      assertThat(result.get(0).toString()).contains("Alice");

      List<Tuple3<Guest, Player, Location>> result1 =
          m.readTupleList(Guest.class, Player.class, Location.class,
              "select g.id as gid, g.name as gname, g.address as gaddress, "
                  + "p.id as pid, p.name as pname, p.address as paddress, "
                  + "l.id lid, l.name lname " + "from guests g " + "join players p on g.id=p.id "
                  + "join locations l on g.id=l.id");

      assertThat(result1.get(0).getT1().getClass()).isEqualTo(Guest.class);
      assertThat(result1.get(0).getT1().getName()).isEqualTo(GUEST_ALICE.getName());
      assertThat(result1.get(0).getT2().getClass()).isEqualTo(Player.class);
      assertThat(result1.get(0).getT2().getName()).isEqualTo(PLAYER_ALICE.getName());
      assertThat(result1.get(0).getT3().getClass()).isEqualTo(Location.class);
      assertThat(result1.get(0).getT3().getName()).isEqualTo(LOCATION_TOKYO.getName());
      assertThat(result1.get(0).toString()).contains("Alice");


    });
  }

  @Test
  void testNamedRequest1() {
    int row =
        sorm.apply(conn -> conn.createCommand("insert into players values(:id, :name, :address)")
            .bindBean(new Player(1, "Frank", "Tokyo")).executeUpdate());
    assertThat(row).isEqualTo(1);
    sorm.apply(conn -> conn.readAll(Player.class)).get(0);
  }

  @Test
  void testNamedRequest() {
    AtomicInteger id = new AtomicInteger(10);


    int row =
        sorm.apply(conn -> conn.createCommand("insert into players values(:id, :name, :address)")
            .bindAll(Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
            .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.apply(conn -> conn.createCommand("insert into players values(:id, :name, :address)")
        .bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo")
        .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.apply(conn -> {
      NamedParameterSql sql =
          NamedParameterSql.parse("insert into players values(`id`, `name`, `address`)", '`', '`',
              new DefaultColumnFieldMapper());
      sql.bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo");
      return conn.executeUpdate(sql.parse());
    });
    assertThat(row).isEqualTo(1);


    var ret = sorm.apply(conn -> conn.createCommand("select * from players where id=:id")
        .bind("id", id.get()).executeQuery(conn.getResultSetTraverser(Player.class)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testClose() {
    sorm.accept(m -> {
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
    Guest a = GUEST_ALICE;
    sorm.accept(m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    sorm.accept(m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    sorm.accept(m -> {
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertOn("players1", a);
      m.deleteOn("players1", a);
      m.insertOn("players1", a, b);
      m.deleteOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteT() {
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insert(a, b);
      m.delete(a, b);
      assertThat(m.readAll(Player.class).size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(a);
      m.delete(b);
      assertThat(m.readAll(Player.class).size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(List.of(a, b));
      assertThat(m.readAll(Player.class).size()).isEqualTo(0);

    });
  }


  @Test
  void testInsertAndGetOnStringT() {
    assertThat(InsertResultImpl.emptyInsertResult().getRowsModified()[0]).isEqualTo(0);


    Guest a = GUEST_ALICE;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });


    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {


    Guest a = GUEST_ALICE;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of());
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    sorm.accept(m -> {
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
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insertOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    Player a = PLAYER_ALICE;
    sorm.accept(m -> {
      m.insert(a);
      m.executeUpdate("DROP TABLE IF EXISTS PLAYERS1");
      m.executeUpdate(ParameterizedSql.of("DROP TABLE IF EXISTS PLAYERS1", new Object[0]));

    });
  }


  @Test
  void testMergeError() {
    try {
      sorm.accept(m -> {
        Guest a = GUEST_ALICE;
        m.merge(a);
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeOnT() {
    sorm.accept(m -> {
      m.mergeOn("players1", new Player[] {});
      m.merge(new Player[] {});
    });

    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.mergeOn("players1", a);
      m.mergeOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(2);

      m.mergeOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(2);
    });
  }

  @Test
  void testMergesError() {
    try {
      sorm.accept(m -> {
        Guest a = GUEST_ALICE;
        Guest b = GUEST_BOB;
        m.merge(a, b);
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeT() {
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.merge(a);
      m.merge(a, b);
      m.merge(List.of(a, b));
      Player c = new Player(a.getId(), "UPDATED", "UPDATED");
      m.merge(c, b);
      assertThat(m.readAll(Player.class).size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(Player.class, a.getId()).readAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testReadLazy() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;
    sorm.accept(m -> {
      m.insert(List.of(a, b));
      Map<String, Object> map =
          m.readMapLazy(ParameterizedSql.of("select * from players")).toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapFirst(ParameterizedSql.of("select * from players"));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      Player p = m.readLazy(Player.class, "select * from players").toList().get(0);
      assertThat(p).isEqualTo(a);
    });
  }

  @Test
  void testReadAllLazy() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;

    sorm.accept(m -> {
      m.insert(a);

      Map<String, Object> map = m.readMapLazy("select * from players").one();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      try {
        m.readMapLazy("select * from hoge").one();
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }

      try {
        m.readFirst(Player.class, "select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
      try {
        m.readMapFirst("select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }

      try {
        m.readList(Player.class, "select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
      try {
        m.readMapList("select * from hoge");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }



      assertThat(m.readAllLazy(Player.class).one()).isEqualTo(a);
      m.insert(b);

      try {
        assertThat(m.readAllLazy(Player.class).one()).isEqualTo(a);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
      try {
        assertThat(m.readMapLazy("select * from players").one()).isEqualTo(a);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
      try {
        assertThat(m.readAllLazy(Player.class).one()).isEqualTo(a);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
      try {
        assertThat(m.readMapOne("select * from players")).isEqualTo(a);
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


      assertThat(m.readAllLazy(Player.class).stream().collect(Collectors.toList())).contains(a, b);
      assertThat(m.readAllLazy(Player.class).toList()).contains(a, b);
      assertThat(m.readAllLazy(Player.class).first()).isEqualTo(a);

      map = m.readMapLazy("select * from players").first();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapLazy("select * from players").toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
      assertThat(map.get("ADDRESS") != null ? map.get("ADDRESS") : map.get("address"))
          .isEqualTo(a.readAddress());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapList("select * from players").get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapList(ParameterizedSql.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapOne(OrderedParameterSql.parse("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapLazy(ParameterizedSql.of("select * from players")).toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(m -> {
      try {
        m.readMapLazy("select * from players").one();
        failBecauseExceptionWasNotThrown(SormException.class);
      } catch (SormException e) {
        assertThat(e.getMessage()).contains("Non-unique");
      }
    });


  }

  @Test
  void testReadByPrimaryKey() {
    sorm.accept(m -> {
      Guest a = GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(Guest.class, 1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.accept(m -> {
      Player a = PLAYER_ALICE;
      Player b = PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList(Player.class, "select * from players")).contains(a, b);
      assertThat(m.readList(Player.class, ParameterizedSql.of("select * from players"))).contains(a,
          b);
      assertThat(
          m.readOne(Player.class, OrderedParameterSql.parse("select * from players where id=?", 1)))
              .isEqualTo(a);
      assertThat(m.readOne(Player.class, "select * from players where id=?", 1)).isEqualTo(a);


    });
  }

  @Test
  void testReadOne() {
    try {
      sorm.accept(m -> {
        Guest a = GUEST_ALICE;
        Guest b = GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g =
            m.readOne(Guest.class, OrderedParameterSql.parse("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(Guest.class, ParameterizedSql.of("select * from guests"));
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("Non-unique");
    }

  }


  @Test
  void testTransaction() {
    Guest a = GUEST_ALICE;
    sorm.acceptTransactionHandler(m -> {
      m.insert(a);
      m.rollback();
    });


    sorm.acceptTransactionHandler(m -> {
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(Guest.class, ParameterizedSql.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testTransactionLevel() {



    Sorm orm = Sorm.builder().setDataSource(sorm.getDataSource())
        .setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).build();

    orm.acceptTransactionHandler(m -> {
      assertThat(m.getJdbcConnection().getTransactionIsolation())
          .isEqualTo(Connection.TRANSACTION_SERIALIZABLE);
    });
    sorm.acceptTransactionHandler(m -> {
      assertThat(m.getJdbcConnection().getTransactionIsolation())
          .isEqualTo(Connection.TRANSACTION_READ_COMMITTED);
    });
  }


  @Test
  void testUpdateOnT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    sorm.accept(m -> {
      m.insert(a);
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(Player.class, a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(Player.class, b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testUpdateT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    sorm.accept(m -> {
      m.insert(a);
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.update(List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(Player.class, a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(Player.class, b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

}
