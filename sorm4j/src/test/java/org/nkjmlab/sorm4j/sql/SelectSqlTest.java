package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.as;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.util.sql.SelectSql;

class SelectSqlTest {

  @Test
  void testBuildSorm() {
    Sorm sorm = createSormWithNewContextAndTables();

    sorm.accept(con -> {
      String sql = SelectSql.builder().from(con.getTableName(Guest.class)).build();
      assertThat(sql).contains("select * from GUESTS");
    });

  }

  @Test
  void testBuild() {

    assertThat(op(op("A", "/", "B"), "+", op("C", "/", "D"))).isEqualTo("((A / B) + (C / D))");

  }

  @Test
  void testBuild1() {
    SelectSql.Builder builder = SelectSql.builder();
    builder.distinct();
    builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
    builder.groupBy("TEAM");
    builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));

    String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).build();
    assertThat(sql).isEqualTo(
        "select distinct avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM order by age desc limit 10");

    builder.toPrettyString();
  }

  @Test
  void testBuild2() {
    SelectSql.Condition where = or(and("ID=A", "NAME=B"), and("YEAR=C", "DATE=D"),
        or(cond("ID", "=", quote("test")), cond("NAME='Hello'")));
    SelectSql.Condition having = and("aveage_age>0", "a>0");

    String sql = SelectSql.builder().select(as("AVG(age)", "aveage_age"), "ID").from("GUESTS")
        .where(where).having(having).orderBy("age", "desc").limit(10, 30).build();
    assertThat(sql).isEqualTo(
        "select AVG(age) as aveage_age, ID from GUESTS where ((ID=A and NAME=B) or (YEAR=C and DATE=D) or (ID = 'test' or NAME='Hello')) having (aveage_age>0 and a>0) order by age desc limit 10 offset 30");
  }


}
