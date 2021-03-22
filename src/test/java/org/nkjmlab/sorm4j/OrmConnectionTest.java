package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.core.mapping.InsertResultImpl;
import org.nkjmlab.sorm4j.sql.InsertResult;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class OrmConnectionTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sorm, Guest.class);
    SormTestUtils.dropAndCreateTable(sorm, Player.class);
  }

  @Test
  void testNamedRequest() {
    AtomicInteger id = new AtomicInteger(10);
    int row = sorm.apply(
        conn -> conn.createNamedParameterRequest("insert into players values(:id, :name, :address)")
            .bindAll(Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
            .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.apply(
        conn -> conn.createNamedParameterRequest("insert into players values(:id, :name, :address)")
            .bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo")
            .executeUpdate());
    assertThat(row).isEqualTo(1);

    row = sorm.apply(conn -> {
      NamedParameterSql sql =
          NamedParameterSql.from("insert into players values(`id`, `name`, `address`)", "`", "`");
      sql.bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo");
      return conn.executeUpdate(sql.toSqlStatement());
    });
    assertThat(row).isEqualTo(1);

    List<Player> ret =
        sorm.apply(conn -> conn.createNamedParameterRequest("select * from players where id=:id")
            .bind("id", id.get()).executeQuery((rs, rowNum) -> conn.mapRow(Player.class, rs)));

    assertThat(ret.size()).isEqualTo(1);

    ret = sorm.apply(conn -> conn.createNamedParameterRequest("select * from players where id=:id")
        .bind("id", id.get()).executeQuery(rs -> conn.mapRows(Player.class, rs)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testOrderedRequest() {
    AtomicInteger id = new AtomicInteger(10);
    int row =
        sorm.apply(conn -> conn.createOrderedParameterRequest("insert into players values(?,?,?)")
            .addParameter(id.incrementAndGet(), "Frank", "Tokyo").executeUpdate());
    assertThat(row).isEqualTo(1);

    List<Player> ret = sorm.apply(
        conn -> conn.createOrderedParameterRequest("select * from players where id=? and name=?")
            .addParameter(id.get(), "Frank").executeQuery(rs -> conn.mapRows(Player.class, rs)));

    assertThat(ret.size()).isEqualTo(1);

    row = sorm.apply(conn -> conn.createOrderedParameterRequest("insert into players values(?,?,?)")
        .addParameter(id.incrementAndGet()).addParameter("Frank").addParameter("Tokyo")
        .executeUpdate());
    assertThat(row).isEqualTo(1);


    ret = sorm.apply(conn -> conn.createOrderedParameterRequest("select * from players where id=?")
        .addParameter(id.get()).executeQuery((rs, rowNum) -> conn.mapRow(Player.class, rs)));

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
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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


    Guest a = SormTestUtils.GUEST_ALICE;
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


    Guest a = SormTestUtils.GUEST_ALICE;
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
    sorm.accept(m -> {
      Guest a = SormTestUtils.GUEST_ALICE;
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
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteAllOn("players1");
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
    sorm.accept(m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insertOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
      m.deleteOn("players1", List.of(a, b));
      assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
    });
  }


  @Test
  void testExec() {
    Player a = SormTestUtils.PLAYER_ALICE;
    sorm.accept(m -> {
      m.insert(a);
      m.executeUpdate("DROP TABLE IF EXISTS PLAYERS1");
      m.executeUpdate(SqlStatement.of("DROP TABLE IF EXISTS PLAYERS1"));

    });
  }


  @Test
  void testMergeError() {
    try {
      sorm.accept(m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
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
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
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
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;
    sorm.accept(m -> {
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_BOB;

    sorm.accept(m -> {
      m.insert(a);

      Map<String, Object> map = m.readAllLazy(Player.class).oneMap();
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());


      try {
        m.readMapLazy("select * from hoge").oneMap();
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
        OrmLogger.on();
        m.readList(Integer.class, "select * from players");
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getCause().getMessage())
            .contains("but 1 column was expected to load data into");
      }
      OrmLogger.off();


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
          .isEqualTo(a.readAddress());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapLazy("select * from players").toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapList("select * from players").get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map = m.readMapList(SqlStatement.of("select * from players")).get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapOne(SqlStatement.of("select * from players where id=?", 1));
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });
    sorm.accept(m -> {
      Map<String, Object> map =
          m.readMapLazy(SqlStatement.of("select * from players")).toMapList().get(0);
      assertThat(map.get("NAME") != null ? map.get("NAME") : map.get("name"))
          .isEqualTo(a.getName());
    });

    sorm.accept(m -> {
      try {
        m.readAllLazy(Player.class).oneMap();
        failBecauseExceptionWasNotThrown(SormException.class);
      } catch (SormException e) {
        assertThat(e.getMessage()).contains("Non-unique");
      }
    });


  }

  @Test
  void testReadByPrimaryKey() {
    sorm.accept(m -> {
      Guest a = SormTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(Guest.class, 1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testReadList() {
    sorm.accept(m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
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
      sorm.accept(m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        Guest b = SormTestUtils.GUEST_BOB;
        m.insert(a);
        m.insert(b);
        Guest g = m.readOne(Guest.class, SqlStatement.of("select * from guests where id=?", 1));
        assertThat(g.getAddress()).isEqualTo(a.getAddress());
        assertThat(g.getName()).isEqualTo(a.getName());
        g = m.readOne(Guest.class, SqlStatement.of("select * from guests"));
        failBecauseExceptionWasNotThrown(SormException.class);
      });
    } catch (SormException e) {
      assertThat(e.getCause().getMessage()).contains("Non-unique");
    }

  }


  @Test
  void testTransaction() {
    Guest a = SormTestUtils.GUEST_ALICE;
    sorm.acceptTransactionHandler(m -> {
      TypedOrmTransaction<Guest> tmp = m.type(Guest.class);
      tmp.insert(a);
      tmp.rollback();

    });


    sorm.acceptTransactionHandler(m -> {
      m.insert(a);
      Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      g = m.readFirst(Guest.class, SqlStatement.of("SELECT * FROM GUESTS"));
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }

  @Test
  void testTransactionLevel() {

    SormFactory.registerModifiedConfig(sorm.getConfigName(), "isolev",
        b -> b.setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));

    try {
      sorm.createWith("isole").acceptTransactionHandler(Guest.class, m -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("is not registered");
    }
    sorm.createWith("isolev").acceptTransactionHandler(Guest.class, m -> {
      assertThat(m.getJdbcConnection().getTransactionIsolation())
          .isEqualTo(Connection.TRANSACTION_SERIALIZABLE);
    });
    sorm.acceptTransactionHandler(Guest.class, m -> {
      assertThat(m.getJdbcConnection().getTransactionIsolation())
          .isEqualTo(Connection.TRANSACTION_READ_COMMITTED);
    });
  }


  @Test
  void testUpdateOnT() {
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_ALICE;
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
    Player a = SormTestUtils.PLAYER_ALICE;
    Player b = SormTestUtils.PLAYER_ALICE;
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
