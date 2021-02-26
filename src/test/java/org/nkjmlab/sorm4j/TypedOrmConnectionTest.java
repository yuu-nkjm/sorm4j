package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.OrmTestUtils;

class TypedOrmConnectionTest {

  private static OrmService srv;

  @BeforeAll
  static void setUp() {
    srv = OrmTestUtils.createOrmService();
    OrmTestUtils.createTable(srv, Guest.class);
    srv.run(Guest.class, m -> m.deleteAll());

  }

  @Test
  void testCommit() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testToUntyped() {}

}
