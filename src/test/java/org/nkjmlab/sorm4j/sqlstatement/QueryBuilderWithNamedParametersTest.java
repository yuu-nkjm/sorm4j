package org.nkjmlab.sorm4j.sqlstatement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.Guest;
import org.nkjmlab.sorm4j.util.SormTestUtils;

class QueryBuilderWithNamedParametersTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTableAll(sorm);
  }

  @Test
  void testBind() {
    sorm.run(Guest.class, con -> {
      try {
        con.createSelectQuery().where("id=?").readFirst();
        Guest ret = con.createSelectQuery().where("id=:id").add(1).readFirst();
        System.out.println(ret);
        String s1 =
            con.createSelectQuery().where("id=?").bind("idnot", 1).toSqlStatement().toString();
        System.out.println();
        System.out.println(s1);

        // assertThat(sql).contains();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

  }

}
