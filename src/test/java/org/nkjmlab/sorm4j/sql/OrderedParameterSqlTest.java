package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OrderedParameterSqlTest {

  @Test
  void testToSqlStatementStringObjectArray() {
    String sql = "select * from customer where id=? and address=?";
    ParameterizedSql statement =
        OrderedParameterSql.from(sql).addParameter(1, "Kyoto").parse();
    System.out.println(statement);
    // assertThat(sqlSt.getParameters().length).isEqualTo(1);
    // assertThat(sqlSt.getParameters()[0]).isEqualTo("'alice', 1");

  }

  @Test
  void testToSqlStatementStringObject() {
    ParameterizedSql sqlSt =
        OrderedParameterSql.parse("select * from a where id=? and id=?", 1, 2);
    assertThat(sqlSt.getParameters().length).isEqualTo(2);
    assertThat(sqlSt.getParameters()[0]).isEqualTo(1);
    assertThat(sqlSt.getParameters()[1]).isEqualTo(2);
  }
}
