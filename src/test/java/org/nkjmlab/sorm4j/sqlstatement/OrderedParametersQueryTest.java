package org.nkjmlab.sorm4j.sqlstatement;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class OrderedParametersQueryTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testAddObjectArray() {
    sorm.run(Player.class, conn -> {
      conn.insert(PLAYER_ALICE);
      Player ret = conn.createOrderedParametersQuery("select * from PLAYERS where ID=?").add(1)
          .readList().get(0);
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
  }

}
