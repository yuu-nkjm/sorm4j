package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.Configurator;

class SimpleBatchProcessorTest {

  private static Sorm sorm;

  @BeforeAll
  static void setUp() {
    SormFactory.registerConfig("SIMPLE_BATCH", builder -> builder
        .setMultiRowProcessorType(Configurator.MultiRowProcessorType.SIMPLE_BATCH));
    sorm = SormFactory.create(jdbcUrl, user, password, "SIMPLE_BATCH");
  }


  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }

  @Test
  void testSetUp() {
    assertThat(sorm.getConfigString())
        .contains(Configurator.MultiRowProcessorType.SIMPLE_BATCH.name());
  }

  @Test
  void testMultiRowInsert() {
    sorm.accept(Player.class, conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
    sorm.acceptTransactionHandler(tr -> {
      try {
        tr.insert(PLAYER_ALICE, null);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("it is null");
      }

    });

  }

  @Test
  void testMultiRowInsertMany() {
    sorm.accept(Guest.class, conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(1000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.accept(Player.class, conn -> conn
        .merge(Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }


}
