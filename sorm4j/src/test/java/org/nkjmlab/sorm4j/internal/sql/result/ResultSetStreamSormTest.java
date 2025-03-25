package org.nkjmlab.sorm4j.internal.sql.result;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.sql.result.ResultSetStream;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class ResultSetStreamSormTest {

  @Test
  void testApplyWithHandlerException() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    ResultSetStream<Guest> guestStream = sorm.getTable(Guest.class).streamAll();

    FunctionHandler<Stream<Guest>, Integer> handler =
        stream -> {
          throw new RuntimeException("Handler processing error");
        };

    assertThatThrownBy(() -> guestStream.apply(handler))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Handler processing error");
  }

  @Test
  void testApplyWithClosedStream() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    ResultSetStream<Guest> guestStream = sorm.getTable(Guest.class).streamAll();

    Stream<Guest> stream = guestStream.apply(e -> e);
    stream.close();

    assertThatThrownBy(stream::count).isInstanceOf(Exception.class);
  }

  @Test
  void testApplyWithEmptyResultSet() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    ResultSetStream<Guest> guestStream = sorm.getTable(Guest.class).streamAll();
    Stream<Guest> stream = guestStream.apply(e -> e);

    assertThatThrownBy(
            () -> stream.findFirst().orElseThrow(() -> new RuntimeException("No data found")))
        .isInstanceOf(Exception.class);
  }
}
