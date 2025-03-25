package org.nkjmlab.sorm4j.sql.parameterize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_BOB;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_CAROL;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_DAVE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSormWithNewDatabaseAndCreateTables;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;

class OrderedParameterSqlBuilderTest {
  private static Sorm sorm = createSormWithNewDatabaseAndCreateTables();

  static {
    sorm.applyHandler(conn -> conn.insert(GUEST_ALICE, GUEST_BOB, GUEST_CAROL, GUEST_DAVE));
  }

  @Test
  void testParse() {
    ParameterizedSql statement =
        ParameterizedSql.withOrderedParameters(
            "select * from guests where address in(<?>)",
            List.of(GUEST_ALICE.getAddress(), GUEST_BOB.getAddress()));
    assertThat(statement.getSql()).isEqualTo("select * from guests where address in(?,?)");
    assertThat(Arrays.asList(statement.getParameters()))
        .isEqualTo(List.of(GUEST_ALICE.getAddress(), GUEST_BOB.getAddress()));
  }

  // @Test
  // void testToSqlStatementStringObjectArray() {
  // String sql = "select * from customer where id=? and address=?";
  // ParameterizedSql statement = OrderedParameterSqlParser.from(sql).addParameter(1,
  // "Kyoto").parse();
  // assertThat(statement);
  // // assertThat(sqlSt.getParameters().length).isEqualTo(1);
  // // assertThat(sqlSt.getParameters()[0]).isEqualTo("'alice', 1");
  //
  // }
  //
  // @Test
  // void testToSqlStatementStringObject() {
  // ParameterizedSql sqlSt = OrderedParameterSqlParser.parse("select * from a where id=? and id=?",
  // 1,
  // 2);
  // assertThat(sqlSt.getParameters().length).isEqualTo(2);
  // assertThat(sqlSt.getParameters()[0]).isEqualTo(1);
  // assertThat(sqlSt.getParameters()[1]).isEqualTo(2);
  // }
}
