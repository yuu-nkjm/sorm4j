package org.nkjmlab.sorm4j.sql.parameterize;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

class ParameterizedSqlTest {

  @Test
  void testLietral() {
    assertThat(SqlStringUtils.literal(List.of("a", "b"))).isEqualTo("'a', 'b'");
    assertThat(SqlStringUtils.literal(null)).isEqualTo("null");
    assertThat(SqlStringUtils.literal("?")).isEqualTo("?");
    assertThat(SqlStringUtils.literal("test")).isEqualTo("'test'");
    assertThat(SqlStringUtils.literal("hi, my name's tim.")).isEqualTo("'hi, my name''s tim.'");
  }

  @Test
  void testParseAsOrdered() {
    String sql = "select * from guest where id=?";
    Object[] params = {99};
    ParameterizedSql ps = ParameterizedSql.withOrderedParameters(sql, params);
    assertThat(ps.getSql()).isEqualTo(sql);
    assertThat(ps.getParameters()).isEqualTo(params);
  }

  @Test
  void testParseAsNamed() {
    String sql = "select * from guest where id=:id";
    Map<String, Object> map = Map.of("id", 1);
    ParameterizedSql ps = ParameterizedSql.withNamedParameters(sql, map);

    String sql1 = "select * from guest where id=?";
    Object[] params = {1};
    assertThat(ps.getSql()).isEqualTo(sql1);
    assertThat(ps.getParameters()).isEqualTo(params);

    assertThat(ps.getExecutableSql()).contains("select * from guest where id=1");
    assertThat(ps.toString()).contains("sql=[select * from guest where id=?], parameters=[1]");
  }

  @Test
  void testEmbeddedOrdered() {
    String sql = "select * from guest where id=?";
    Object[] params = {99};
    assertThat(ParameterizedSql.withOrderedParameters(sql, params).getExecutableSql())
        .contains("select * from guest where id=99");
  }

  @Test
  void testEmbeddedMap() {
    String sql = "select * from guest where name=:name and id=:id";
    Map<String, Object> params = Map.of("id", 99, "name", "a");

    assertThat(ParameterizedSql.withNamedParameters(sql, params).getExecutableSql())
        .contains("select * from guest where name='a' and id=99");
  }

  @Test
  void testEmbeddedMapFail() {

    String sql = "select * from guest where name=:name and id=:id";
    ParameterizedSql p = ParameterizedSql.withNamedParameters(sql, Map.of("name", 1));
    assertThat(p.getSql()).doesNotContain(":name").doesNotContain(":id");
  }
}
