package org.nkjmlab.sorm4j.internal.sql.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.test.common.Player;

class ResultSetIteratorTest {

  private OrmConnectionImpl mockOrmConnection;
  private ResultSet mockResultSet;
  private ResultSetIterator<Player> resultSetIterator;

  @BeforeEach
  void setUp() throws SQLException {
    mockOrmConnection = mock(OrmConnectionImpl.class);
    mockResultSet = mock(ResultSet.class);
    resultSetIterator = new ResultSetIterator<>(mockOrmConnection, Player.class, mockResultSet);
  }

  @Test
  void testHasNext() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);

    assertTrue(resultSetIterator.hasNext());
    assertFalse(resultSetIterator.hasNext());
  }

  @Test
  void testHasNextWithSQLException() throws SQLException {
    when(mockResultSet.next()).thenThrow(new SQLException("Test exception"));

    assertThrows(SQLException.class, () -> resultSetIterator.hasNext());
  }

  @Test
  void testNext() throws SQLException {
    Player expectedObject = new Player();
    when(mockOrmConnection.mapRowToObject(Player.class, mockResultSet)).thenReturn(expectedObject);

    assertEquals(expectedObject, resultSetIterator.next());
  }

  @Test
  void testNextWithSQLException() throws SQLException {
    when(mockOrmConnection.mapRowToObject(Player.class, mockResultSet))
        .thenThrow(new SQLException("Test exception"));

    assertThrows(SQLException.class, () -> resultSetIterator.next());
  }

  @Test
  void testRemove() {
    assertThrows(UnsupportedOperationException.class, () -> resultSetIterator.remove());
  }
}
