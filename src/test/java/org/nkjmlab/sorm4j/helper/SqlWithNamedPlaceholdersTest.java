package org.nkjmlab.sorm4j.helper;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SqlWithNamedPlaceholdersTest {

  @Test
  void testGetSql() {
    String sql = "select * from simple where id=:idid and name=:name";
    Map<String, Object> namedParams = Map.of("name", "foo", "id", 1, "idid", 2);
    SqlWithNamedPlaceholders sp =
        SqlWithNamedPlaceholders.createWithNamedParameters(sql, namedParams);

    org.assertj.core.api.Assertions.assertThat(sp.getSql())
        .isEqualTo("select * from simple where id=? and name=?");

    org.assertj.core.api.Assertions.assertThat(sp.getParameters())
        .isEqualTo(new Object[] {2, "foo"});

  }

}
