package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.OrmConfigStoreBuilder;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;
import org.nkjmlab.sorm4j.util.DebugPointFactory;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;



  @BeforeAll
  static void setUp() {
    DebugPointFactory.on();
    DebugPointFactory.off();
    DebugPointFactory.setModes(Map.of(DebugPointFactory.Name.MAPPING, true));
    OrmConfigStore conf = SormFactory.configure("BATCH_CONF", builder -> builder
        .setMultiRowProcessorType(OrmConfigStoreBuilder.MultiRowProcessorType.MULTI_ROW_AND_BATCH)
        .build());

    sorm = SormTestUtils.createSorm(conf.getConfigName());
    SormTestUtils.dropAndCreateTableAll(sorm);
    String s = sorm.applyAndGet(Player.class, conn -> ((TypedOrmConnectionImpl<Player>) conn)
        .getTableMapping(Player.class).getFormattedString());

    assertThat(s.toString()).contains(BatchOfMultiRowInOneStatementProcessor.class.getSimpleName());
    sorm = SormFactory.create(jdbcUrl, user, password, "BATCH_CONF");
  }

  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }



  @Test
  void testMultiRowInsert() {
    sorm.apply(Player.class, conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowInsertNull() {
    try {
      sorm.apply(Player.class, conn -> conn.insert(a, b, c, a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Unique index or primary key violation");
    }
    try {
      sorm.apply(Player.class, conn -> conn.insert(a, b, null));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Fail to get value from");
    }
  }

  @Test
  void testMultiRowInsertMany() {
    sorm.apply(Guest.class, conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.apply(Player.class, conn -> conn.merge(a, b, c));
  }


}
