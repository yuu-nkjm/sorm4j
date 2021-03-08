package org.nkjmlab.sorm4j.sqlstatement;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sqlstatement.SelectBuilder.*;
import static org.nkjmlab.sorm4j.sqlstatement.SelectBuilder.as;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.Condition;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilderImpl.OrderBy;

class SelectQueryTest {


  @Test
  void testBuild1() {
    SelectBuilder builder = SelectBuilder.create();
    builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
    builder.groupBy("TEAM");
    builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));
    builder.groupBy("a");
    builder.distinct();
    builder.toString();

    String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).buildSqlString();
    assertThat(sql).contains(
        "select avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM order by age desc limit 10");

    builder.toPrettyString();
  }

  @Test
  void testBuild2() {
    Condition where = or(and("ID=A", "NAME=B"), and("YEAR=C", "DATE=D"),
        or(cond("ID", "=", q("test")), cond("NAME='Hello'")));
    Condition having = and("aveage_age>0", "a>0");
    OrderBy orderBy = order("age", "desc");

    String sql = SelectBuilder.create().select(as("AVG(age)", "aveage_age"), "ID").from("GUESTS")
        .where(where).having(having).orderBy(orderBy).limit(10, 30).buildSqlString();
    assertThat(sql).contains(
        "select AVG(age) as aveage_age, ID from GUESTS where ((ID=A and NAME=B) or (YEAR=C and DATE=D) or (ID='test' or NAME='Hello')) having (aveage_age>0 and a>0) order by age desc limit 10 offset 30");
  }


}