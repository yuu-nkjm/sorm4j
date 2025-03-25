package org.nkjmlab.sorm4j.internal;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_ALICE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.container.Tuple.Tuple3;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;
import org.nkjmlab.sorm4j.table.orm.Table;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;

class SormImplTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testCreate() {
    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", Sorm.getDefaultContext());
    Sorm.getDefaultContext().toString();
  }

  @Test
  void testJoin() {
    List<Tuple2<Guest, Player>> ret =
        sorm.leftJoinOn(Guest.class, Player.class, "guests.id=players.id");
    assertThat(ret.size()).isEqualTo(sorm.selectAll(Guest.class).size());

    List<Tuple3<Guest, Player, Sport>> ret1 =
        sorm.leftJoinOn(
            Guest.class, Player.class, Sport.class, "guests.id=players.id", "players.id=sports.id");
    assertThat(ret1.size()).isEqualTo(sorm.selectAll(Guest.class).size());

    sorm.insert(PLAYER_ALICE);
    assertThat(sorm.exists(PLAYER_ALICE)).isTrue();
    assertThat(sorm.existsByPrimaryKeyIn("players", PLAYER_ALICE.id)).isTrue();
    assertThat(
            sorm.readTupleList(
                    Guest.class,
                    Player.class,
                    ParameterizedSql.of(
                        "select * from guests join players on guests.id=players.id"))
                .size())
        .isEqualTo(0);

    assertThat(sorm.getTableSql("players")).isNotNull();

    assertThat(sorm.execute("select * from players")).isTrue();

    sorm.readTupleList(
        Guest.class,
        Player.class,
        Sport.class,
        ParameterizedSql.of(
            "select * from guests join players on guests.id=players.id join sports on players.id=sports.id"));
  }

  @Test
  void testJoin3() {
    assertThat(
            sorm.readTupleList(
                    Guest.class,
                    Player.class,
                    Sport.class,
                    ParameterizedSql.of(
                        "select guests.id, guests.name, guests.address, players.id, players.name, players.address, sports.id, sports.name from guests join players on guests.id=players.id join sports on guests.id=sports.id"))
                .size())
        .isEqualTo(0);

    List<Tuple3<Guest, Player, Sport>> ret1 =
        sorm.joinOn(
            Guest.class, Player.class, Sport.class, "guests.id=players.id", "players.id=sports.id");
    assertThat(ret1.size()).isEqualTo(sorm.selectAll(Guest.class).size());
  }

  @Test
  void testExcute() throws SQLException {
    assertThat(sorm.getTableSql(Player.class)).isNotNull();
    assertThat(sorm.execute("select * from players")).isTrue();
    sorm.insert(SormTestUtils.PLAYER_ALICE);
    sorm.stream(Player.class, "select * from players")
        .accept(st -> assertThat(st.count()).isEqualTo(1));
  }

  @Test
  void readMapOne() {
    sorm.insert(SormTestUtils.GUEST_ALICE);
    Table.of(sorm, Guest.class, "guests");
    Table.of(sorm, Guest.class);
    try (Connection conn = sorm.openJdbcConnection()) {
      assertThat(SormImpl.DEFAULT_CONTEXT.getTableMapping(conn, "guests", Guest.class).toString())
          .contains("CsvColumn");
    } catch (SQLException e) {
    }

    assertThat(sorm.getJdbcDatabaseMetaData().toString()).contains("jdbc");
  }

  @Test
  void testAutoRollback() throws SQLException {
    Guest a = SormTestUtils.GUEST_ALICE;
    try (OrmConnection tr = sorm.open(TRANSACTION_READ_COMMITTED)) {
      tr.insert(a);
      // auto-rollback
    }
    assertThat(sorm.selectAll(Guest.class).size() == 0);

    sorm.applyHandler(Connection.TRANSACTION_READ_COMMITTED, conn -> conn.insert(a));
    assertThat(sorm.selectAll(Guest.class).size() == 0);
  }

  @Test
  void testException() throws SQLException {
    DataSource dsMock = Mockito.spy(DataSource.class);
    Mockito.doThrow(new SQLException("Mock getConnection exception")).when(dsMock).getConnection();
    // Connection conMock = Mockito.spy(Connection.class);
    // Mockito.doThrow(new SQLException("Mock close exception")).when(conMock).close();
    // Mockito.when(dsMock.getConnection()).thenReturn(conMock);

    Sorm sorm = Sorm.create(dsMock);
    try {
      sorm.openJdbcConnection();
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
  }

  @Test
  void testException1() throws SQLException {

    try {
      sorm.applyHandler(
          con -> {
            throw new RuntimeException("");
          });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sorm.acceptHandler(
          con -> {
            throw new RuntimeException("");
          });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
  }

  @Test
  void testToString() {
    assertThat(sorm.toString()).contains("Sorm");

    Sorm.create(SormTestUtils.createNewDatabaseDataSource()).getDataSource();
  }

  private static Guest a = SormTestUtils.GUEST_ALICE;

  @Test
  void testBeginTransaction() {
    try (OrmConnection tr = sorm.open(TRANSACTION_READ_COMMITTED)) {
      tr.insert(a);
      // auto-rollback
    }
  }
}
