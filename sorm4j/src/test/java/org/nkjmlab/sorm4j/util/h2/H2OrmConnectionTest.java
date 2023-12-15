package org.nkjmlab.sorm4j.util.h2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;

class H2OrmConnectionTest {

  @Test
  void testH2OrmConnectionOf() {
    OrmConnection mockOrmConnection = mock(OrmConnection.class);
    H2OrmConnection h2OrmConnection = H2OrmConnection.of(mockOrmConnection);

    assertNotNull(h2OrmConnection);
    assertTrue(h2OrmConnection instanceof H2OrmConnection);
  }
}
