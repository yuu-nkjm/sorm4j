package org.nkjmlab.sorm4j.internal.mapping.multirow;

import static java.sql.Connection.*;
import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory.MultiRowProcessorType;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class SimpleBatchProcessorTest {

  private static Sorm sorm;

  @BeforeAll
  static void setUp() {
    DataSource ds = SormTestUtils.createDataSource();
    SormContext context = SormContext.builder().setMultiRowProcessorFactory(MultiRowProcessorFactory
        .builder().setMultiRowProcessorType(MultiRowProcessorType.SIMPLE_BATCH).build()).build();
    sorm = Sorm.create(ds, context);
  }


  @BeforeEach
  void setUpEach() {
    sorm = createSormWithNewContextAndTables();
  }


  @Test
  void testMultiRowInsert() {
    sorm.acceptHandler(conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
    sorm.acceptHandler(TRANSACTION_READ_COMMITTED, tr -> {
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
    sorm.acceptHandler(conn -> conn.insert(t));
  }

  @Test
  void testMultiRowMerge() {
    sorm.acceptHandler(conn -> conn
        .merge(Stream.generate(() -> PLAYER_ALICE).limit(3000).collect(Collectors.toList())));
  }


}
