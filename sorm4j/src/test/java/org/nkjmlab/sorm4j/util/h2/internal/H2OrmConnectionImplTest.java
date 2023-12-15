package org.nkjmlab.sorm4j.util.h2.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.internal.SormContextImpl;

class H2OrmConnectionImplTest {

  @Test
  void testH2OrmConnectionImplWithConnectionAndSormContext() {
    Connection mockConnection = mock(Connection.class);
    SormContextImpl mockSormContext = mock(SormContextImpl.class);

    H2OrmConnectionImpl h2OrmConnection = new H2OrmConnectionImpl(mockConnection, mockSormContext);

    assertNotNull(h2OrmConnection);
    assertEquals(mockConnection, h2OrmConnection.getJdbcConnection());
  }

  @Test
  void testH2OrmConnectionImplWithOrmConnection() {
    OrmConnection mockOrmConnection = mock(OrmConnection.class);
    Connection mockConnection = mock(Connection.class);
    SormContextImpl mockSormContext = mock(SormContextImpl.class);

    when(mockOrmConnection.getJdbcConnection()).thenReturn(mockConnection);
    when(mockOrmConnection.getContext()).thenReturn(mockSormContext);

    H2OrmConnectionImpl h2OrmConnection = new H2OrmConnectionImpl(mockOrmConnection);

    assertNotNull(h2OrmConnection);
    assertEquals(mockConnection, h2OrmConnection.getJdbcConnection());
  }

  @Test
  void testGetOrm() {
    OrmConnection mockOrmConnection = mock(OrmConnection.class);
    Connection mockConnection = mock(Connection.class);
    SormContextImpl mockSormContext = mock(SormContextImpl.class);

    when(mockOrmConnection.getJdbcConnection()).thenReturn(mockConnection);
    when(mockOrmConnection.getContext()).thenReturn(mockSormContext);

    H2OrmConnectionImpl h2OrmConnection = new H2OrmConnectionImpl(mockOrmConnection);

    OrmConnection returnedOrmConnection = h2OrmConnection.getOrm();

    assertNotNull(returnedOrmConnection);
    assertSame(h2OrmConnection, returnedOrmConnection);
  }
}
