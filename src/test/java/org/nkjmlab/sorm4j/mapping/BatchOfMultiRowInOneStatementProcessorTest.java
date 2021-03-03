package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.SormTestUtils.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.connectionsource.ConnectionSource;
import org.nkjmlab.sorm4j.util.DebugPointFactory;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.Player;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;

  @BeforeAll
  static void beforAll() {
    DebugPointFactory.on();
    DebugPointFactory.off();
    DebugPointFactory.setModes(Map.of(DebugPointFactory.Name.MAPPING, true));
  }


  @BeforeAll
  static void setUp() {
    Sorm.configure("BATCH_CONF", builder -> builder.setMultiRowProcessorFactory(
        t -> new BatchOfMultiRowInOneStatementProcessor<>(t, 10, 10, 4)).build());
    sorm = Sorm.create(ConnectionSource.of(jdbcUrl, user, password), "BATCH_CONF");
  }

  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }

  @Test
  void testSetUp() {
    String s = sorm.execute(Player.class, conn -> ((TypedOrmConnectionImpl<Player>) conn)
        .getTableMapping(Player.class).getFormattedString());
    assertThat(s).contains(BatchOfMultiRowInOneStatementProcessor.class.getSimpleName());
  }


  @Test
  void testMultiRowInsert() {
    sorm.run(Player.class, conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowInsertMany() {
    sorm.run(Guest.class, conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.run(Player.class, conn -> conn.merge(a, b, c));
  }


}
