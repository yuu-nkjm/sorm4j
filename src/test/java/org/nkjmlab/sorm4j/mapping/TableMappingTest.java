package org.nkjmlab.sorm4j.mapping;

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
import org.nkjmlab.sorm4j.tool.Location;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class TableMappingTest {
  private Sorm sormImpl;

  @BeforeEach
  void setUp() {
    sormImpl = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sormImpl, Guest.class);
    SormTestUtils.dropAndCreateTable(sormImpl, Player.class);
    SormTestUtils.dropAndCreateTable(sormImpl, Location.class);
  }

  @Test
  void testGetValue() {
    try {
      sormImpl.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.getValue(new Guest(), "hoge");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }

    try {
      sormImpl.run(Guest.class, m -> {
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
      sormImpl.run(Guest.class, m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.insertAndGet(conMock, a);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Fail to insert and get");
    }
  }

  @Test
  void testSetValue() {
    try {
      sormImpl.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "hoge", 0);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }
    try {
      sormImpl.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "id", "String");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
    try {
      sormImpl.run(Player.class, m -> {
        TableMapping<Player> tm = getTableMapping(m, Player.class);
        tm.setValue(new Player(), "name", 1);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
  }

  @Test
  void testCol() {
    sormImpl.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getAllColumns())
          .containsAll(List.of("ID", "NAME", "ADDRESS"));
    });
    sormImpl.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getPrimaryKeys()).containsAll(List.of("ID"));
    });
    sormImpl.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).toString()).contains("Mapping");
    });
  }

  public static <T> TableMapping<T> getTableMapping(TypedOrmConnection<T> conn,
      Class<T> objectClass) {
    return ((AbstractOrmMapper) conn).getTableMapping(objectClass);
  }

}
