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
    OrmTestUtils.recreateTable(srv, Guest.class);

    OrmTestUtils.recreateTable(srv, Player.class);

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

  @Test
  void testDeleteT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insert(a, b);
      m.delete(a, b);
      assertThat(m.readAll().size()).isEqualTo(0);
    });
  }

  @Test
  void testDeleteOnStringT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.insertOn("players1", a, b);
      m.deleteOn("players1", a, b);
      assertThat(m.readList("select * from players1").size()).isEqualTo(0);
    });
  }

  @Test
  void testUpdateT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      m.insert(a);
      m.update(new Player(a.getId(), "UPDATED", "UPDATED"));
      Player p = m.readByPrimaryKey(a.getId());
      assertThat(p.getAddress()).isEqualTo("UPDATED");
    });
  }

  @Test
  void testInsertAndGetOnStringT() {
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      InsertResult<Guest> g = m.insertAndGet(a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
    srv.run(Guest.class, m -> {
      Guest a = OrmTestUtils.GUEST_ALICE;
      InsertResult<Guest> g = m.insertAndGetOn("players1", a);
      assertThat(g.getObject().getId()).isEqualTo(1);
    });
  }

  @Test
  void testMergeOnT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.mergeOn("players1", a, b);
      m.mergeOn("players1", a, b);
      assertThat(m.readList("select * from players1").size()).isEqualTo(2);
    });
  }

  @Test
  void testMergeT() {
    srv.run(Player.class, m -> {
      Player a = OrmTestUtils.PLAYER_ALICE;
      Player b = OrmTestUtils.PLAYER_BOB;
      m.merge(a, b);
      Player c = new Player(a.getId(), "UPDATED", "UPDATED");
      m.merge(c, b);
      assertThat(m.readAll().size()).isEqualTo(2);
      assertThat(m.readByPrimaryKey(a.getId()).getAddress()).isEqualTo("UPDATED");
    });
  }
}
