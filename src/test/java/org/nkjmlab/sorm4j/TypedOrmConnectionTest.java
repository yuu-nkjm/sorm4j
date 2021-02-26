package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import java.sql.SQLException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class TypedOrmConnectionTest {

  private OrmService srv;

  @BeforeEach
  void setUp() {
    srv = OrmTestUtils.createOrmService();
    OrmTestUtils.createTable(srv, Guest.class);
    srv.run(Guest.class, m -> m.deleteAll());

    OrmTestUtils.createTable(srv, Player.class);
    srv.run(Player.class, m -> m.deleteAll());

  }

  @Test
  void testInsertAndRead() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }

  @Test
  void testClose() {
    srv.run(Guest.class, m -> {
      m.close();
      try {
        assertThat(m.getJdbcConnection().isClosed()).isTrue();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  @Test
  void testCommint() {
    Guest a = OrmTestUtils.GUEST_ALICE;
    srv.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      // auto roll-back;
    });
    srv.run(Guest.class, m -> {
      m.begin();
      m.insert(a);
      m.commit();
      m.close();
    });
    srv.run(Guest.class, m -> {
      Guest g = m.readFirst("SELECT * FROM GUESTS");
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
    });
  }


  @Test
  void testToUntyped() {
    srv.getTypedConnection(Guest.class).toUntyped();
  }

  @Test
  void testGetAllColumns() {
    srv.run(Guest.class, m -> {
      assertThat(m.getAllColumns()).contains("ID", "NAME", "ADDRESS");
    });
  }

  @Test
  void testGetPrimaryKeys() {
    srv.run(Guest.class, m -> {
      assertThat(m.getPrimaryKeys()).contains("ID");
    });
  }

  @Test
  void testReadByPrimaryKey() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      m.insert(a);
      Guest g = m.readByPrimaryKey(1);
      assertThat(g.getAddress()).isEqualTo(a.getAddress());
      assertThat(g.getName()).isEqualTo(a.getName());
    });
  }



  @Test
  void testReadAllLazy() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readAllLazy().stream().collect(Collectors.toList())).contains(a, b);
    });
  }

  @Test
  void testReadList() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      assertThat(m.readList("select * from players")).contains(a, b);
    });
  }


  @Test
  void testInsertOnStringT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      assertThat(m.readList("select * from players1")).contains(a, b);
    });
  }


}
