package org.nkjmlab.sorm4j.result;

import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class ResultSetStreamTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewContextAndTables();
  }


  @Test
  void testA() {
    sorm.acceptHandler(m -> m.insert(PLAYER_ALICE, PLAYER_BOB));
    sorm.acceptHandler(m -> {
      try (Stream<Player> st = m.openStreamAll(Player.class)) {
        st.onClose(() -> System.out.println("close1"));
        st.findAny();
      }
    });

    sorm.acceptHandler(m -> {
      try (Stream<Player> st = m.openStreamAll(Player.class)) {
        st.onClose(() -> System.out.println("close2"));
        st.findAny();
      }
    });

    // sorm.accept(m -> {
    // Stream<Player> st = m.readAllLazy(Player.class).stream();
    // List<Player> ret = st.collect(Collectors.toList());
    // });
  }

}
