package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.TypedOrmMapper;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class TableMappingTest {
  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sorm, Guest.class);
    SormTestUtils.dropAndCreateTable(sorm, Player.class);
    SormTestUtils.dropAndCreateTable(sorm, Location.class);
  }

  @Test
  void testGetValue() {
    try {
      sorm.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.getValue(new Guest(), "hoge");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }

    try {
      sorm.run(Guest.class, m -> {
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
      sorm.run(Guest.class, m -> {
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
      sorm.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "hoge", 0);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }
    try {
      sorm.run(Guest.class, m -> {
        TableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.setValue(new Guest(), "id", "String");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
    try {
      sorm.run(Player.class, m -> {
        TableMapping<Player> tm = getTableMapping(m, Player.class);
        tm.setValue(new Player(), "name", 1);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
  }

  @Test
  void testCol() {
    sorm.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getAllColumns())
          .containsAll(List.of("ID", "NAME", "ADDRESS"));
    });
    sorm.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).getPrimaryKeys()).containsAll(List.of("ID"));
    });
    sorm.run(Guest.class, m -> {
      assertThat(getTableMapping(m, Guest.class).toString()).contains("Mapping");
    });
  }

  public static <T> TableMapping<T> getTableMapping(TypedOrmMapper<T> conn, Class<T> objectClass) {
    return ((AbstractOrmMapper) conn).getTableMapping(objectClass);
  }

}
