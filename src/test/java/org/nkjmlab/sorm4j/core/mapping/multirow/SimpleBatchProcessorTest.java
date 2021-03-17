package org.nkjmlab.sorm4j.core.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Configurator;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.core.mapping.TypedOrmConnectionImpl;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class SimpleBatchProcessorTest {

  private static Sorm sorm;
  private static final Player a = PLAYER_ALICE;
  private static final Player b = PLAYER_BOB;
  private static final Player c = PLAYER_CAROL;

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
    String s = sorm.apply(Player.class, conn -> ((TypedOrmConnectionImpl<Player>) conn)
        .getTableMapping(Player.class).getFormattedString());
    assertThat(s).contains(SimpleBatchProcessor.class.getSimpleName());
  }

  @Test
  void testMultiRowInsert() {
    sorm.accept(Player.class, conn -> conn.insert(a, b));
    sorm.acceptTransactionHandler(tr -> {
      try {
        tr.insert(a, null);
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
