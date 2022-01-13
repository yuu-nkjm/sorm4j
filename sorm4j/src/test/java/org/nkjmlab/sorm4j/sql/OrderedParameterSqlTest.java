package org.nkjmlab.sorm4j.sql;

import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Customer;

class OrderedParameterSqlTest {
  private static Sorm sorm = createSormAndDropAndCreateTableAll();
  static {
    sorm.apply(conn -> conn.insert(GUEST_ALICE, GUEST_BOB, GUEST_CAROL, GUEST_DAVE));

  }

  @Test
  void testParse() {
    ParameterizedSql statement = OrderedParameterSql.parse(
        "select * from customer where name like {?} and address in(<?>) and id=?", "'A%'",
        List.of("Tokyo", "Kyoto"), 1);
    List<Customer> ret = sorm.apply(conn -> conn.readList(Customer.class, statement));
    System.out.println(ret);

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
