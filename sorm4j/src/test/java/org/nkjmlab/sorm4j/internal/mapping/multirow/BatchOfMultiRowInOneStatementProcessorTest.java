package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory.MultiRowProcessorType;
import org.nkjmlab.sorm4j.util.logger.Log4jSormLogger;

class BatchOfMultiRowInOneStatementProcessorTest {

  private static Sorm sorm;
  private static final Player a = SormTestUtils.PLAYER_ALICE;
  private static final Player b = SormTestUtils.PLAYER_BOB;
  private static final Player c = SormTestUtils.PLAYER_CAROL;



  @BeforeAll
  static void setUp() {
    SormContext context = SormContext.builder()
        .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder()
            .setMultiRowProcessorType(MultiRowProcessorType.MULTI_ROW_AND_BATCH).build())
        .setLoggerOffAll().setLoggerOnAll().setLoggerSupplier(() -> Log4jSormLogger.getLogger())
        .build();
    sorm = Sorm.create(Sorm.createDataSource(jdbcUrl, user, password), context);
    SormTestUtils.dropAndCreateTableAll(sorm);

    assertThat(sorm.getContext().toString()).contains(MultiRowProcessorType.MULTI_ROW_AND_BATCH.name());
  }

  @BeforeEach
  void setUpEach() {
    SormTestUtils.dropAndCreateTableAll(sorm);
  }



  @Test
  void testMultiRowInsert() {
    sorm.accept(conn -> conn.insert(a, b));
  }

  @Test
  void testMultiRowInsertNull() {
    try {
      sorm.accept(conn -> conn.insert(a, b, c, a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Unique index or primary key violation");
    }
    try {
      sorm.accept(conn -> conn.insert(a, b, null));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("Fail to get value from");
    }
  }

  @Test
  void testMultiRowInsertMany() {
    sorm.accept(conn -> conn
        .insert(Stream.generate(() -> GUEST_ALICE).limit(3000).collect(Collectors.toList())));
  }

  @Test
  void testMultiRowMerge() {
    sorm.accept(conn -> conn
        .merge(Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }


}
