package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

class SqlWithOrderedParametersTest {

  @Test
  void testToSqlStatementStringObjectArray() {
    SqlStatement sqlSt =
        OrderedParameterSql.toSqlStatement("select * from a where in(?)", List.of("alice", 1));
    assertThat(sqlSt.getParameters().length).isEqualTo(1);
    assertThat(sqlSt.getParameters()[0]).isEqualTo("'alice', 1");

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
