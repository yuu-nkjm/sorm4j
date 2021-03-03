package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.mapping.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class TypedOrmConnectionTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sorm, Guest.class);
    SormTestUtils.dropAndCreateTable(sorm, Player.class);
    SormTestUtils.dropAndCreateTable(sorm, Location.class);
  }

  @Test
  void testTableName() {
    sorm.run(Guest.class, m -> {
      assertThat(m.getTableName()).contains("GUESTS");
    });
    sorm.run(Guest.class, m -> {
      int[] g = m.merge(new Guest[] {});
      assertThat(g.length).isEqualTo(0);
    });
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });

    try {
      sorm.run(Guest.class, m -> {
        new DefaultTableNameMapper().toValidTableName("aaa", m.getJdbcConnection().getMetaData());
        failBecauseExceptionWasNotThrown(Exception.class);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match a existing table");
    }
    Guest a = SormTestUtils.GUEST_ALICE;

    sorm.run(Guest.class, conn -> {

      conn.executeTransaction(tr -> {
        return 1;
      });


      OrmConnection orm = Sorm.toUntyped(conn);
      Sorm.toTyped(orm, Guest.class);
      orm.runTransaction(tr -> {
        tr.insert(a);
        Guest g = tr.readFirst(Guest.class, "SELECT * FROM GUESTS");
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        g = tr.readFirst(Guest.class, SqlStatement.of("SELECT * FROM GUESTS"));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
      });
      orm.executeTransaction(tr -> {
        tr.insert(a);
        Guest g = tr.readFirst(Guest.class, "SELECT * FROM GUESTS");
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        g = tr.readFirst(Guest.class, SqlStatement.of("SELECT * FROM GUESTS"));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        tr.commit();
        return 1;
      });



    });

  }

  @Test
  void testClose() {
    Sorm.create(SormTestUtils.createDataSourceH2()).getConnectionSource();

    sorm.run(Guest.class, m -> {
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
    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    sorm.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    sorm.run(Guest.class, m -> {
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", a);
      m.deleteOn("players1", a);
      m.insertOn("players1", a, b);
      m.deleteOn("players1", a, b);
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteT() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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


    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
      m.insertAndGet(new Guest[0]);

    });
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {
    assertThat(InsertResult.empty().getRowsModified()[0]).isEqualTo(0);


    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.run(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    sorm.run(Guest.class, m -> {
      Guest a = SormTestUtils.GUEST_ALICE;
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
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", List.of(a, b));
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteOn("players1", List.of(a, b));
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    Player a = SormTestUtils.PLAYER_ALICE;
    String sql = "select * from players where id=?";
    sorm.run(Player.class, m -> {
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
      sorm.run(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        m.merge(a);
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
    try {
      sorm.run(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        m.merge(a, a, a);
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeOnT() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
      sorm.run(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
        m.merge(a, b);
        failBecauseExceptionWasNotThrown(OrmException.class);
      });
    } catch (OrmException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }
  }

  @Test
  void testMergeT() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.run(Player.class, m -> {
      m.insert(List.of(a, b));
      Map<String, Object> map =
          m.readLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readLazy(SqlStatement.of("select * from players")).toMapList().get(0);
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.run(Player.class, m -> {
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
    sorm.run(Player.class, m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.run(Player.class, m -> {
      Map<String, Object> map =
          m.readMapLazy("select * from players").stream().collect(Collectors.toList()).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.run(Player.class, m -> {
      LazyResultSet<Player> r = m.readLazy("select * from players");
      Iterator<Player> it = r.iterator();
      r.close();
      try {
        it.hasNext();
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("already closed");
      }
    });


    sorm.run(Player.class, m -> {
      Map<String, Object> map = m.readMapList(SqlStatement.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.run(Player.class, m -> {
      Map<String, Object> map =
          m.readMapOne(SqlStatement.of("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.run(Player.class, m -> {
      Map<String, Object> map =
          m.readMapLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
  }

  @Test
  void testReadByPrimaryKey() {
    sorm.run(Guest.class, m -> {
      Guest a = SormTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList("select * from players")).contains(a, b);
      assertThat(m.readList(SqlStatement.of("select * from players"))).contains(a, b);
      assertThat(m.readOne(SqlStatement.of("select * from players where id=?", 1))).isEqualTo(a);
      assertThat(m.readOne("select * from players where id=?", 1)).isEqualTo(a);
    });
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    List<Player> result = sorm.execute(Player.class, m -> m.readList("select * from players"));
    assertThat(result).contains(a, b);
    List<Player> result1 = sorm.execute(m -> m.readList(Player.class, "select * from players"));
    assertThat(result1).contains(a, b);
  }

  @Test
  void testReadOne() {
    try {
      sorm.run(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
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
      Sorm.create(SormTestUtils.jdbcUrl, SormTestUtils.user, SormTestUtils.password)
          .getConnectionSource().getDataSource();
      fail("Should be fail");
    } catch (Exception e) {

    }
  }


  @Test
  void testTransaction() {
    Guest a = SormTestUtils.GUEST_ALICE;

    sorm.runTransaction(Connection.TRANSACTION_READ_COMMITTED, m -> {
      m.insert(a);
    });

    sorm.executeTransaction(m -> m.insert(a));
    sorm.executeTransaction(Connection.TRANSACTION_READ_COMMITTED, m -> m.insert(a));

    sorm.runTransaction(Guest.class, m -> {
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(SqlStatement.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      m.commit();
    });

    Guest g = sorm.executeTransaction(Guest.class, m -> m.readFirst("SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());

    g = sorm.executeTransaction(Guest.class, Connection.TRANSACTION_READ_COMMITTED,
        m -> m.readFirst("SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());

  }

  @Test
  void testTransactionLevel() {
    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.runTransaction(Guest.class, Connection.TRANSACTION_SERIALIZABLE, m -> {
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testEnum() {
    sorm.run(Location.class, m -> {
      m.insert(new Location(Location.Place.KYOTO));
      assertThat(m.readFirst("SELECT * FROM locations").getName()).isEqualTo(Location.Place.KYOTO);
    });

  }

  @Test
  void testUpdateOnT() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_ALICE;

    // auto-rolback
    sorm.executeTransaction(conn -> conn.insert(a));
    // auto-rolback
    sorm.runTransaction(conn -> conn.insert(a));
    try (OrmTransaction trans = sorm.beginTransaction()) {
      // auto-rolback
      trans.insert(a);
    }
    try (OrmTransaction trans = sorm.beginTransaction(Connection.TRANSACTION_READ_COMMITTED)) {
      // auto-rolback
      trans.insert(a);
    }

    try (Connection conn = sorm.getJdbcConnection()) {
      Sorm.getOrmConnection(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    sorm.run(Player.class, m -> m.runTransaction(conn -> {
      m.insert(a);
      // auto-rolback
    }));


    sorm.run(Player.class, m -> {
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_ALICE;
    sorm.run(Player.class, m -> {
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
