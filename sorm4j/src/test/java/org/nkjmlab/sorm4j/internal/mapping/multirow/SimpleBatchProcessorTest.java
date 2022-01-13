package org.nkjmlab.sorm4j.internal.mapping.multirow;

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
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.mapping.MultiRowProcessorFactory.MultiRowProcessorType;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class SimpleBatchProcessorTest {

  private static Sorm sorm;

  @BeforeAll
  static void setUp() {
    DataSource ds = Sorm.createDataSource(jdbcUrl, user, password);
    SormContext context = SormContext.builder().setMultiRowProcessorFactory(MultiRowProcessorFactory
        .builder().setMultiRowProcessorType(MultiRowProcessorType.SIMPLE_BATCH).build()).build();
    sorm = Sorm.create(ds, context);
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
