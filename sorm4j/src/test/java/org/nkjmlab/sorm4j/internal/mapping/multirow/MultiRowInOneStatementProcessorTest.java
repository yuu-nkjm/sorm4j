package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.context.MultiRowProcessorFactory.MultiRowProcessorType.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class MultiRowInOneStatementProcessorTest {

  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;


  private static List<Sorm> sorms = List.of(SIMPLE_BATCH, MULTI_ROW, MULTI_ROW_AND_BATCH).stream()
      .map(type -> SormTestUtils.createSormWithNewDatabaseAndCreateTables(SormContext.builder()
          .setMultiRowProcessorFactory(
              MultiRowProcessorFactory.builder().setMultiRowProcessorType(type).build())
          .build()))
      .collect(Collectors.toList());



  @Test
  void testMultiRowInsert() {
    sorms.forEach(sorm -> {
      sorm.deleteAll(Player.class);
      sorm.insert(a, b);
    });

  }

  @Test
  void testMultiRowInsertNull() {
    sorms.forEach(sorm -> {
      sorm.deleteAll(Player.class);
      try {
        sorm.insert(a, b, a, c);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("Unique index or primary key violation");
      }
      try {
        sorm.insert(a, b, null);
        failBecauseExceptionWasNotThrown(Exception.class);
      } catch (Exception e) {
        assertThat(e.getMessage()).contains("Fail to get value from");
      }
    });
  }

  @Test
  void testMultiRowInsertMany() {
    sorms.forEach(sorm -> sorm.insert(
        Stream.generate(() -> SormTestUtils.GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorms.forEach(sorm -> sorm.merge(Stream.generate(() -> SormTestUtils.PLAYER_ALICE).limit(3000)
        .collect(Collectors.toList())));
  }


}
