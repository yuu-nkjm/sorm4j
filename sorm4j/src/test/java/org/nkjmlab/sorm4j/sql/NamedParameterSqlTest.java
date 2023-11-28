package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.test.common.Customer;
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
          NamedParameterSqlParser.of(sql).bind("id", 1).bind("address", "Kyoto").parse();
      sorm.readList(Guest.class, statement);
    }
    {
      ParameterizedSql statement =
          NamedParameterSqlParser.parse(
              "select * from guests where name like {:name} and address in(<:address>) and id=:id",
              Map.of("id", 1, "address", List.of("Tokyo", "Kyoto"), "name", "'A%'"));
      sorm.applyHandler(conn -> conn.readList(Customer.class, statement));
    }
  }

  @Test
  void testCreate() {
    ParameterizedSql sp = NamedParameterSqlParser.parse(sql, namedParams);

    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});

    assertThat(sp.toString())
        .isEqualTo("sql=[select * from simple where id=? and name=?], parameters=[2, foo]");

    assertThat(ParameterizedSql.of("select * from test").toString())
        .contains("[select * from test]");
  }

  @Test
  void testBindAll() {
    ParameterizedSql sp = NamedParameterSqlParser.of(sql).bindAll(namedParams).parse();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");
    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBind() {

    ParameterizedSql sp =
        NamedParameterSqlParser.of(sql).bind("name", "foo").bind("id", 1).bind("idid", 2).parse();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBindList() {

    ParameterizedSql sp =
        NamedParameterSqlParser.of("select * from where ID in(<:player_names_1>)")
            .bind("player_names_1", List.of("foo", "bar"))
            .parse();

    assertThat(sp.getSql()).contains("?,?");
    assertThat(sp.getParameters()[0]).isEqualTo("foo");
    assertThat(sp.getParameters()[1]).isEqualTo("bar");
  }

  @Test
  void testBindListFail() {
    assertThatThrownBy(
            () ->
                NamedParameterSqlParser.of("select * from where ID in(<:names>)")
                    .bind("names", "foo")
                    .parse())
        .isInstanceOfSatisfying(
            SormException.class,
            e ->
                assertThat(e.getMessage())
                    .isEqualTo("<?> parameter should be bind Collection or Array"));
  }
}
