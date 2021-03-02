package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.connectionsource.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.mapping.TypedOrmTransaction;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class SormTest {

  private Sorm srv;

  @BeforeEach
  void setUp() {
    srv = OrmTestUtils.createSorm();
    OrmTestUtils.dropAndCreateTable(srv, Guest.class);
    OrmTestUtils.dropAndCreateTable(srv, Player.class);
  }


  @Test
  void testException() throws SQLException {
    ConnectionSource mock = Mockito.spy(ConnectionSource.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(mock).getConnection();
    Sorm sorm = Sorm.of(mock);
    try {
      sorm.getJdbcConnection();
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }

  }

  @Test
  void testException1() throws SQLException {
    Connection conMock = Mockito.spy(Connection.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(conMock).close();

    ConnectionSource csMock =
        Mockito.spy(new DataSourceConnectionSource(OrmTestUtils.createDataSourceHikari()));

    Mockito.when(csMock.getConnection()).thenReturn(conMock);
    Sorm sorm = Sorm.of(csMock);

    try {
      sorm.runWithJdbcConnection(con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }

    try {
      sorm.executeWithJdbcConnection(con -> 1);
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
  }

  @Test
  void testToString() {
    assertThat(srv.toString()).contains("OrmService");

    Sorm.of(OrmTestUtils.jdbcUrl, OrmTestUtils.user, OrmTestUtils.password);
    Sorm.withNewConfig(OrmTestUtils.jdbcUrl, OrmTestUtils.user, OrmTestUtils.password,
        OrmConfigStore.DEFAULT_CONFIGURATIONS);

    Sorm.withNewConfig(OrmTestUtils.createDataSourceH2(), OrmConfigStore.DEFAULT_CONFIGURATIONS)
        .getConnectionSource();

  }


  @Test
  void testRunWithJdbcConnection() {

    srv.runWithJdbcConnection(con -> {
    });

  }

  @Test
  void testExecuteWithJdbcConnection() {
    srv.executeWithJdbcConnection(con -> "test");
  }

  @Test
  void testRunTransactionConsumerOfOrmTransaction() {
    srv.runWithJdbcConnection(t -> {
    });
  }

  Guest a = OrmTestUtils.GUEST_ALICE;

  @Test
  void testRunTransactionClassOfTConsumerOfTypedOrmTransactionOfT() {
    try (TypedOrmTransaction<Guest> tr = srv.beginTransaction(Guest.class)) {
      tr.begin();
      tr.insert(a);
      tr.rollback();
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(Sorm.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(0);
    });

  }

  @Test
  void testBeginTransaction() {
    try (OrmTransaction tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      // auto-rollback
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(Sorm.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(0);
    });
    try (OrmTransaction tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(Sorm.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(1);
    });
  }

}
