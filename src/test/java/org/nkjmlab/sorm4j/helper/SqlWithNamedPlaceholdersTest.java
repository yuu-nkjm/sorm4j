package org.nkjmlab.sorm4j.helper;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SqlWithNamedPlaceholdersTest {
  private String sql = "select * from simple where id=:idid and name=:name";
  private Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);

  @Test
  void testCreate() {
    SqlWithNamedPlaceholders sp =
        SqlWithNamedPlaceholders.createWithNamedParameters(sql, namedParams);

    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");

    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});

  }

  @Test
  void testBuilder() {
    SqlWithNamedPlaceholders sp =
        SqlWithNamedPlaceholders.createBuilder(sql).putAllParameters(namedParams).build();

    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");

    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});


  }
}
