package org.nkjmlab.sorm4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Location;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class TypedOrmConnectionTest2 {

  private Sorm sormImpl;

  @BeforeEach
  void setUp() {
    sormImpl = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTable(sormImpl, Guest.class);
    SormTestUtils.dropAndCreateTable(sormImpl, Player.class);
    SormTestUtils.dropAndCreateTable(sormImpl, Location.class);
  }

  @Test
  void testInsertArray() {
    sormImpl.run(Player.class, m -> {
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
