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
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class OrmConnectionTest {

  private Sorm srv;

  @BeforeEach
  void setUp() {
    srv = OrmTestUtils.createSorm();
    OrmTestUtils.dropAndCreateTable(srv, Guest.class);
    OrmTestUtils.dropAndCreateTable(srv, Player.class);
  }

  @Test
  void testClose() {
    srv.run(m -> {
      m.close();
      try {
        assertThat(m.getJdbcConnection().isClosed()).isTrue();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }


  @Test
  void testCommint() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    srv.run(m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    srv.run(m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    srv.run(m -> {
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a);
      m.deleteOn("players1", a);
      m.insertOn("players1", a, b);
      m.deleteOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteT() {
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
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
    assertThat(InsertResult.empty().getRowsModified()[0]).isEqualTo(0);


    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {
    assertThat(InsertResult.empty().getRowsModified()[0]).isEqualTo(0);


    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGet(List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    Guest b = OrmTestUtils.GUEST_BOB;
    srv.run(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    srv.run(m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
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
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    String sql = "select * from players where id=?";
    srv.run(m -> {
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
      srv.run(m -> {
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
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
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
      srv.run(m -> {
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
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.merge(a);
      m.merge(a, b);
      m.merge(List.of(a, b));
      Player c = new Player(a.getId(), "UPDATED", "UPDATED");
      m.merge(c, b);
      assertThat(m.readAll(Player.class).size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(Player.class, a.getId()).getAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testReadLazy() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_BOB;
    srv.run(m -> {
      m.insert(List.of(a, b));
      Map<String, Object> map =
          m.readLazy(Player.class, SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapFirst(SqlStatement.of("select * from players"));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      Player p = m.readLazy(Player.class, "select * from players").toList().get(0);
      assertThat(p).isEqualTo(a);
    });
  }

  @Test
  void testReadAllLazy() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_BOB;
    srv.run(m -> {
      m.insert(a);

      Map<String, Object> map = m.readAllLazy(Player.class).oneMap();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      assertThat(m.readAllLazy(Player.class).one()).isEqualTo(a);


      m.insert(b);
      assertThat(m.readAllLazy(Player.class).stream().collect(Collectors.toList())).contains(a, b);
      assertThat(m.readAllLazy(Player.class).toList()).contains(a, b);
      assertThat(m.readAllLazy(Player.class).first()).isEqualTo(a);

      map = m.readAllLazy(Player.class).firstMap();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readAllLazy(Player.class).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
      assertThat(map.get("ADDRESS") != null ? map.get("ADDRESS") : map.get("address"))
          .isEqualTo(a.getAddress());
    });
    srv.run(m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(m -> {
      Map<String, Object> map = m.readMapList(SqlStatement.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(m -> {
      Map<String, Object> map =
          m.readMapOne(SqlStatement.of("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    srv.run(m -> {
      Map<String, Object> map =
          m.readMapLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
  }

  @Test
  void testReadByPrimaryKey() {
    srv.run(m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(Guest.class, 1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    srv.run(m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList(Player.class, "select * from players")).contains(a, b);
      assertThat(m.readList(Player.class, SqlStatement.of("select * from players"))).contains(a, b);
      assertThat(m.readOne(Player.class, SqlStatement.of("select * from players where id=?", 1)))
          .isEqualTo(a);
      assertThat(m.readOne(Player.class, "select * from players where id=?", 1)).isEqualTo(a);


    });
  }

  @Test
  void testReadOne() {
    try {
      srv.run(m -> {
        Guest a = OrmTestUtils.GUEST_ALICE;
        Guest b = OrmTestUtils.GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(Guest.class, SqlStatement.of("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(Guest.class, SqlStatement.of("select * from guests"));
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
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(Guest.class, SqlStatement.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
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
  void testUpdateOnT() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_ALICE;
    srv.run(m -> {
      m.insert(a);
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.updateOn("players", List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(Player.class, a.getId());
      assertThat(p.getAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(Player.class, b.getId());
      assertThat(p.getAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testUpdateT() {
    Player a = OrmTestUtils.PLAYER_ALICE;
    Player b = OrmTestUtils.PLAYER_ALICE;
    srv.run(m -> {
      m.insert(a);
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED"));
      m.update(List.of(new Player(a.getId(), "UPDATED", "UPDATED"),
          new Player(b.getId(), "UPDATED", "UPDATED")));
      Player p = m.readByPrimaryKey(Player.class, a.getId());
      assertThat(p.getAddress()).isEqualTo("UPDATED");
      p = m.readByPrimaryKey(Player.class, b.getId());
      assertThat(p.getAddress()).isEqualTo("UPDATED");
    });
  }

}
