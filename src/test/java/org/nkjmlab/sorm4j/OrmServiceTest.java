package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class OrmServiceTest {

  private OrmService srv;

  @BeforeEach
  void setUp() {
    srv = OrmTestUtils.createOrmService();
    OrmTestUtils.dropAndCreateTable(srv, Guest.class);
    OrmTestUtils.dropAndCreateTable(srv, Player.class);
  }

  @Test
  void testToString() {
    assertThat(srv.toString()).contains("OrmService");
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
      assertThat(srv.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(0);
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
      assertThat(srv.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(0);
    });
    try (OrmTransaction tr = srv.beginTransaction()) {
      tr.begin();
      tr.insert(a);
      tr.commit();
    }
    srv.runWithJdbcConnection(con -> {
      assertThat(srv.toTypedOrmConnection(Guest.class, con).readAll().size()).isEqualTo(1);
    });
  }

}
