package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.tool.Customer.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.tool.Customer;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class NamedParameterSqlTest {
  private String sql = "select * from simple where id=:idid and name=:name";
  private Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);

  private static Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();

  @Test
  void testCustomer() {
    sorm.apply(conn -> conn.insert(ALICE, BOB, CAROL, DAVE));

    SqlStatement statement1 = OrderedParameterSql.toSqlStatement(
        "select * from customer where name like $?$ and address in(<?>) and id=?", "%Alice%",
        List.of("Tokyo", "Kyoto"), 1);
    System.out.println(sorm.apply(conn -> conn.readList(Customer.class, statement1)));



    // String sql = "select * from customer where id=:id and address=:address";
    // SqlStatement statement =
    // NamedParameterSql.from(sql).bind("id", 1).bind("address", "Kyoto").toSqlStatement();
    // sorm.apply(conn -> conn.readList(Customer.class, statement));
    //
    // SqlStatement sqlSt = NamedParameterSql.toSqlStatement(
    // "select * from customer where address in(:living)", Map.of("living", List.of("Kyoto")));
    // List<Customer> ret = sorm.apply(conn -> conn.readList(Customer.class, sqlSt));
    // System.out.println(ret);
  }

  @Test
  void testCreate() {
    SqlStatement sp = NamedParameterSql.toSqlStatement(sql, namedParams);

    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});

    assertThat(sp.toString())
        .isEqualTo("sql=[select * from simple where id=? and name=?], parameters=[2, foo]");

    assertThat(SqlStatement.of("select * from test").toString()).contains("[select * from test]");
  }

  @Test
  void testBindAll() {
    SqlStatement sp = NamedParameterSql.from(sql).bindAll(namedParams).toSqlStatement();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");
    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBind() {

    SqlStatement sp = NamedParameterSql.from(sql).bind("name", "foo").bind("id", 1).bind("idid", 2)
        .toSqlStatement();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBindList() {

    SqlStatement sp = NamedParameterSql.from("select * from where ID in(<:names>)")
        .bind("names", List.of("foo", "bar")).toSqlStatement();

    System.out.println(sp);

    assertThat(sp.getSql()).contains("?,?");
    assertThat(sp.getParameters()[0]).isEqualTo("foo");
    assertThat(sp.getParameters()[1]).isEqualTo("bar");


  }

}
