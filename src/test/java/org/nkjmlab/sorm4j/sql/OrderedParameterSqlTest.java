package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderedParameterSqlTest {

  @Test
  void testToSqlStatementStringObjectArray() {
    SqlStatement sqlSt = OrderedParameterSql.toSqlStatement(
        "select * from a where name like $?$ and address in(<?>) and id=$?$", "%Alice%",
        List.of("Tokyo", "Kyoto"), 1);

    System.out.println(sqlSt);
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
