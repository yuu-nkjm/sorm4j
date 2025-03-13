package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.table.orm.TableConnection;
import org.nkjmlab.sorm4j.test.common.Player;

class TableConnectionImplTest {

  @Test
  void testCloseException() throws SQLException {
    Connection mockConnection = mock(Connection.class);
    OrmConnection ormConnection = OrmConnection.of(mockConnection);

    doThrow(SQLException.class).when(mockConnection).close();

    TableConnection<Player> tableConnection =
        TableConnection.of(ormConnection, Player.class, "players");

    assertThat(tableConnection.getValueType()).isEqualTo(Player.class);
    assertDoesNotThrow(() -> tableConnection.close());
    verify(mockConnection).close();
  }
}
