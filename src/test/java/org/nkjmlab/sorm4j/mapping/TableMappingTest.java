package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.TypedOrmMapper;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.SormTestUtils;
import org.nkjmlab.sorm4j.util.Player;

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
      assertThat(e.getMessage()).contains("not access getter for");
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
      assertThat(e.getMessage()).contains("Error setting value");
    }
    try {
      sorm.run(Player.class, m -> {
        TableMapping<Player> tm = getTableMapping(m, Player.class);
        tm.setValue(new Player(), "name", 1);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Error setting value");
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
