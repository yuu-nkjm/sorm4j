package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;

class NamedParametersQueryImplTest {

  private Sorm sorm;

  @BeforeEach
  void testBeforeEach() {
    this.sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }

  @Test
  void testToSqlStatement() {
    sorm.accept(con -> {
      con.insert(PLAYER_ALICE);
      ParameterizedSql ret = con.type(Player.class)
          .createNamedParameterQuery("select * from players where id=:id and name=:name").parse();
      assertThat(ret.getSql()).isEqualTo(
          ParameterizedSql.from("select * from players where id=:id and name=:name").getSql());
    });
    sorm.accept(con -> {
      ParameterizedSql ret = con.createNamedParameterQuery(Player.class,
          "select * from players where id=:id and name=:name").parse();
      assertThat(ret.getSql()).isEqualTo(
          ParameterizedSql.from("select * from players where id=:id and name=:name").getSql());
    });
  }

  @Test
  void testBindAll() {
    sorm.accept(con -> {
      con.insert(PLAYER_ALICE);
      Player ret = con.type(Player.class)
          .createNamedParameterQuery("select * from players where id=:id and name=:name")
          .bindAll(Map.of("id", 1, "name", "Alice")).readOne();
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
    sorm.accept(con -> {
      Player ret = con
          .createNamedParameterQuery(Player.class,
              "select * from players where id=:id and name=:name")
          .bindAll(Map.of("id", 1, "name", "Alice")).readOne();
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
  }

  @Test
  void testBind() {
    sorm.accept(con -> {
      con.insert(PLAYER_ALICE);
      Player ret =
          con.type(Player.class).createNamedParameterQuery("select * from players where id=:id")
              .bind("id", 1).readFirst();
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
    sorm.accept(con -> {
      Player ret = con.createNamedParameterQuery(Player.class, "select * from players where id=:id")
          .bind("id", 1).readFirst();
      assertThat(ret).isEqualTo(PLAYER_ALICE);
    });
  }

}
