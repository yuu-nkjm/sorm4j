package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.OrmTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class BatchOfMultiRowInOneStatementProcessorTest {

  private Sorm sorm;
  private Player a = OrmTestUtils.PLAYER_ALICE;
  private Player b = OrmTestUtils.PLAYER_BOB;
  private Player c = OrmTestUtils.PLAYER_CAROL;

  @BeforeEach
  void setUp() {
    this.sorm = Sorm.of(jdbcUrl, user, password,
        new OrmConfigStore.Builder().setMultiRowProcessorFactory(MultiRowProcessorFactory
            .of(t -> new BatchOfMultiRowInOneStatementProcessor(t, 10, 10, 4))).build());
    OrmTestUtils.dropAndCreateTable(sorm, Player.class);
  }

  @Test
  void testMultiRowInsert() {
    sorm.run(Player.class, conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowMerge() {
    sorm.run(Player.class, conn -> conn.merge(a, b, c));
  }


}
