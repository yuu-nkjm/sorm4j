package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.Customer.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Customer;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class NamedParameterSqlTest {
  private String sql = "select * from simple where id=:idid and name=:name";
  private Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);

  private static Sorm sorm = SormTestUtils.createSormAndDropAndCreateTableAll();

  @Test
  void testCustomer() {
    sorm.apply(conn -> conn.insert(ALICE, BOB, CAROL, DAVE));

    {
      ParameterizedSql statement = OrderedParameterSql.parse(
          "select * from customer where name like {?} and address in(<?>) and id=?", "A%",
          List.of("Tokyo", "Kyoto"), 1);
      System.out.println(statement);
      List<Customer> ret = sorm.apply(conn -> conn.readList(Customer.class, statement));
      System.out.println(ret);
    }

    {
      String sql = "select * from customer where id=:id and address=:address";
      ParameterizedSql statement =
          NamedParameterSql.from(sql).bind("id", 1).bind("address", "Kyoto").parse();
      System.out.println(statement);
      List<Customer> ret = sorm.apply(conn -> conn.readList(Customer.class, statement));
      System.out.println(ret);
    }
    {
      ParameterizedSql statement = NamedParameterSql.parse(
          "select * from customer where name like {:name} and address in(<:address>) and id=:id",
          Map.of("id", 1, "address", List.of("Tokyo", "Kyoto"), "name", "A%"));
      System.out.println(statement);
      List<Customer> ret = sorm.apply(conn -> conn.readList(Customer.class, statement));
      System.out.println(ret);
    }
  }

  @Test
  void testCreate() {
    ParameterizedSql sp = NamedParameterSql.parse(sql, namedParams);

    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});

    assertThat(sp.toString())
        .isEqualTo("sql=[select * from simple where id=? and name=?], parameters=[2, foo]");

    assertThat(ParameterizedSql.from("select * from test").toString()).contains("[select * from test]");
  }

  @Test
  void testBindAll() {
    ParameterizedSql sp = NamedParameterSql.from(sql).bindAll(namedParams).parse();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");
    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBind() {

    ParameterizedSql sp = NamedParameterSql.from(sql).bind("name", "foo").bind("id", 1).bind("idid", 2)
        .parse();
    assertThat(sp.getSql()).isEqualTo("select * from simple where id=? and name=?");

    assertThat(sp.getParameters()).isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBindList() {

    ParameterizedSql sp = NamedParameterSql.from("select * from where ID in(<:names>)")
        .bind("names", List.of("foo", "bar")).parse();

    System.out.println(sp);

    assertThat(sp.getSql()).contains("?,?");
    assertThat(sp.getParameters()[0]).isEqualTo("foo");
    assertThat(sp.getParameters()[1]).isEqualTo("bar");


  }

}
