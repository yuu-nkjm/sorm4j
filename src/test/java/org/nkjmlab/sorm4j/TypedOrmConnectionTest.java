package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.mapping.TypedOrmConnectionImpl;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class TypedOrmConnectionTest {

  private Sorm srv;

  @BeforeEach
  void setUp() {
    srv = OrmTestUtils.createSorm();
    OrmTestUtils.dropAndCreateTable(srv, Guest.class);
    OrmTestUtils.dropAndCreateTable(srv, Player.class);
    OrmTestUtils.dropAndCreateTable(srv, Location.class);
  }

  @Test
  void testClose() {
    Sorm.of(OrmTestUtils.createDataSourceH2()).getConnectionSource();

    srv.run(Guest.class, m -> {
      m.close();
      try {
        assertThat(m.getJdbcConnection().isClosed()).isTrue();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  @Test
  void testCol() {
    srv.run(Guest.class, m -> {
      assertThat(((TypedOrmConnectionImpl<Guest>) m).getAllColumns())
          .containsAll(List.of("ID", "NAME", "ADDRESS"));
    });
    srv.run(Guest.class, m -> {
      assertThat(((TypedOrmConnectionImpl<Guest>) m).getPrimaryKeys()).containsAll(List.of("ID"));
    });
  }

  @Test
  void testCommint() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    srv.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    srv.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    srv.run(Guest.class, m -> {
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a);
      m.deleteOn("players1", a);
      m.insertOn("players1", a, b);
      m.deleteOn("players1", a, b);
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      m.delete(a, b);
      assertThat(m.readAll().size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(a);
      m.delete(b);
      assertThat(m.readAll().size()).isEqualTo(0);

      m.insert(a, b);
      m.delete(List.of(a, b));
      assertThat(m.readAll().size()).isEqualTo(0);

    });
  }


  @Test
  void testInsertAndGetOnStringT() {
    assertThat(InsertResult.empty().getRowsModified()[0]).isEqualTo(0);


    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {
    assertThat(InsertResult.empty().getRowsModified()[0]).isEqualTo(0);


    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
      m.deleteAll();
      assertThat(m.readList("select * from guests").size()).isEqualTo(0);
    });
  }

  @Test
  void testInsertOnStringT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", List.of(a, b));
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteOn("players1", List.of(a, b));
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    String sql = "select * from players where id=?";
    srv.run(Player.class, m -> {
      m.insert(a);
      m.execute(sql, 1);
      m.executeQuery(sql, 1);
      m.execute(SqlStatement.of(sql, 1));
      m.executeQuery(SqlStatement.of(sql, 1));
      m.executeUpdate("DROP TABLE IF EXISTS PLAYERS1");
      m.executeUpdate(SqlStatement.of("DROP TABLE IF EXISTS PLAYERS1"));

    });
  }


  @Test
  void testMergeError() {
    try {
      srv.run(Guest.class, m -> {
        Guest a = OrmTestUtils.GUEST_ALICE;
        m.merge(a);
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeOnT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.mergeOn("players1", a);
      m.mergeOn("players1", a, b);
      assertThat(m.readList("select * from players1").size()).isEqualTo(2);

      m.mergeOn("players1", List.of(a, b));
      assertThat(m.readList("select * from players1").size()).isEqualTo(2);
    });
  }

  @Test
  void testMergesError() {
    try {
      srv.run(Guest.class, m -> {
        Guest a = OrmTestUtils.GUEST_ALICE;
        Guest b = OrmTestUtils.GUEST_BOB;
        m.merge(a, b);
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.merge(a);
      m.merge(a, b);
      m.merge(List.of(a, b));
      Player c = new Player(a.getId(), "UPDATED", "UPDATED");
      m.merge(c, b);
      assertThat(m.readAll().size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(a.getId()).readAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testReadLazy() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_BOB;
    srv.run(Player.class, m -> {
      m.insert(List.of(a, b));
      Map<String, Object> map =
          m.readLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapFirst(SqlStatement.of("select * from players"));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      Player p = m.readLazy("select * from players").toList().get(0);
      assertThat(p).isEqualTo(a);

      try {
        m.readLazy("select * from players").iterator().remove();
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }



    });
  }

  @Test
  void testReadAllLazy() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_BOB;
    srv.run(Player.class, m -> {
      m.insert(a);

      Map<String, Object> map = m.readAllLazy().oneMap();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      assertThat(m.readAllLazy().one()).isEqualTo(a);


      m.insert(b);
      assertThat(m.readAllLazy().stream().collect(Collectors.toList())).contains(a, b);
      assertThat(m.readAllLazy().toList()).contains(a, b);
      assertThat(m.readAllLazy().first()).isEqualTo(a);

      map = m.readAllLazy().firstMap();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readAllLazy().toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
      assertThat(map.get("ADDRESS") != null ? map.get("ADDRESS") : map.get("address"))
          .isEqualTo(a.readAddress());
    });
    srv.run(Player.class, m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(Player.class, m -> {
      Map<String, Object> map = m.readMapList(SqlStatement.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(Player.class, m -> {
      Map<String, Object> map =
          m.readMapOne(SqlStatement.of("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(Player.class, m -> {
      Map<String, Object> map =
          m.readMapLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
  }

  @Test
  void testReadByPrimaryKey() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList("select * from players")).contains(a, b);
      assertThat(m.readList(SqlStatement.of("select * from players"))).contains(a, b);
      assertThat(m.readOne(SqlStatement.of("select * from players where id=?", 1))).isEqualTo(a);
      assertThat(m.readOne("select * from players where id=?", 1)).isEqualTo(a);
    });
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_BOB;
    List<Player> result = srv.execute(Player.class, m -> m.readList("select * from players"));
    assertThat(result).contains(a, b);
    List<Player> result1 = srv.execute(m -> m.readList(Player.class, "select * from players"));
    assertThat(result1).contains(a, b);
  }

  @Test
  void testReadOne() {
    try {
      srv.run(Guest.class, m -> {
        Guest a = OrmTestUtils.GUEST_ALICE;
        Guest b = OrmTestUtils.GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(SqlStatement.of("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(SqlStatement.of("select * from guests"));
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getCause().getMessage()).contains("Non-unique");
    }

  }

  @Test
  void testSormExeption() {
    try {
      Sorm.of(OrmTestUtils.jdbcUrl, OrmTestUtils.user, OrmTestUtils.password).getConnectionSource()
          .getDataSource();
      fail("Should be fail");
    } catch (Exception e) {

    }
  }

  @Test
  void testToUntyped() {
    srv.getConnection(Guest.class).toUntyped();
  }

  @Test
  void testTransaction() {
    Guest a = OrmTestUtils.GUEST_ALICE;

    srv.runTransaction(m -> {
      m.insert(a);
    }, Connection.TRANSACTION_READ_COMMITTED);

    srv.runTransaction(Guest.class, m -> {
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(SqlStatement.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      m.commit();
    });

    Guest g = srv.executeTransaction(Guest.class, m -> m.readFirst("SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());

    g = srv.executeTransaction(Guest.class, Connection.TRANSACTION_READ_COMMITTED,
        m -> m.readFirst("SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());

  }

  @Test
  void testTransactionLevel() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    srv.runTransaction(Guest.class, Connection.TRANSACTION_SERIALIZABLE, m -> {
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testEnum() {
    srv.run(Location.class, m -> {
      m.insert(new Location(Location.Place.KYOTO));
      assertThat(m.readFirst("SELECT * FROM locations").getName()).isEqualTo(Location.Place.KYOTO);
    });

  }

  @Test
  void testUpdateOnT() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_ALICE;

    // auto-rolback
    srv.executeTransaction(conn -> conn.insert(a));
    // auto-rolback
    srv.runTransaction(conn -> conn.insert(a));
    try (OrmTransaction trans = srv.beginTransaction()) {
      // auto-rolback
      trans.insert(a);
    }
    try (OrmTransaction trans = srv.beginTransaction(Connection.TRANSACTION_READ_COMMITTED)) {
      // auto-rolback
      trans.insert(a);
    }

    try (Connection conn = srv.getJdbcConnection()) {
      Sorm.toOrmConnection(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    srv.run(Player.class, m -> m.runTransaction(conn -> {
      m.insert(a);
      // auto-rolback
    }));


    srv.run(Player.class, m -> {
      m.insert(a);
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testUpdateT() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_ALICE;
    srv.run(Player.class, m -> {
      m.insert(a);
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.update(List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(a.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(b.getId());
      assertThat(p.readAddress()).isEqualTo("UPDATED");
    });
  }

}
