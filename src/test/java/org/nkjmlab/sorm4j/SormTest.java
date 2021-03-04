package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.connectionsource.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.mapping.OrmTransaction;
import org.nkjmlab.sorm4j.mapping.TypedOrmTransaction;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class SormTest {

  private Sorm srv;

  @BeforeEach
  void setUp() {
    srv = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(srv, Guest.class);
    SormTestUtils.dropAndCreateTable(srv, Player.class);
  }


  @Test
  void testException() throws SQLException {
    ConnectionSource mock = Mockito.spy(ConnectionSource.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(mock).getConnection();
    Sorm sorm = Sorm.create(mock);
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
        Mockito.spy(new DataSourceConnectionSource(SormTestUtils.createDataSourceHikari()));

    Mockito.when(csMock.getConnection()).thenReturn(conMock);
    Sorm sorm = Sorm.create(csMock);

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
    try {
      sorm.execute(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.execute(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.executeTransaction(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.executeTransaction(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.executeTransaction(1, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.executeTransaction(Guest.class, 1, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.runWithJdbcConnection(con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.run(con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.run(Guest.class, con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.runTransaction(con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.runTransaction(Guest.class, con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.runTransaction(1, con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }
    try {
      sorm.runTransaction(Guest.class, 1, con -> {
      });
      failBecauseExceptionWasNotThrown(OrmException.class);
    } catch (OrmException e) {
    }

  }

  @Test
  void testToString() {
    assertThat(srv.toString()).contains("Sorm");

    Sorm.create(SormTestUtils.createDataSourceH2()).getConnectionSource();

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

  Guest a = SormTestUtils.GUEST_ALICE;

  @Test
  void testRunTransactionClassOfTConsumerOfTypedOrmTransactionOfT() {
    try (TypedOrmTransaction<Guest> tr = srv.beginTransaction(Guest.class)) {
      tr.begin();
      tr.insert(a);
      tr.rollback();
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(Sorm.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
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
      assertThat(Sorm.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
    });
    try (OrmTransaction tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(Sorm.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(1);
    });
  }

}
