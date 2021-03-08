package org.nkjmlab.sorm4j.sqlstatement;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.sqlstatement.SelectBuilder.*;
import static org.nkjmlab.sorm4j.sqlstatement.SelectBuilder.as;
import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class SelectQueryTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    sorm.run(Player.class, con -> con.insert(SormTestUtils.PLAYER_ALICE));
  }

  @Test
  void testExecBindQuery() {
    sorm.run(Player.class,
        con -> assertThat(con.createSelectQuery().where("id=:id").bind("id", 1).readLazy().one())
            .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testExecBindAllQuery() {
    sorm.run(Player.class,
        con -> assertThat(
            con.createSelectQuery().where("id=:id").bindAll(Map.of("id", 1)).readLazy().one())
                .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testExecAddQuery() {
    sorm.run(Player.class,
        con -> assertThat(con.createSelectQuery().where("id=?").add(1).readLazy().one())
            .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testExecAddAllQuery() {
    sorm.run(Player.class,
        con -> assertThat(con.createSelectQuery().where(and("id=?", "name=?"))
            .add(PLAYER_ALICE.getId(), PLAYER_ALICE.getName()).readLazy().one())
                .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testCompareSelectBuilderAndSelectQuery() {
    sorm.run(Guest.class, con -> {
      SelectQuery<Guest> builder = con.createSelectQuery();
      builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
      builder.groupBy("TEAM");
      builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));
      builder.groupBy("TEAM");
      builder.distinct();
      builder.toString();
      String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).buildSqlString();
      assertThat(sql).contains(
          "select distinct avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM order by age desc limit 10");

      SelectBuilder bs = SelectBuilder.create();
      bs.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
      bs.groupBy("TEAM");
      bs.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));
      bs.groupBy("TEAM");
      bs.distinct();
      bs.toString();
      String sql2 = bs.from("GUESTS").orderBy("age", "desc").limit(10).buildSqlString();

      assertThat(sql).isEqualTo(sql2);
      assertThat(builder.toPrettyString()).isEqualTo(bs.toPrettyString());
      assertThat(builder.toPrettyString(true)).isEqualTo(bs.toPrettyString(true));
    });

  }



}
