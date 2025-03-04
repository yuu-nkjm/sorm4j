package org.nkjmlab.sorm4j.util.sql.param;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.ParameterizedSql;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

class ParameterizedSqlParserTest {

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
    Object[] params = {1};
    ParameterizedSql ps = ParameterizedSqlParser.parse(sql, params);
    assertThat(ps.getSql()).isEqualTo(sql);
    assertThat(ps.getParameters()).isEqualTo(params);
  }

  @Test
  void testParseAsNamed() {
    String sql = "select * from guest where id=:id";
    Map<String, Object> map = Map.of("id", 1);
    ParameterizedSql ps = ParameterizedSqlParser.parse(sql, map);

    String sql1 = "select * from guest where id=?";
    Object[] params = {1};
    assertThat(ps.getSql()).isEqualTo(sql1);
    assertThat(ps.getParameters()).isEqualTo(params);

    assertThat(ps.getParameterBindedSql()).contains("select * from guest where id=1");
    assertThat(ps.toString()).contains("sql=[select * from guest where id=?], parameters=[1]");
  }

  @Test
  void testEmbeddedOrdered() {
    String sql = "select * from guest where id={?}";
    Object[] params = {1};
    assertThat(ParameterizedSqlParser.embedParameter(sql, params))
        .contains("select * from guest where id=1");
  }

  @Test
  void testEmbeddedMap() {
    String sql = "select * from guest where name={:name} and id={:id}";
    Map<String, Object> params = Map.of("id", 1, "name", "'a'");

    assertThat(ParameterizedSqlParser.embedParameter(sql, params))
        .contains("select * from guest where name='a' and id=1");
  }

  @Test
  void testEmbeddedOrderFail() {
    String sql = "select * from guest where id={?}";

    assertThatThrownBy(() -> ParameterizedSqlParser.embedParameter(sql))
        .isInstanceOfSatisfying(
            SormException.class,
            e -> assertThat(e.getMessage()).contains("Could not embed all parameters"));
  }

  @Test
  void testEmbeddedMapFail() {

    String sql = "select * from guest where name={:name} and id={:id}";

    assertThatThrownBy(() -> ParameterizedSqlParser.embedParameter(sql, Map.of("name1", 1)))
        .isInstanceOfSatisfying(
            SormException.class,
            e -> assertThat(e.getMessage()).contains("Could not embed all parameters"));
  }
}
