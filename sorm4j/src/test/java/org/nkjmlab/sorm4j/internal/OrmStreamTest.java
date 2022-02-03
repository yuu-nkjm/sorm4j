package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class OrmStreamTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void test() {
    sorm.insert(SormTestUtils.GUEST_ALICE);

    assertThat(sorm.getTableSql(Player.class).toString()).contains("select");

    int ret = sorm.streamAll(Guest.class).apply(strm -> strm.collect(Collectors.toList()).size());

    assertThat(ret).isEqualTo(1);


  }

}
