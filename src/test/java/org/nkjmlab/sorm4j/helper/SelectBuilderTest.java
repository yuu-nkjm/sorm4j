package org.nkjmlab.sorm4j.helper;

import org.junit.jupiter.api.Test;

class SelectBuilderTest {

  @Test
  void testBuild() {
    String sql = new SelectBuilder().select("*").from("customers").where("id=1").orderBy("id DESC")
        .limit(1).build();
    System.out.println(sql);
    org.assertj.core.api.Assertions.assertThat(sql)
        .isEqualTo("SELECT * FROM customers WHERE id=1 ORDER BY id DESC LIMIT 1");
  }

}