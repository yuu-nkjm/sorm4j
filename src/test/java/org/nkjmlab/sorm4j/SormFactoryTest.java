package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class SormFactoryTest {

  @Test
  void testGetConfigString() {
    String str = SormFactory.getConfigString(SormFactory.DEFAULT_CONFIG_NAME);
    assertThat(str).contains("configName=DEFAULT_CONFIG");

  }



  @Test
  void testToOrmConnectionConnectionString() {
    Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    OrmConnection orm =
        SormFactory.toOrmConnection(sorm.getJdbcConnection(), SormFactory.DEFAULT_CONFIG_NAME);
    orm.insert(PLAYER_ALICE);
    assertThat(orm.type(Player.class).readFirst("select * from players")).isEqualTo(PLAYER_ALICE);
  }

}
