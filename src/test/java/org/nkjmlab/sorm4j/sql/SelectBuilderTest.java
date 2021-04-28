package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sql.ParameterizedSql.*;
import static org.nkjmlab.sorm4j.sql.SelectBuilder.*;
import static org.nkjmlab.sorm4j.sql.SelectBuilder.as;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class SelectBuilderTest {

  @Test
  void testBuildSorm() {
    Sorm sormImpl = SormTestUtils.createSorm();
    SormTestUtils.dropAndCreateTableAll(sormImpl);

    sormImpl.accept(con -> {
      String sql = SelectBuilder.create().from(con.type(Guest.class).getTableName()).buildSql();
      assertThat(sql).contains("select * from GUESTS");
    });

  }

  @Test
  void testBuild1() {
    SelectBuilder builder = SelectBuilder.create();
    builder.distinct();
    builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
    builder.groupBy("TEAM");
    builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));

    String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).parse().getSql();
    assertThat(sql).contains(
        "select distinct avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM order by age desc limit 10");

    builder.toPrettyString();
  }

  @Test
  void testBuild2() {
    SelectBuilder.Condition where = or(and("ID=A", "NAME=B"), and("YEAR=C", "DATE=D"),
        or(cond("ID", "=", quote("test")), cond("NAME='Hello'")));
    SelectBuilder.Condition having = and("aveage_age>0", "a>0");
    SelectBuilder.OrderBy orderBy = order("age", "desc");

    String sql = SelectBuilder.create().select(as("AVG(age)", "aveage_age"), "ID").from("GUESTS")
        .where(where).having(having).orderBy(orderBy).limit(10, 30).buildSql();
    assertThat(sql).contains(
        "select AVG(age) as aveage_age, ID from GUESTS where ((ID=A and NAME=B) or (YEAR=C and DATE=D) or (ID='test' or NAME='Hello')) having (aveage_age>0 and a>0) order by age desc limit 10 offset 30");
  }


}
