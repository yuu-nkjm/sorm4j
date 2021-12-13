package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class SormTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testCreate() {
    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "sa", "");
    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
  }

  @Test
  void testAutoRollback() throws SQLException {
    Guest a = SormTestUtils.GUEST_ALICE;
    try (OrmConnection tr = sorm.openTransaction()) {
      tr.insert(a);
      // auto-rollback
    }
    assertThat(sorm.readAll(Guest.class).size() == 0);

  }

  @Test
  void testException() throws SQLException {
    DataSource mock = Mockito.spy(DataSource.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(mock).getConnection();
    Sorm sormImpl = Sorm.create(mock);
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

    DataSource mock = Mockito.spy(DataSource.class);

    Mockito.when(mock.getConnection()).thenReturn(conMock);
    Sorm sormImpl = Sorm.create(mock);

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
      sormImpl.apply(con -> {
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
      sormImpl.applyTransactionHandler(con -> {
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
      sormImpl.accept(con -> {
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
      sormImpl.acceptTransactionHandler(con -> {
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }

  }

  @Test
  void testToString() {
    assertThat(sorm.toString()).contains("Sorm");

    Sorm.create(SormTestUtils.createDataSourceH2()).getDataSource();

  }


  @Test
  void testRunWithJdbcConnection() {

    sorm.acceptJdbcConnectionHandler(con -> {
    });

  }

  @Test
  void testExecuteWithJdbcConnection() {
    sorm.applyJdbcConnectionHandler(con -> "test");
  }

  @Test
  void testRunTransactionConsumerOfOrmTransaction() {
    sorm.acceptJdbcConnectionHandler(t -> {
    });
  }

  Guest a = SormTestUtils.GUEST_ALICE;


  @Test
  void testBeginTransaction() {
    try (OrmConnection tr = sorm.openTransaction()) {
      tr.insert(a);
      // auto-rollback
    }
    sorm.acceptJdbcConnectionHandler(con -> {
      assertThat(Sorm.toOrmConnection(con).readAll(Guest.class).size()).isEqualTo(0);
    });
    try (OrmConnection tr = sorm.openTransaction()) {
      tr.insert(a);
      tr.commit();
    }
    sorm.acceptJdbcConnectionHandler(con -> {
      assertThat(Sorm.toOrmConnection(con).readAll(Guest.class).size()).isEqualTo(1);
    });
  }

}
