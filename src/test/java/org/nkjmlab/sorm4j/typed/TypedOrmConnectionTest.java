package org.nkjmlab.sorm4j.typed;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
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
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Location;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.internal.mapping.InsertResultImpl;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.LazyResultSet;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class TypedOrmConnectionTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testApplyPreparedStatementHandler() {
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);

      m.acceptPreparedStatementHandler(ParameterizedSql.parse("select * from guests where id=?", 1),
          pstmt -> pstmt.execute());
      m.applyPreparedStatementHandler(ParameterizedSql.parse("select * from guests where id=?", 1),
          pstmt -> pstmt.execute());
    });

  }

  @Test
  void testOrderedRequest() {
    AtomicInteger id = new AtomicInteger(10);

    int row = sorm.apply(
        conn -> conn.createNamedParameterRequest("insert into players values(:id, :name, :address)")
            .bindAll(Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
            .executeUpdate());
    assertThat(row).isEqualTo(1);


    row = sorm.apply(conn -> conn.createOrderedParameterRequest("insert into players values(?,?,?)")
        .addParameter(id.incrementAndGet()).addParameter("Frank").addParameter("Tokyo")
        .executeUpdate());
    assertThat(row).isEqualTo(1);


    List<Player> ret1 = sorm.apply(conn -> conn.type(Player.class).executeQuery(
        ParameterizedSql.from("select * from players"), (rs, rn) -> conn.mapRow(Player.class, rs)));

    assertThat(ret1.size()).isEqualTo(2);
    ret1 = sorm.apply(conn -> conn.type(Player.class).executeQuery(
        ParameterizedSql.from("select * from players"), rs -> conn.mapRowList(Player.class, rs)));
    assertThat(ret1.size()).isEqualTo(2);

    List<Map<String, Object>> ret2 =
        sorm.apply(conn -> conn.executeQuery(ParameterizedSql.from("select * from players"),
            rs -> conn.mapRowsToMapList(rs)));

    assertThat(ret2.size()).isEqualTo(2);

    ret2 = sorm.apply(conn -> conn.executeQuery(ParameterizedSql.from("select * from players"),
        (rs, rowNum) -> conn.mapRowToMap(rs)));

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
    sorm.accept(m -> {
      // nodate
      int[] g = m.merge(new Guest[] {});
      assertThat(g.length).isEqualTo(0);
    });

    // merge one objects
    sorm.accept(m -> {
      int g = m.merge(PLAYER_ALICE);
      assertThat(g).isEqualTo(1);
    });

    // merge two objects
    sorm.accept(m -> {
      int[] g = m.merge(PLAYER_ALICE, PLAYER_BOB);
      assertThat(g[0]).isEqualTo(2);
    });

    // merge with list and check result
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
      Player c = new Player(PLAYER_ALICE.getId(), "UPDATED", "UPDATED");
      m.merge(List.of(c));
      assertThat(m.readAll().size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(PLAYER_ALICE.getId()).readAddress()).isEqualTo("UPDATED");
    });
    try {
      // merge will be fail because of having auto generated keys.
      sorm.accept(m -> {
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
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
      m.mergeOn("players1", PLAYER_ALICE);
      m.mergeOn("players1", PLAYER_ALICE, PLAYER_BOB);
      m.mergeOn("players1", List.of(PLAYER_ALICE, PLAYER_BOB));
      assertThat(m.readList("select * from players1").size()).isEqualTo(2);
    });
  }



  @Test
  void testTableName() {
    // table name
    sorm.accept(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      assertThat(m.getTableName()).contains("GUESTS");
    });

    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", new Guest[] {});
      assertThat(g.getRowsModified()[0]).isEqualTo(0);
    });


    sorm.accept(conn -> {



    });

  }

  @Test
  void testClose() {
    SormFactory.create(SormTestUtils.createDataSourceH2()).getDataSource();

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
    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.accept(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      m.begin();
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    sorm.accept(m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    sorm.accept(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testDeleteOnStringT() {
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
      m.insertAndGet(new Guest[0]);
    });

    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testInsertAndGetOnList() {
    assertThat(InsertResultImpl.emptyInsertResult().getRowsModified()[0]).isEqualTo(0);


    Guest a = SormTestUtils.GUEST_ALICE;
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
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGet(a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = SormTestUtils.GUEST_ALICE;
    Guest b = SormTestUtils.GUEST_BOB;
    sorm.accept(m -> {
      InsertResult<Guest> g = m.insertAndGetOn("players1", a, b);
      assertThat(g.getObject().getId()).isEqualTo(2);
    });
  }



  @Test
  void testInsertAndRead() {
    sorm.accept(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
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
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList("select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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
    sorm.accept(m -> {
      m.insert(a);
      m.executeUpdate("DROP TABLE IF EXISTS PLAYERS1");
      m.executeUpdate(ParameterizedSql.from("DROP TABLE IF EXISTS PLAYERS1"));

    });
  }



  @Test
  void testReadLazy() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);

      m.insert(List.of(a, b));

      Player p1 = m.readLazy(ParameterizedSql.from("select * from players"))
          .toList((rs, rowNum) -> m.mapRow(rs)).get(0);
      assertThat(p1.getName()).isEqualTo(a.getName());


      Map<String, Object> map =
          m.readMapLazy(ParameterizedSql.from("select * from players")).toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapLazy(ParameterizedSql.from("select * from players")).toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      map = m.readMapFirst(ParameterizedSql.from("select * from players"));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());

      map = m.readMapFirst("select * from players");
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      map = m.readMapFirst(OrderedParameterSql.parse("select * from players WHERE id=?", 100));
      assertThat(map).isNull();


      Player p = m.readLazy("select * from players").toList().get(0);
      assertThat(p).isEqualTo(a);

      try {
        m.readLazy("select * from players").iterator().remove();
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
      }
    });
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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
    sorm.accept(m -> {
      m.insert(a);

    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapLazy("select * from players").stream().collect(Collectors.toList()).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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


    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapList(ParameterizedSql.from("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(m -> {
      Map<String, Object> map = m.readMapList("select * from players").get(0);
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
      Map<String, Object> map = m.readMapOne("select * from players where id=?", 1);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapLazy(ParameterizedSql.from("select * from players")).toList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
  }

  @Test
  void testReadByPrimaryKey() {
    sorm.accept(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      Guest a = SormTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList("select * from players")).contains(a, b);
      assertThat(m.readList(ParameterizedSql.from("select * from players"))).contains(a, b);
      assertThat(m.readOne(OrderedParameterSql.parse("select * from players where id=?", 1)))
          .isEqualTo(a);
      assertThat(m.readOne("select * from players where id=?", 1)).isEqualTo(a);
    });
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    List<Player> result = sorm.apply(m -> m.readList(Player.class, "select * from players"));
    assertThat(result).contains(a, b);
    List<Player> result1 = sorm.apply(m -> m.readList(Player.class, "select * from players"));
    assertThat(result1).contains(a, b);
  }

  @Test
  void testReadOne() {
    try {
      sorm.accept(conn -> {
        TypedOrmConnection<Guest> m = conn.type(Guest.class);
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(OrderedParameterSql.parse("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(ParameterizedSql.from("select * from guests"));
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getMessage()).contains("Non-unique");
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

    sorm.acceptTransactionHandler(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      m.insert(a);
      m = m.type(Player.class).type(Guest.class);
      m = m.untype().type(Guest.class);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(ParameterizedSql.from("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      m.commit();
    });

    Guest g = sorm.applyTransactionHandler(m -> m.readFirst(Guest.class, "SELECT * FROM GUESTS"));
    assertThat(g.getAddress()).isEqualTo(a.getAddress());

    sorm.acceptTransactionHandler(conn -> {
      TypedOrmConnection<Guest> m = conn.type(Guest.class);
      m.begin(java.sql.Connection.TRANSACTION_READ_COMMITTED);
    });


  }


  @Test
  void testEnum() {
    sorm.accept(m -> {
      m.insert(SormTestUtils.LOCATION_KYOTO);
      assertThat(m.readFirst(Location.class, "SELECT * FROM locations").getName())
          .isEqualTo(Location.Place.KYOTO);
    });

  }

  @Test
  void testUpdateOnT() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;

    // auto-commit
    sorm.acceptTransactionHandler(conn -> conn.insert(a));
    int num = sorm.apply(conn -> conn.readOne(Integer.class, "select count(*) from players"));
    assertThat(num).isEqualTo(1);

    try (OrmConnection trans = sorm.openTransaction()) {
      // auto-rollback
      trans.insert(b);
    }
    num = sorm.apply(conn -> conn.readOne(Integer.class, "select count(*) from players"));
    assertThat(num).isEqualTo(1);

    try (Connection conn = sorm.getJdbcConnection()) {
      SormFactory.toOrmConnection(conn);
    } catch (SQLException e) {
      fail();
    }

    sorm.accept(conn -> conn.deleteAll(Player.class));


    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
      m.insert(a, b);
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
    sorm.accept(conn -> {
      TypedOrmConnection<Player> m = conn.type(Player.class);
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