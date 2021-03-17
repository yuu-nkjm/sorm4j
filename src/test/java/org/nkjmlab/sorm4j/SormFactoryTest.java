package org.nkjmlab.sorm4j;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class SormFactoryTest {

  @Test
  void testToOrmConnectionConnectionClassOfTString() {
    Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    TypedOrmConnection<Guest> orm = SormFactory.toOrmConnection(sorm.getJdbcConnection(),
        Guest.class, SormFactory.DEFAULT_CONFIG_NAME);
    orm.type(Guest.class).insert(GUEST_ALICE);
    assertThat(orm.untype().readOne(Integer.class, "select count(*) from guests")).isEqualTo(1);
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
