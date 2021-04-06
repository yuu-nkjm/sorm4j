package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class LazyResultSetTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }


  @Test
  void testA() {
    sorm.accept(m -> m.insert(PLAYER_ALICE, PLAYER_BOB));
    sorm.accept(m -> {
      try (Stream<Player> st = m.readAllLazy(Player.class).stream()) {
        st.onClose(() -> System.out.println("close1"));
        Optional<Player> ret = st.findAny();

      }
    });

    sorm.accept(m -> {
      Stream<Player> st = m.readAllLazy(Player.class).stream();
      st.onClose(() -> System.out.println("close2"));
      Optional<Player> ret = st.findAny();
    });

    // sorm.accept(m -> {
    // Stream<Player> st = m.readAllLazy(Player.class).stream();
    // List<Player> ret = st.collect(Collectors.toList());
    // });
  }

}
