package org.nkjmlab.sorm4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class TypedOrmConnectionTest2 {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sorm, Guest.class);
    SormTestUtils.dropAndCreateTable(sorm, Player.class);
    SormTestUtils.dropAndCreateTable(sorm, Location.class);
  }

  @Test
  void testInsertArray() {
    sorm.run(Player.class, m -> {
      Player a = SormTestUtils.PLAYER_ALICE;
      Player b = SormTestUtils.PLAYER_BOB;
      m.insert(a, b);
      System.out.println(m.readAll());
      // List<Player> g =
      // m.readList("SELECT * FROM PLAYERS WHERE NAME IN (?)", ((Object) new int[] {1, 2}));
      // System.out.println(g);
    });
  }

}
