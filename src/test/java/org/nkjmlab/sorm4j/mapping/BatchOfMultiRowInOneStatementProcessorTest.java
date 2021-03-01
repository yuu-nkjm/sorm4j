package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.util.OrmTestUtils.*;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.config.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = OrmTestUtils.PLAYER_ALICE;
  private static final Player b = OrmTestUtils.PLAYER_BOB;
  private static final Player c = OrmTestUtils.PLAYER_CAROL;

  @BeforeAll
  static void beforAll() {
    DebugPointFactory.setModes(Map.of(DebugPointFactory.Name.MAPPING, true));
  }


  @BeforeAll
  static void setUp() {
    sorm = Sorm.of(jdbcUrl, user, password,
        new OrmConfigStore.Builder().setMultiRowProcessorFactory(MultiRowProcessorFactory
            .of(t -> new BatchOfMultiRowInOneStatementProcessor(t, 10, 10, 4))).build());
  }

  @BeforeEach
  void setUpEach() {
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
