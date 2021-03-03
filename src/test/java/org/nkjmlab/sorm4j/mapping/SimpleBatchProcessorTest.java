package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.SormTestUtils.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class SimpleBatchProcessorTest {

  private static Sorm sorm;
  private static final Player a = PLAYER_ALICE;
  private static final Player b = PLAYER_BOB;
  private static final Player c = PLAYER_CAROL;

  @BeforeAll
  static void setUp() {
    Sorm.configure("SIMPLE_BATCH", builder -> builder
        .setMultiRowProcessorFactory(t -> new SimpleBatchProcessor<>(t, 10)).build());
    sorm = Sorm.create(ConnectionSource.of(jdbcUrl, user, password), "SIMPLE_BATCH");
  }


  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
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
    sorm.runTransaction(tr -> {
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
    sorm.run(Guest.class, conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(1000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.run(Player.class, conn -> conn.merge(a, b, c));
  }


}
