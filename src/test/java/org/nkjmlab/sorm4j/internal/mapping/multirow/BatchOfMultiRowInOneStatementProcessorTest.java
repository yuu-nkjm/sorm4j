package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Configurator;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.SormLogger;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;



  @BeforeAll
  static void setUp() {
    SormLogger.on();
    SormLogger.off();
    SormLogger.on();
    SormFactory.registerConfig("BATCH_CONF", builder -> builder
        .setMultiRowProcessorType(Configurator.MultiRowProcessorType.MULTI_ROW_AND_BATCH));

    sorm = SormTestUtils.createSorm("BATCH_CONF");
    SormTestUtils.dropAndCreateTableAll(sorm);
    String s = sorm.getConfigString();

    assertThat(s.toString())
        .contains(Configurator.MultiRowProcessorType.MULTI_ROW_AND_BATCH.name());
    sorm = SormFactory.create(jdbcUrl, user, password, "BATCH_CONF");
  }

  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }



  @Test
  void testMultiRowInsert() {
    sorm.accept(Player.class, conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowInsertNull() {
    try {
      sorm.accept(Player.class, conn -> conn.insert(a, b, c, a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Unique index or primary key violation");
    }
    try {
      sorm.accept(Player.class, conn -> conn.insert(a, b, null));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Fail to get value from");
    }
  }

  @Test
  void testMultiRowInsertMany() {
    sorm.accept(Guest.class, conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.accept(Player.class, conn -> conn
        .merge(Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }


}
