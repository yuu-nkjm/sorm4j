package org.nkjmlab.sorm4j.helper;

import org.junit.jupiter.api.Test;

class SelectBuilderTest {

  @Test
  void testBuild() {
    String sql = new SimpleSelectBuilder().select("*").from("customers").where("id=1")
        .orderBy("id DESC").limit(1).build();
    org.assertj.core.api.Assertions.assertThat(sql)
        .isEqualTo("SELECT * FROM customers WHERE id=1 ORDER BY id DESC LIMIT 1");

    sql = new SimpleSelectBuilder().select("*").from("customers").where("id=1").orderBy("id DESC")
        .limit(1).toString();
    org.assertj.core.api.Assertions.assertThat(sql)
        .isEqualTo("SELECT * FROM customers WHERE id=1 ORDER BY id DESC LIMIT 1");
  }

}
