package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.mapping.InsertResultImpl;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Location;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

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
  void testOrderedRequest() {
    AtomicInteger id = new AtomicInteger(10);

    int row = sorm.apply(Player.class,
        conn -> conn.createNamedParameterRequest("insert into players values(:id, :name, :address)")
            .bindAll(Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
            .executeUpdate());
    assertThat(row).isEqualTo(1);


    row = sorm.apply(Player.class,
        conn -> conn.createOrderedParameterRequest("insert into players values(?,?,?)")
            .addParameter(id.incrementAndGet()).addParameter("Frank").addParameter("Tokyo")
            .executeUpdate());
    assertThat(row).isEqualTo(1);


    List<Player> ret1 =
        sorm.apply(Player.class, conn -> conn.executeQuery(SqlStatement.of("select * from players"),
            (rs, rn) -> conn.mapRow(Player.class, rs)));

    assertThat(ret1.size()).isEqualTo(2);
    ret1 =
        sorm.apply(Player.class, conn -> conn.executeQuery(SqlStatement.of("select * from players"),
            rs -> conn.mapRows(Player.class, rs)));
    assertThat(ret1.size()).isEqualTo(2);

    List<Map<String, Object>> ret2 = sorm.apply(Player.class, conn -> conn
        .executeQuery(SqlStatement.of("select * from players"), rs -> conn.mapRows(rs)));

    assertThat(ret2.size()).isEqualTo(2);

    ret2 = sorm.apply(Player.class, conn -> conn
        .executeQuery(SqlStatement.of("select * from players"), (rs, rowNum) -> conn.mapRow(rs)));

    assertThat(ret2.size()).isEqualTo(2);


  }


  @Test
  void testException() {
    sorm.accept(m -> {
      try {
        m.executeUpdate("selecttt");
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("Syntax error in SQL statement");
      }
    });
  }

  @Test
  void testMerge() {
    sorm.accept(Guest.class, m -> {
      // nodate
      int[] g = m.merge(new Guest[] {});
      assertThat(g.length).isEqualTo(0);
    });

    // merge one objects
    sorm.accept(Player.class, m -> {
      int g = m.merge(PLAYER_ALICE);
      assertThat(g).isEqualTo(1);
    });

    // merge two objects
    sorm.accept(Player.class, m -> {
      int[] g = m.merge(PLAYER_ALICE, PLAYER_BOB);
      assertThat(g[0]).isEqualTo(2);
    });

    // merge with list and check result
    sorm.accept(Player.class, m -> {
      Player c = new Player(PLAYER_ALICE.getId(), "UPDATED", "UPDATED");
      m.merge(List.of(c));
      assertThat(m.readAll().size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(PLAYER_ALICE.getId()).readAddress()).isEqualTo("UPDATED");
    });
    try {
      // merge will be fail because of having auto generated keys.
      sorm.accept(Guest.class, m -> {
        m.merge(GUEST_ALICE, GUEST_BOB);
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("autogenerated");
    }

  }


  @Test
  void testMergeOn() {
    // only exec test.
    sorm.accept(Player.class, m -> {
      m.mergeOn("players1", PLAYER_ALICE);
      m.mergeOn("players1", PLAYER_ALICE, PLAYER_BOB);
      m.mergeOn("players1", List.of(PLAYER_ALICE, PLAYER_BOB));
      assertThat(m.readList("select * from players1").size()).isEqualTo(2);
    });
  }



  @Test
  void testTableName() {
    // table name
    sorm.accept(Guest.class, m -> {
      assertThat(m.getTableName()).contains("GUESTS");
    });

    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });


    sorm.accept(Guest.class, conn -> {



    });

  }

  @Test
  void testClose() {
    SormFactory.create(SormTestUtils.createDataSourceH2()).getDataSource();

    sorm.accept(Guest.class, m -> {
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
    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.accept(Guest.class, m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    sorm.accept(Guest.class, m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    sorm.accept(Guest.class, m -> {
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    sorm.accept(Player.class, m -> {
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
    sorm.accept(Player.class, m -> {
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
    assertThat(InsertResultImpl.emptyInsertResult().getRowsModified()[0]).isEqualTo(0);


    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
      m.insertAndGet(new Guest[0]);
    });

    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {
    assertThat(InsertResultImpl.emptyInsertResult().getRowsModified()[0]).isEqualTo(0);


    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", List.of(a));
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.accept(Guest.class, m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    sorm.accept(Guest.class, m -> {
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
    sorm.accept(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
    sorm.accept(Player.class, m -> {
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
    sorm.accept(Player.class, m -> {
      m.insert(a);
      m.executeUpdate("DROP TABLE IF EXISTS PLAYERS1");
      m.executeUpdate(SqlStatement.of("DROP TABLE IF EXISTS PLAYERS1"));

    });
  }



  @Test
  void testReadLazy() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.accept(Player.class, m -> {
      m.insert(List.of(a, b));


      Player p1 = m.readLazy(SqlStatement.of("select * from players"))
          .toList((rs, rowNum) -> m.mapRow(Player.class, rs)).get(0);
      assertThat(p1.getName()).isEqualTo(a.getName());


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

      map = m.readMapFirst("select * from players");
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      map = m.readMapFirst(SqlStatement.of("select * from players WHERE id=?", 100));
      assertThat(map).isNull();


      Player p = m.readLazy("select * from players").toList().get(0);
      assertThat(p).isEqualTo(a);

      try {
        m.readLazy("select * from players").iterator().remove();
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
    });
    sorm.accept(Player.class, m -> {
      try {
        m.readLazy("select * from playersass");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
    });
  }

  @Test
  void testReadAllLazy() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.accept(Player.class, m -> {
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
    sorm.accept(Player.class, m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(Player.class, m -> {
      Map<String, Object> map =
          m.readMapLazy("select * from players").stream().collect(Collectors.toList()).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(Player.class, m -> {
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


    sorm.accept(Player.class, m -> {
      Map<String, Object> map = m.readMapList(SqlStatement.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(Player.class, m -> {
      Map<String, Object> map = m.readMapList("select * from players").get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(Player.class, m -> {
      Map<String, Object> map =
          m.readMapOne(SqlStatement.of("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(Player.class, m -> {
      Map<String, Object> map = m.readMapOne("select * from players where id=?", 1);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(Player.class, m -> {
      Map<String, Object> map =
          m.readMapLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
  }

  @Test
  void testReadByPrimaryKey() {
    sorm.accept(Guest.class, m -> {
      Guest a = SormTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.accept(Player.class, m -> {
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
    List<Player> result = sorm.apply(Player.class, m -> m.readList("select * from players"));
    assertThat(result).contains(a, b);
    List<Player> result1 = sorm.apply(m -> m.readList(Player.class, "select * from players"));
    assertThat(result1).contains(a, b);
  }

  @Test
  void testReadOne() {
    try {
      sorm.accept(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(SqlStatement.of("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(SqlStatement.of("select * from guests"));
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getCause().getMessage()).contains("Non-unique");
    }

  }

  @Test
  void testSormExeption() {
    DataSource ds = SormFactory
        .create(SormTestUtils.jdbcUrl, SormTestUtils.user, SormTestUtils.password).getDataSource();
    try {
      ds.unwrap(null);
      failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
    } catch (Exception e) {
    }
    try {
      ds.isWrapperFor(null);
      failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
    } catch (Exception e) {
    }
    try {
      ds.getParentLogger();
      failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
    } catch (Exception e) {
    }
    try {
      ds.getConnection("sa", "");
      ds.getLogWriter();
      ds.setLogWriter(null);
      ds.getLoginTimeout();
      ds.setLoginTimeout(10);
    } catch (Exception e) {
      fail();
    }

  }


  @Test
  void testTransaction() {
    Guest a = SormTestUtils.GUEST_ALICE;


    sorm.applyTransactionHandler(m -> m.insert(a));

    sorm.acceptTransactionHandler(Guest.class, m -> {
      m.insert(a);
      m = m.type(Player.class).type(Guest.class);
      m = m.untype().type(Guest.class);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(SqlStatement.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      m.commit();
    });

    Guest g = sorm.applyTransactionHandler(Guest.class, m -> m.readFirst("SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());


  }


  @Test
  void testEnum() {
    sorm.accept(Location.class, m -> {
      m.insert(new Location(Location.Place.KYOTO));
      assertThat(m.readFirst("SELECT * FROM locations").getName()).isEqualTo(Location.Place.KYOTO);
    });

  }

  @Test
  void testUpdateOnT() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_ALICE;

    // auto-rolback
    sorm.applyTransactionHandler(conn -> conn.insert(a));
    // auto-rolback
    sorm.acceptTransactionHandler(conn -> conn.insert(a));
    try (OrmConnection trans = sorm.openTransaction()) {
      // auto-rolback
      trans.insert(a);
    }

    try (Connection conn = sorm.getJdbcConnection()) {
      SormFactory.toOrmConnection(conn);
    } catch (SQLException e) {
      fail();
    }


    sorm.accept(Player.class, m -> {
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
    sorm.accept(Player.class, m -> {
      m = m.untype().type(Player.class);
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
