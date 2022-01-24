package org.nkjmlab.sorm4j;

import static java.sql.Connection.*;
import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.internal.SormImpl;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.table.Table;

class SormImplTest {


  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewContextAndTables();
  }

  @Test
  void testCreate() {
    Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
    Sorm.getDefaultContext().toString();
  }

  @Test
  void readMapOne() {
    sorm.insert(SormTestUtils.GUEST_ALICE);

    Table.create(sorm, Guest.class);
    try (Connection conn = sorm.getJdbcConnection()) {
      assertThat(SormImpl.DEFAULT_CONTEXT.getTableMapping(conn, "guests", Guest.class).toString())
          .contains("Column");
    } catch (SQLException e) {
    }

    assertThat(sorm.getJdbcDatabaseMetaData().toString()).contains("jdbc");


    sorm.acceptHandler(conn -> conn.openStreamAll(Guest.class),
        stream -> stream.collect(Collectors.toList()));

    sorm.applyHandler(conn -> conn.openStreamAll(Guest.class),
        stream -> stream.collect(Collectors.toList()));


  }


  @Test
  void testAutoRollback() throws SQLException {
    Guest a = SormTestUtils.GUEST_ALICE;
    try (OrmConnection tr = sorm.open(TRANSACTION_READ_COMMITTED)) {
      tr.insert(a);
      // auto-rollback
    }
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
      sorm.getJdbcConnection();
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
  }

  @Test
  void testException1() throws SQLException {


    try {
      sorm.applyHandler(con -> {
        throw new RuntimeException("");
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sorm.acceptHandler(con -> {
        throw new RuntimeException("");
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sorm.applyHandler(null, con -> {
        throw new RuntimeException("");
      });
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
    }
    try {
      sorm.acceptHandler(null, con -> {
        throw new RuntimeException("");
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

  private static Guest a = SormTestUtils.GUEST_ALICE;

  @Test
  void testBeginTransaction() {
    try (OrmConnection tr = sorm.open(TRANSACTION_READ_COMMITTED)) {
      tr.insert(a);
      // auto-rollback
    }
  }

}
