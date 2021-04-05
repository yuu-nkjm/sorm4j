package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class OrderedParametersQueryTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testAddObjectArray() {
    sorm.accept(Player.class, conn -> {
      conn.insert(PLAYER_ALICE);
      Player ret = conn.createOrderedParameterQuery("select * from PLAYERS where ID=?").addParameter(1)
          .readList().get(0);
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
    sorm.accept(conn -> {
      Player ret =
          conn.createOrderedParameterQuery(Player.class, "select * from PLAYERS where ID=?").addParameter(1)
              .readList().get(0);
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
  }

}
