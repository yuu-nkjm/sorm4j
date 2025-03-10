package org.nkjmlab.sorm4j.sql.parameterize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_ALICE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_BOB;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_CAROL;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.GUEST_DAVE;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.createSormWithNewDatabaseAndCreateTables;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.test.common.Guest;

class NamedParameterSqlTest {
  private String sql = "select * from simple where id=:idid and name=:name";
  private Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);

  private static Sorm sorm = createSormWithNewDatabaseAndCreateTables();

  static {
    sorm.applyHandler(conn -> conn.insert(GUEST_ALICE, GUEST_BOB, GUEST_CAROL, GUEST_DAVE));
  }

  @Test
  void testCustomer() {
    {
      String sql = "select * from guests where id=:id and address=:address";
      ParameterizedSql statement =
          NamedParameterSqlBuilder.builder(sql)
              .bindParameter("id", 1)
              .bindParameter("address", "Kyoto")
              .build();
      sorm.readList(Guest.class, statement);
    }
    {
      ParameterizedSql statement =
          ParameterizedSql.withNamedParameters(
              "select * from guests where address in(<:address>) and id=:id",
              Map.of("id", 1, "address", List.of("Tokyo", "Kyoto")));
      sorm.applyHandler(conn -> conn.readList(Guest.class, statement));
    }
  }

  @Test
  void testbuild() {
    ParameterizedSql sp = ParameterizedSql.withNamedParameters(sql, namedParams);

    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});

    assertThat(sp.toString())
        .isEqualTo("sql=[select * from simple where id=? and name=?], parameters=[2, foo]");

    assertThat(ParameterizedSql.of("select * from test").toString())
        .contains("[select * from test]");
  }

  @Test
  void testBindAll() {
    ParameterizedSql sp = NamedParameterSqlBuilder.builder(sql).bindParameters(namedParams).build();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");
    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBind() {

    ParameterizedSql sp =
        NamedParameterSqlBuilder.builder(sql)
            .bindParameter("name", "foo")
            .bindParameter("id", 1)
            .bindParameter("idid", 2)
            .build();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBindList() {

    ParameterizedSql sp =
        NamedParameterSqlBuilder.builder("select * from where ID in(<:player_names_1>)")
            .bindParameter("player_names_1", List.of("foo", "bar"))
            .build();

    assertThat(sp.getSql()).contains("?,?");
    assertThat(sp.getParameters()[0]).isEqualTo("foo");
    assertThat(sp.getParameters()[1]).isEqualTo("bar");
  }

  @Test
  void testBindListFail() {
    assertThatThrownBy(
            () ->
                NamedParameterSqlBuilder.builder("select * from where ID in(<:names>)")
                    .bindParameter("names", "foo")
                    .build())
        .isInstanceOfSatisfying(
            SormException.class,
            e ->
                assertThat(e.getMessage())
                    .isEqualTo("<?> parameter should be bind Collection or Array"));
  }
}
