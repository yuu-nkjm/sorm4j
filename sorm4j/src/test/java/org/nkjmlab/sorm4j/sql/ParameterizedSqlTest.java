package org.nkjmlab.sorm4j.sql;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.util.sql.SelectSql.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.SormException;

class ParameterizedSqlTest {

  @Test
  void testLietral() {
    assertThat(literal(List.of("a", "b"))).isEqualTo("'a', 'b'");
    assertThat(literal(null)).isEqualTo("null");
    assertThat(literal("?")).isEqualTo("?");
    assertThat(literal("test")).isEqualTo("'test'");
    assertThat(literal("hi, my name's tim.")).isEqualTo("'hi, my name''s tim.'");
  }

  @Test
  void testParseAsOrdered() {
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

  @Test
  void testEmbeddedOrdered() {
    String sql = "select * from guest where id={?}";
    Object[] params = {1};
    assertThat(ParameterizedSql.embedParameter(sql, params))
        .contains("select * from guest where id=1");


  }

  @Test
  void testEmbeddedMap() {
    String sql = "select * from guest where name={:name} and id={:id}";
    Map<String, Object> params = Map.of("id", 1, "name", "'a'");

    assertThat(ParameterizedSql.embedParameter(sql, params))
        .contains("select * from guest where name='a' and id=1");
  }

  @Test
  void testEmbeddedOrderFail() {
    String sql = "select * from guest where id={?}";

    assertThatThrownBy(() -> ParameterizedSql.embedParameter(sql)).isInstanceOfSatisfying(
        SormException.class,
        e -> assertThat(e.getMessage()).contains("Could not embed all parameters"));
  }

  @Test
  void testEmbeddedMapFail() {

    String sql = "select * from guest where name={:name} and id={:id}";
    Map<String, Object> params = Map.of("id", 1, "name", "'a'");


    assertThatThrownBy(() -> ParameterizedSql.embedParameter(sql, Map.of("name1", 1)))
        .isInstanceOfSatisfying(SormException.class,
            e -> assertThat(e.getMessage()).contains("Could not embed all parameters"));

  }

}
