package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.PLAYER_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSormWithNewDatabaseAndCreateTables;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory.MultiRowProcessorType;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;

  @BeforeAll
  static void setUp() {
    SormContext context =
        SormContext.builder()
            .setMultiRowProcessorFactory(
                MultiRowProcessorFactory.builder()
                    .setMultiRowProcessorType(MultiRowProcessorType.MULTI_ROW_AND_BATCH)
                    .build())
            .setLogContext(
                LogContext.builder()
                    .enableAll()
                    .disableAll()
                    .setLoggerSupplier(() -> Log4jSormLogger.getLogger())
                    .build())
            .build();
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables(context);
    assertThat(sorm.getContext().toString())
        .contains(MultiRowProcessorType.MULTI_ROW_AND_BATCH.name());
  }

  @BeforeEach
  void setUpEach() {
    sorm = createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testMultiRowInsert() {
    sorm.acceptHandler(conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowInsertNull() {
    try {
      sorm.acceptHandler(conn -> conn.insert(a, b, c, a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Unique index or primary key violation");
    }
    try {
      sorm.acceptHandler(conn -> conn.insert(a, b, null));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Fail to get value from");
    }
  }

  @Test
  void testMultiRowInsertMany() {
    sorm.acceptHandler(
        conn ->
            conn.insert(
                Stream.generate(() -> GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.acceptHandler(
        conn ->
            conn.merge(
                Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }
}
