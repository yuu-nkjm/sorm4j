package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OrderedParameterSqlTest {

  @Test
  void testToSqlStatementStringObjectArray() {
    String sql = "select * from customer where id=? and address=?";
    SqlStatement statement =
        OrderedParameterSql.from(sql).addParameter(1, "Kyoto").toSqlStatement();
    System.out.println(statement);
    // assertThat(sqlSt.getParameters().length).isEqualTo(1);
    // assertThat(sqlSt.getParameters()[0]).isEqualTo("'alice', 1");

  }

  @Test
  void testToSqlStatementStringObject() {
    SqlStatement sqlSt =
        OrderedParameterSql.toSqlStatement("select * from a where id=? and id=?", 1, 2);
    assertThat(sqlSt.getParameters().length).isEqualTo(2);
    assertThat(sqlSt.getParameters()[0]).isEqualTo(1);
    assertThat(sqlSt.getParameters()[1]).isEqualTo(2);
  }
}
