package org.nkjmlab.sorm4j.internal;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_BOB;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_BOB;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.TENNIS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.NamedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.OrderedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.command.Command;

class OrmConnectionImplTest {
  private Sorm orm;
  static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  @BeforeEach
  void setUp() throws SQLException {
    orm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  public void testCloseWithSQLException() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    doThrow(SQLException.class).when(mockConnection).close();
    OrmConnection.of(mockConnection).close();
  }

  @Test
  public void testCommitWithSQLException() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    doThrow(SQLException.class).when(mockConnection).commit();
    assertThrows(SQLException.class, () -> OrmConnection.of(mockConnection).commit());
  }

  @Test
  public void testExecuteWithSQLException() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    String sql = "SELECT * FROM table";
    when(mockConnection.prepareStatement(sql)).thenThrow(SQLException.class);
    assertThrows(SQLException.class, () -> OrmConnection.of(mockConnection).execute(sql));
  }

  @Test
  public void testExecuteUpdateWithSQLException() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    String sql = "UPDATE table SET column = ?";
    when(mockConnection.prepareStatement(sql)).thenThrow(SQLException.class);

    assertThrows(
        SQLException.class, () -> OrmConnection.of(mockConnection).executeUpdate(sql, "value"));
  }

  @Test
  void testDelete() {
    orm.acceptHandler(
        conn -> {
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
    orm.insert(GUEST_ALICE, GUEST_BOB);
    orm.insert(PLAYER_ALICE, PLAYER_BOB);
    orm.insert(SormTestUtils.SOCCER);
    orm.insert(SormTestUtils.TENNIS);

    List<Tuple2<Guest, Player>> result =
        orm.joinOn(Guest.class, Player.class, "guests.id=players.id");

    assertThat(result.get(0).getT1().getClass()).isEqualTo(Guest.class);
    assertThat(result.get(0).getT2().getClass()).isEqualTo(Player.class);
    assertThat(result.get(0).toString()).contains("Alice");

    List<Tuple3<Guest, Player, Sport>> result1 =
        orm.joinOn(
            Guest.class, Player.class, Sport.class, "guests.id=players.id", "players.id=sports.id");

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
    orm.acceptHandler(
        m -> {
          m.insert(GUEST_ALICE, GUEST_BOB);
          m.insert(PLAYER_ALICE, PLAYER_BOB);
          m.insert(SormTestUtils.SOCCER);
          m.insert(SormTestUtils.TENNIS);

          List<Tuple2<Guest, Player>> result =
              m.readTupleList(
                  Guest.class,
                  Player.class,
                  "select g.id as gid, g.name as gname, g.address as gaddress, p.id as pid, p.name as pname, p.address as paddress from guests g join players p on g.id=p.id");

          assertThat(result.get(0).getT1().getClass()).isEqualTo(Guest.class);
          assertThat(result.get(0).getT2().getClass()).isEqualTo(Player.class);
          assertThat(result.get(0).toString()).contains("Alice");

          List<Tuple3<Guest, Player, Sport>> result1 =
              m.readTupleList(
                  Guest.class,
                  Player.class,
                  Sport.class,
                  "select g.id as gid, g.name as gname, g.address as gaddress, "
                      + "p.id as pid, p.name as pname, p.address as paddress, "
                      + "s.id sportdotid, s.name sportdotname "
                      + "from guests g "
                      + "join players p on g.id=p.id "
                      + "join sports s on g.id=s.id");

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
    int row =
        orm.applyHandler(
            conn ->
                Command.create(conn, "insert into players values(:id, :name, :address)")
                    .bindBean(new Player(1, "Frank", "Tokyo"))
                    .executeUpdate());
    assertThat(row).isEqualTo(1);
  }

  @Test
  void testNamedRequest() {
    AtomicInteger id = new AtomicInteger(10);

    int row =
        orm.applyHandler(
            conn ->
                Command.create(conn, "insert into players values(:id, :name, :address)")
                    .bindAll(
                        Map.of("id", id.incrementAndGet(), "name", "Frank", "address", "Tokyo"))
                    .executeUpdate());
    assertThat(row).isEqualTo(1);

    row =
        orm.applyHandler(
            conn ->
                Command.create(conn, "insert into players values(:id, :name, :address)")
                    .bind("id", id.incrementAndGet())
                    .bind("name", "Frank")
                    .bind("address", "Tokyo")
                    .executeUpdate());
    assertThat(row).isEqualTo(1);

    row =
        orm.applyHandler(
            conn -> {
              NamedParameterSqlParser sql =
                  NamedParameterSqlParser.of(
                      "insert into players values(`id`, `name`, `address`)",
                      '`',
                      '`',
                      new DefaultColumnToFieldAccessorMapper());
              sql.bind("id", id.incrementAndGet()).bind("name", "Frank").bind("address", "Tokyo");
              return conn.executeUpdate(sql.parse());
            });
    assertThat(row).isEqualTo(1);

    var ret =
        orm.applyHandler(
            conn ->
                Command.create(conn, "select * from players where id=:id")
                    .bind("id", id.get())
                    .executeQuery(conn.getResultSetTraverser(Player.class)));

    assertThat(ret.size()).isEqualTo(1);
  }

  @Test
  void testClose() {
    orm.acceptHandler(
        m -> {
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
    orm.acceptHandler(
        TRANSACTION_READ_COMMITTED,
        m -> {
          m.insert(PLAYER_ALICE);
          Player p = m.readOne(Player.class, "SELECT * FROM PLAYERS");
          assertThat(p.getName()).isEqualTo(PLAYER_ALICE.getName());
          // auto roll-back;
        });
    orm.acceptHandler(
        TRANSACTION_READ_COMMITTED,
        m -> {
          m.insert(PLAYER_ALICE);
          m.commit();
          m.close();
        });
    orm.acceptHandler(
        m -> {
          Player p = m.readOne(Player.class, "SELECT * FROM PLAYERS");
          assertThat(p.getName()).isEqualTo(PLAYER_ALICE.getName());
        });
  }

  @Test
  void testDeleteOnStringT() {
    orm.acceptHandler(
        m -> {
          Player a = PLAYER_ALICE;
          Player b = PLAYER_BOB;
          m.insertInto("players1", a);
          m.deleteIn("players1", a);
          assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
          m.insertInto("players1", a, b);
          m.deleteIn("players1", a, b);
          assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
        });
  }

  @Test
  void testDeleteT() {
    orm.acceptHandler(
        m -> {
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
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGet(new Guest[] {});
          assertThat(g.countRowsModified()).isEqualTo(0);
        });

    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGet(a);
          assertThat(g.getGeneratedKeys().get("id")).isEqualTo(1);
        });
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGetIn("players1", a);
          assertThat(g.getGeneratedKeys().get("id")).isNull();
        });
  }

  @Test
  void testInsertAndGetOnList() {
    Guest a = GUEST_ALICE;
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGetIn("players1", List.of());
          assertThat(g.toString()).contains("InsertResult");
          assertThat(g.countRowsModified()).isEqualTo(0);
        });
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGet(List.of(a));
          assertThat(g.getGeneratedKeys().get("id")).isEqualTo(1);
        });
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGetIn("guests", List.of(GUEST_BOB));
          assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
        });
  }

  @Test
  void testInsertAndGetOnStringT0() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGet(a, b);
          assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
        });
  }

  @Test
  void testInsertAndGetOnStringT1() {
    Guest a = GUEST_ALICE;
    Guest b = GUEST_BOB;
    orm.acceptHandler(
        m -> {
          InsertResult g = m.insertAndGetIn("guests", a, b);
          assertThat(g.getGeneratedKeys().get("id")).isEqualTo(2);
        });
  }

  @Test
  void testInsertAndRead() {
    orm.acceptHandler(
        m -> {
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
    orm.acceptHandler(
        m -> {
          Player a = PLAYER_ALICE;
          Player b = PLAYER_BOB;
          m.insertInto("players1", a, b);
          assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
          m.deleteAllIn("players1");
          assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
        });
    orm.acceptHandler(
        m -> {
          Player a = PLAYER_ALICE;
          Player b = PLAYER_BOB;
          m.insertInto("players1", List.of(a, b));
          assertThat(m.readList(Player.class, "select * from players1")).contains(a, b);
          m.deleteIn("players1", List.of(a, b));
          assertThat(m.readList(Player.class, "select * from players1").size()).isEqualTo(0);
        });
  }

  @Test
  void testExec() {
    orm.acceptHandler(
        m -> {
          m.insert(PLAYER_ALICE);
          assertThat(m.executeUpdate("delete from players")).isEqualTo(1);
          m.insert(PLAYER_ALICE, PLAYER_BOB);
          assertThat(m.executeUpdate(ParameterizedSql.of("delete from players", new Object[0])))
              .isEqualTo(2);
          ;
        });
  }

  @Test
  void testMergeError() {
    try {
      orm.acceptHandler(
          m -> {
            Guest a = GUEST_ALICE;
            m.merge(a);
          });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Parameter \"#3\" is not set;");
    }
  }

  @Test
  void testmergeInT() {
    orm.mergeIn("players1", new Player[] {});
    orm.merge(new Player[] {});

    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;
    orm.mergeIn("players1", a);
    orm.mergeIn("players1", a, b);
    assertThat(orm.readList(Player.class, "select * from players1").size()).isEqualTo(2);

    orm.mergeIn("players1", List.of(a, b));
    assertThat(orm.readList(Player.class, "select * from players1").size()).isEqualTo(2);
  }

  @Test
  void testMergesError() {
    try {
      orm.acceptHandler(
          m -> {
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
    orm.merge(a);
    orm.merge(a, b);
    orm.merge(List.of(a, b));
    Player c = new Player(a.getId(), "UPDATED", "UPDATED");
    orm.merge(c, b);
    assertThat(orm.selectAll(Player.class).size()).isEqualTo(2);
    assertThat(orm.selectByPrimaryKey(Player.class, a.getId()).readAddress()).isEqualTo("UPDATED");
  }

  @Test
  void testReadAllLazy() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_BOB;

    orm.acceptHandler(
        m -> {
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
    orm.acceptHandler(
        m -> {
          Guest a = GUEST_ALICE;
          m.insert(a);
          Guest g = m.selectByPrimaryKey(Guest.class, 1);
          assertThat(g.getAddress()).isEqualTo(a.getAddress());
          assertThat(g.getName()).isEqualTo(a.getName());
        });
  }

  @Test
  void testReadList() {
    orm.acceptHandler(
        m -> {
          Player a = PLAYER_ALICE;
          Player b = PLAYER_BOB;
          m.insert(a, b);
          assertThat(m.readList(Player.class, "select * from players")).contains(a, b);
          assertThat(m.readList(Player.class, ParameterizedSql.of("select * from players")))
              .contains(a, b);
          assertThat(
                  m.readOne(
                      Player.class,
                      OrderedParameterSqlParser.parse("select * from players where id=?", 1)))
              .isEqualTo(a);
          assertThat(m.readOne(Player.class, "select * from players where id=?", 1)).isEqualTo(a);
        });
  }

  @Test
  void testReadOne() {
    try {
      orm.acceptHandler(
          m -> {
            Guest a = GUEST_ALICE;
            Guest b = GUEST_BOB;
            m.insert(a);
            m.insert(b);
            Guest g =
                m.readOne(
                    Guest.class,
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
    orm.acceptHandler(
        TRANSACTION_READ_COMMITTED,
        m -> {
          m.insert(a);
          m.rollback();
        });

    orm.acceptHandler(
        TRANSACTION_READ_COMMITTED,
        m -> {
          m.insert(a);
          Guest g = m.readFirst(Guest.class, "SELECT * FROM GUESTS");
          assertThat(g.getAddress()).isEqualTo(a.getAddress());
          g = m.readFirst(Guest.class, ParameterizedSql.of("SELECT * FROM GUESTS"));
          assertThat(g.getAddress()).isEqualTo(a.getAddress());
        });
  }

  @Test
  void testTransactionLevel() {
    Sorm.create(orm.getDataSource(), SormContext.builder().build())
        .acceptHandler(
            Connection.TRANSACTION_SERIALIZABLE,
            m -> {
              assertThat(m.getJdbcConnection().getTransactionIsolation())
                  .isEqualTo(Connection.TRANSACTION_SERIALIZABLE);
            });
  }

  @Test
  void testUpdateOnT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    orm.acceptHandler(
        m -> {
          m.insert(a);
          m.updateWith("players", new Player(a.getId(), "UPDATED", "UPDATED"));
          m.updateWith(
              "players",
              new Player(a.getId(), "UPDATED", "UPDATED"),
              new Player(b.getId(), "UPDATED", "UPDATED"));
          m.updateWith(
              "players",
              List.of(
                  new Player(a.getId(), "UPDATED", "UPDATED"),
                  new Player(b.getId(), "UPDATED", "UPDATED")));
          Player p = m.selectByPrimaryKey(Player.class, a.getId());
          assertThat(p.readAddress()).isEqualTo("UPDATED");
          p = m.selectByPrimaryKey(Player.class, b.getId());
          assertThat(p.readAddress()).isEqualTo("UPDATED");
          m.updateByPrimaryKey(Player.class, RowMap.of("address", "upup"), a.getId());
          assertThat(m.selectByPrimaryKey(Player.class, a.getId()).readAddress()).isEqualTo("upup");
          assertThat(m.exists(p)).isTrue();
        });
  }

  @Test
  void testUpdateT() {
    Player a = PLAYER_ALICE;
    Player b = PLAYER_ALICE;
    orm.acceptHandler(
        m -> {
          m.insert(a);
          m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
          m.update(
              new Player(a.getId(), "UPDATED", "UPDATED"),
              new Player(b.getId(), "UPDATED", "UPDATED"));
          m.update(
              List.of(
                  new Player(a.getId(), "UPDATED", "UPDATED"),
                  new Player(b.getId(), "UPDATED", "UPDATED")));
          Player p = m.selectByPrimaryKey(Player.class, a.getId());
          assertThat(p.readAddress()).isEqualTo("UPDATED");
          p = m.selectByPrimaryKey(Player.class, b.getId());
          assertThat(p.readAddress()).isEqualTo("UPDATED");
        });
  }
}
