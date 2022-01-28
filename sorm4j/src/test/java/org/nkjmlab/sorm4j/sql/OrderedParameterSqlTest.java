package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;

class OrderedParameterSqlTest {
  private static Sorm sorm = createSormWithNewDatabaseAndCreateTables();
  static {
    sorm.applyHandler(conn -> conn.insert(GUEST_ALICE, GUEST_BOB, GUEST_CAROL, GUEST_DAVE));

  }

  @Test
  void testParse() {
    ParameterizedSql statement =
        OrderedParameterSql.parse("select * from guests where name like {?} and address in(<?>)",
            "'A%'", List.of(GUEST_ALICE.getAddress(), GUEST_BOB.getAddress()));
    assertThat(statement.getSql())
        .isEqualTo("select * from guests where name like 'A%' and address in(?,?)");
    assertThat(Arrays.asList(statement.getParameters()))
        .isEqualTo(List.of(GUEST_ALICE.getAddress(), GUEST_BOB.getAddress()));
  }

  // @Test
  // void testToSqlStatementStringObjectArray() {
  // String sql = "select * from customer where id=? and address=?";
  // ParameterizedSql statement = OrderedParameterSql.from(sql).addParameter(1, "Kyoto").parse();
  // System.out.println(statement);
  // // assertThat(sqlSt.getParameters().length).isEqualTo(1);
  // // assertThat(sqlSt.getParameters()[0]).isEqualTo("'alice', 1");
  //
  // }
  //
  // @Test
  // void testToSqlStatementStringObject() {
  // ParameterizedSql sqlSt = OrderedParameterSql.parse("select * from a where id=? and id=?", 1,
  // 2);
  // assertThat(sqlSt.getParameters().length).isEqualTo(2);
  // assertThat(sqlSt.getParameters()[0]).isEqualTo(1);
  // assertThat(sqlSt.getParameters()[1]).isEqualTo(2);
  // }
}
