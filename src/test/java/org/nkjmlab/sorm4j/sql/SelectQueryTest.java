package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import static org.nkjmlab.sorm4j.sql.SelectBuilder.*;
import static org.nkjmlab.sorm4j.sql.SelectBuilder.as;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class SelectQueryTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    sorm.accept(con -> con.insert(SormTestUtils.PLAYER_ALICE));
  }

  @Test
  void testExecBindQuery() {
    sorm.accept(con -> assertThat(
        con.type(Player.class).createSelectQuery().where("id=:id").bind("id", 1).readLazy().one())
            .isEqualTo(PLAYER_ALICE));
    sorm.accept(con -> assertThat(
        con.createSelectQuery(Player.class).where("id=:id").bind("id", 1).readLazy().one())
            .isEqualTo(PLAYER_ALICE));

    sorm.accept(con -> assertThat(
        con.createNamedParameterQuery(Player.class, "select * from players where id=:id")
            .bind("id", 1).readLazy().one()).isEqualTo(PLAYER_ALICE));

    sorm.accept(con -> assertThat(
        con.createOrderedParameterQuery(Player.class, "select * from players where id=?")
            .addParameter(1).readLazy().one()).isEqualTo(PLAYER_ALICE));

  }

  @Test
  void testExecBindAllQuery() {
    sorm.accept(con -> assertThat(con.type(Player.class).createSelectQuery().where("id=:id")
        .bindAll(Map.of("id", 1)).readLazy().one()).isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testExecAddQuery() {
    sorm.accept(con -> assertThat(
        con.type(Player.class).createSelectQuery().where("id=?").addParameter(1).readLazy().one())
            .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testExecAddAllQuery() {
    sorm.accept(
        con -> assertThat(con.type(Player.class).createSelectQuery().where(and("id=?", "name=?"))
            .addParameter(PLAYER_ALICE.getId(), PLAYER_ALICE.getName()).readLazy().one())
                .isEqualTo(PLAYER_ALICE));
  }

  @Test
  void testSelectQueryCond() {
    sorm.accept(con -> {
      SelectQuery<Guest> builder = con.type(Guest.class).createSelectQuery();
      builder.orderBy(order("id", "asc"));
      builder.having("avg(age)>100");
      assertThat(builder.toString())
          .isEqualTo("select * from GUESTS having avg(age)>100 order by id asc");

      builder.having(cond("avg(age)>1000"));
      assertThat(builder.toString())
          .isEqualTo("select * from GUESTS having avg(age)>1000 order by id asc");

      builder.limit(10, 30);
      assertThat(builder.toString()).isEqualTo(
          "select * from GUESTS having avg(age)>1000 order by id asc limit 10 offset 30");

    });

  }

  @Test
  void testCompareSelectBuilderAndSelectQuery() {
    sorm.accept(con -> {
      SelectQuery<Guest> builder = con.type(Guest.class).createSelectQuery();
      builder.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
      builder.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));
      builder.groupBy("TEAM");
      builder.having("avg(age)>0");
      builder.distinct();
      String sql = builder.from("GUESTS").orderBy("age", "desc").limit(10).buildSql();
      System.out.println(sql);
      assertThat(sql).contains(
          "select distinct avg(AGE) as AVERAGE_AGE, TEAM from GUESTS where ((ID>100 and COUNTRY IN (?)) or YEAR>2001) group by TEAM having avg(age)>0 order by age desc limit 10");

      SelectBuilder bs = SelectBuilder.create();
      bs.select(as("avg(AGE)", "AVERAGE_AGE"), "TEAM");
      bs.where(or(and("ID>100", "COUNTRY IN (?)"), "YEAR>2001"));
      bs.groupBy("TEAM");
      bs.having("avg(age)>0");
      bs.distinct();
      bs.toString();
      String sql2 = bs.from("GUESTS").orderBy("age", "desc").limit(10).buildSql();

      assertThat(sql).isEqualTo(sql2);
      assertThat(builder.parse().getSql()).isEqualTo(sql2);
      assertThat(builder.toPrettyString()).isEqualTo(bs.toPrettyString());
      assertThat(builder.toPrettyString(true)).isEqualTo(bs.toPrettyString(true));
    });

  }



}
