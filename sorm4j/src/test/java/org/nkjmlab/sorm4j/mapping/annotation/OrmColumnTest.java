package org.nkjmlab.sorm4j.mapping.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class OrmColumnTest {

  @Test
  void test() {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    sorm.executeUpdate("create table players (name varchar(255))");
    Player o = new Player();
    o.userName = "u";
    sorm.insert(o);
    assertThat(sorm.selectAll(Player.class).get(0).userName).isEqualTo("u");
  }

  public static class Player {
    @OrmColumn("name")
    public String userName;
  }
}
