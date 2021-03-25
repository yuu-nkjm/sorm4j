package org.nkjmlab.sorm4j.internal.mapping;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.TypedOrmConnection;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class TableMappingTest {
  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testGetValue() {
    try {
      sorm.accept(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.getValue(new Guest(), "hoge");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }

    try {
      sorm.accept(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.getValue(new String(), "id");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not get a value");
    }

  }

  @Test
  void testInsertAndGetEx() throws SQLException {
    Connection conMock = Mockito.spy(Connection.class);
    Mockito.doThrow(new SQLException("Mock exception")).when(conMock)
        .prepareStatement(Mockito.anyString(), Mockito.any(String[].class));
    try {
      sorm.accept(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.insertAndGet(conMock, a);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Mock exception");
    }
  }

  @Test
  void testSetValue() {
    try {
      sorm.accept(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "hoge", 0);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }
    try {
      sorm.accept(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "id", "String");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
    try {
      sorm.accept(Player.class, m -> {
        TableMapping<Player> tm = getTableMapping(m, Player.class);
        tm.setValue(new Player(), "name", 1);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
  }

  @Test
  void testCol() {
    sorm.accept(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getAllColumns())
          .containsAll(List.of("ID", "NAME", "ADDRESS"));
    });
    sorm.accept(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getPrimaryKeys()).containsAll(List.of("ID"));
    });
    sorm.accept(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).toString()).contains("Mapping");
    });
  }

  public static <T> TableMapping<T> getTableMapping(TypedOrmConnection<T> conn,
      Class<T> objectClass) {
    return ((AbstractOrmMapper) conn.untype()).getTableMapping(objectClass);
  }

}
