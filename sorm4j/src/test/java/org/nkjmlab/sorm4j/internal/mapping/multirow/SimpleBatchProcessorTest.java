package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory.MultiRowProcessorType;

class SimpleBatchProcessorTest {

  private static Sorm sorm;

  @BeforeAll
  static void setUp() {
    sorm = Sorm.builder().setDataSource(jdbcUrl, user, password)
        .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder()
            .setMultiRowProcessorType(MultiRowProcessorType.SIMPLE_BATCH).build())
        .build();
  }


  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }


  @Test
  void testMultiRowInsert() {
    sorm.accept(conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
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
    List<Guest> t = Stream.generate(() -> GUEST_ALICE).limit(1000).collect(Collectors.toList());
    sorm.accept(conn -> conn.insert(t));
  }

  @Test
  void testMultiRowMerge() {
    sorm.accept(conn -> conn
        .merge(Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }


}
