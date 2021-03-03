package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.OrmTestUtils.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.util.OrmTestUtils;
import org.nkjmlab.sorm4j.util.Player;

class SimpleBatchProcessorTest {

  private static Sorm sorm;
  private static final Player a = OrmTestUtils.PLAYER_ALICE;
  private static final Player b = OrmTestUtils.PLAYER_BOB;
  private static final Player c = OrmTestUtils.PLAYER_CAROL;

  @BeforeAll
  static void setUp() {
    Sorm.configure("SIMPLE_BATCH", builder -> builder
        .setMultiRowProcessorFactory(t -> new SimpleBatchProcessor<>(t, 10)).build());
    sorm = Sorm.create(ConnectionSource.of(jdbcUrl, user, password), "SIMPLE_BATCH");
  }


  @BeforeEach
  void setUpEach() {
    OrmTestUtils.dropAndCreateTable(sorm, Player.class);
  }

  @Test
  void testSetUp() {
    String s = sorm.execute(Player.class, conn -> ((TypedOrmConnectionImpl<Player>) conn)
        .getTableMapping(Player.class).getFormattedString());
    assertThat(s).contains(SimpleBatchProcessor.class.getSimpleName());
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
