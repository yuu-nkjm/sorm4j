package org.nkjmlab.sorm4j.util.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.as;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Guest;

class SelectSqlTest {

  @Test
  void test() {

    assertThat(literal(new String[] {"a", "b", null})).isEqualTo("array ['a', 'b', null]");
    assertThat(literal(List.of("a", "b"))).isEqualTo("'a', 'b'");
    assertThat(literal(1)).isEqualTo(String.valueOf(1));
    assertThat(literal(true)).isEqualTo(String.valueOf(true));

    assertThat(literal(null)).isEqualTo("null");
    assertThat(literal("?")).isEqualTo("?");

    assertThat(joinCommaAndSpace("a", "b")).isEqualTo("a, b");
    assertThat(joinSpace("a", "b")).isEqualTo("a b");

    assertThat(select("a")).isEqualTo(" select a ");
    assertThat(select("a", "b")).isEqualTo(" select a, b ");

    assertThat(SelectSql.in("id", List.of("a", 1)).toString()).isEqualTo(" id in ('a', 1) ");
    assertThat(SelectSql.between("id", 1, 2).toString()).isEqualTo(" id between 1 and 2 ");

    assertThat(SelectSql.groupBy("id, name")).isEqualTo(" group by id, name ");
    assertThat(SelectSql.orderBy("id, name")).isEqualTo(" order by id, name ");
    assertThat(SelectSql.limit(1)).isEqualTo(" limit 1 ");

    assertThat(SelectSql.orderByAsc("id")).isEqualTo(" order by id asc ");
    assertThat(orderByDesc("id")).isEqualTo(" order by id desc ");
    assertThat(func("count", "*")).isEqualTo(" count(*) ");
    assertThat(func("atan2", "x", "y")).isEqualTo(" atan2(x, y) ");
    assertThat(count("*")).isEqualTo(" count(*) ");
    assertThat(sum("*")).isEqualTo(" sum(*) ");
    assertThat(avg("*")).isEqualTo(" avg(*) ");
    assertThat(castAs("id", "double")).isEqualTo(" cast(id as double) ");
    assertThat(columnWithTableName("guest", "id", "name")).isEqualTo(" guest.id , guest.name ");
    assertThat(selectDistinct("id")).isEqualTo(" select distinct id ");
    assertThat(where()).isEqualTo(" where ");
    assertThat(where("id=?")).isEqualTo(" where id=? ");
    assertThat(where(and("id=?", "name=?", "address=?")))
        .isEqualTo(" where (id=? and name=? and address=?) ");
    assertThat(op(op("A", "/", "B"), "+", op("C", "/", "D"))).isEqualTo("((A / B) + (C / D))");
  }

  @Test
  void testBuild1() {
    Sorm sorm = createSormWithNewDatabaseAndCreateTables();
    sorm.acceptHandler(
        con -> {
          String sql = SelectSql.builder().from(con.getTableName(Guest.class)).build();
          assertThat(sql).contains("select * from GUESTS");
        });

    SelectSql.Builder builder = SelectSql.builder();
    builder.distinct();
    builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
    builder.groupBy("TEAM");
    builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));

    String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).build();
    assertThat(sql)
        .isEqualTo(
            "select distinct avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM order by age desc limit 10");

    builder.toPrettyString();
  }

  @Test
  void testBuild2() {
    SelectSql.Condition where =
        or(
            and("ID=A", "NAME=B"),
            and("YEAR=C", "DATE=D"),
            or(cond("ID", "=", quote("test")), cond("NAME='Hello'")));
    SelectSql.Condition having = and("aveage_age>0", "a>0");

    String sql =
        SelectSql.builder()
            .select(as("AVG(age)", "aveage_age"), "ID")
            .from("GUESTS")
            .where(where)
            .having(having)
            .orderBy("age", "desc")
            .limit(10, 30)
            .build();
    assertThat(sql)
        .isEqualTo(
            "select AVG(age) as aveage_age, ID from GUESTS where ((ID=A and NAME=B) or (YEAR=C and DATE=D) or (ID = 'test' or NAME='Hello')) having (aveage_age>0 and a>0) order by age desc limit 10 offset 30");
  }
}
