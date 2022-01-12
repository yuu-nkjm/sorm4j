package org.nkjmlab.sorm4j.internal.mapping;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.ColumnToAccessorMapping;

class TableMappingTest {
  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testGetValue() {
    try {
      sorm.accept(m -> {
        ColumnToAccessorMapping tm = getTableMapping(m, Guest.class).getColumnToAccessorMap();
        Guest g = new Guest();
        tm.getValue(g, "hoge");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }

    try {
      sorm.accept(m -> {
        ColumnToAccessorMapping tm = getTableMapping(m, Guest.class).getColumnToAccessorMap();
        String s = new String();
        tm.getValue(s, "id");
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
      sorm.accept(m -> {
        Guest a = SormTestUtils.GUEST_ALICE;
        SqlParametersToTableMapping<Guest> tm = getTableMapping(m, Guest.class);
        tm.insertAndGet(conMock, a);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Mock exception");
    }
  }

  @Test
  void testSetValue() {
    try {
      sorm.accept(m -> {
        ColumnToAccessorMapping tm = getTableMapping(m, Guest.class).getColumnToAccessorMap();
        tm.setValue(new Guest(), "hoge", 0);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not have a corresponding");
    }
    try {
      sorm.accept(m -> {
        ColumnToAccessorMapping tm = getTableMapping(m, Guest.class).getColumnToAccessorMap();
        tm.setValue(new Guest(), "id", "String");
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
    try {
      sorm.accept(m -> {
        ColumnToAccessorMapping tm = getTableMapping(m, Guest.class).getColumnToAccessorMap();
        tm.setValue(new Player(), "name", 1);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Could not set a value");
    }
  }

  @Test
  void testCol() {
    sorm.accept(m -> {
      assertThat(getTableMapping(m, Guest.class).getTableMetaData().getColumns())
          .containsAll(List.of("ID", "NAME", "ADDRESS"));
    });
    sorm.accept(m -> {
      assertThat(getTableMapping(m, Guest.class).getTableMetaData().getPrimaryKeys())
          .containsAll(List.of("ID"));
    });
    sorm.accept(m -> {
      assertThat(getTableMapping(m, Guest.class).toString()).contains("Mapping");
    });
  }

  public static <T> SqlParametersToTableMapping<T> getTableMapping(OrmConnection conn, Class<T> objectClass) {
    return ((OrmConnectionImpl) conn).getTableMapping(objectClass);
  }

}
