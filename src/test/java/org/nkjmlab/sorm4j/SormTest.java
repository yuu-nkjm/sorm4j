package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.mapping.DataSourceConnectionSource;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

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
    Sorm sormImpl = SormFactory.create(mock);
    try {
      sormImpl.getJdbcConnection();
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
  }

  @Test
  void testException1() throws SQLException {
    Connection conMock = Mockito.spy(Connection.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(conMock).close();

    ConnectionSource csMock =
        Mockito.spy(new DataSourceConnectionSource(SormTestUtils.createDataSourceHikari()));

    Mockito.when(csMock.getConnection()).thenReturn(conMock);
    Sorm sormImpl = SormFactory.create(csMock);

    try {
      sormImpl.runWithJdbcConnection(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }

    try {
      sormImpl.executeWithJdbcConnection(con -> 1);
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.execute(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.execute(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.executeTransaction(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.executeTransaction(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.executeTransaction(1, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.executeTransaction(Guest.class, 1, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.runWithJdbcConnection(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.run(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.run(Guest.class, con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.runTransaction(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.runTransaction(Guest.class, con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.runTransaction(1, con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.runTransaction(Guest.class, 1, con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }

  }

  @Test
  void testToString() {
    assertThat(srv.toString()).contains("Sorm");

    SormFactory.create(SormTestUtils.createDataSourceH2()).getConnectionSource();

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
    try (TypedOrmConnection<Guest> tr = srv.beginTransaction(Guest.class)) {
      tr.begin();
      tr.insert(a);
      tr.rollback();
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
    });

  }

  @Test
  void testBeginTransaction() {
    try (OrmConnection tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      // auto-rollback
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
    });
    try (OrmConnection tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(1);
    });
  }

}
