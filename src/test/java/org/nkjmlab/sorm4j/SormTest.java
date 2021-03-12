package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.core.DataSourceConnectionSource;
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
      sormImpl.acceptJdbcConnectionHandler(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }

    try {
      sormImpl.applyJdbcConnectionHandler(con -> 1);
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.apply(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.apply(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.applyTransactionHandler(con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.applyTransactionHandler(Guest.class, con -> {
        return 1;
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.acceptJdbcConnectionHandler(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.accept(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.accept(Guest.class, con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.acceptTransactionHandler(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sormImpl.acceptTransactionHandler(Guest.class, con -> {
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

    srv.acceptJdbcConnectionHandler(con -> {
    });

  }

  @Test
  void testExecuteWithJdbcConnection() {
    srv.applyJdbcConnectionHandler(con -> "test");
  }

  @Test
  void testRunTransactionConsumerOfOrmTransaction() {
    srv.acceptJdbcConnectionHandler(t -> {
    });
  }

  Guest a = SormTestUtils.GUEST_ALICE;

  @Test
  void testRunTransactionClassOfTConsumerOfTypedOrmTransactionOfT() {
    try (TypedOrmTransaction<Guest> tr = srv.openTransaction(Guest.class)) {
      tr.begin();
      tr.insert(a);
      tr.rollback();
      tr.commit();
    }
    srv.acceptJdbcConnectionHandler(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
    });

  }

  @Test
  void testBeginTransaction() {
    try (OrmConnection tr = srv.openTransaction()) {
      tr.begin();
      tr.insert(a);
      // auto-rollback
    }
    srv.acceptJdbcConnectionHandler(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(0);
    });
    try (OrmConnection tr = srv.openTransaction()) {
      tr.begin();
      tr.insert(a);
      tr.commit();
    }
    srv.acceptJdbcConnectionHandler(con -> {
      assertThat(SormFactory.getTypedOrmConnection(con, Guest.class).readAll().size()).isEqualTo(1);
    });
  }

}
