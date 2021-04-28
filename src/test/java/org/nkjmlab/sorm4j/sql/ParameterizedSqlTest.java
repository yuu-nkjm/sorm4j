package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;

class ParameterizedSqlTest {

  @Test
  void testLietral() {
    assertThat(SqlUtils.literal(List.of("a", "b"))).isEqualTo("'a', 'b'");
    assertThat(SqlUtils.literal(null)).isEqualTo("null");
    assertThat(SqlUtils.literal("?")).isEqualTo("?");
    assertThat(SqlUtils.literal("test")).isEqualTo("'test'");
  }

  @Test
  void testParseAsOrdered() {
    assertThat(ParameterizedSql.literal("hi, my name's tim.")).isEqualTo("'hi, my name''s tim.'");

    String sql = "select * from guest where id=?";
    Object[] params = {1};
    ParameterizedSql ps = ParameterizedSql.parse(sql, params);
    assertThat(ps.getSql()).isEqualTo(sql);
    assertThat(ps.getParameters()).isEqualTo(params);
  }

  @Test
  void testParseAsNamed() {
    String sql = "select * from guest where id=:id";
    Map<String, Object> map = Map.of("id", 1);
    ParameterizedSql ps = ParameterizedSql.parse(sql, map);

    String sql1 = "select * from guest where id=?";
    Object[] params = {1};
    assertThat(ps.getSql()).isEqualTo(sql1);
    assertThat(ps.getParameters()).isEqualTo(params);

    assertThat(ps.getBindedSql()).contains("select * from guest where id=1");
    assertThat(ps.toString()).contains("sql=[select * from guest where id=?], parameters=[1]");

  }

}
