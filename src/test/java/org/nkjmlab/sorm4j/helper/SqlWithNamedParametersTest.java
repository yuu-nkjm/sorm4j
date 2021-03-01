package org.nkjmlab.sorm4j.helper;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.SqlStatement;
import org.nkjmlab.sorm4j.SqlWithNamedParameters;

class SqlWithNamedParametersTest {
  private String sql = "select * from simple where id=:idid and name=:name";
  private Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);

  @Test
  void testCreate() {
    SqlStatement sp = SqlWithNamedParameters.toSqlStatement(sql, namedParams);

    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");

    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});

    org.assertj.core.api.Assertions.assertThat(sp.toString())
        .isEqualTo("[select * from simple where id=? and name=?] with [2, foo]");


  }

  @Test
  void testBindAll() {
    SqlStatement sp = new SqlWithNamedParameters(sql).bindAll(namedParams).toSqlStatement();
    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");
    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});
  }

  @Test
  void testBind() {

    SqlStatement sp = new SqlWithNamedParameters(sql).bind("name", "foo").bind("id", 1)
        .bind("idid", 2).toSqlStatement();

    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");

    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});


  }

}
