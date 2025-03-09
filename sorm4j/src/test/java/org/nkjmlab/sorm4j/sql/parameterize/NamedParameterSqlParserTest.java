package org.nkjmlab.sorm4j.sql.parameterize;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.parameterize.NamedParameterSqlFactory;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;

class NamedParameterSqlParserTest {

  @Test
  void testBindAll() {
    NamedParameterSqlFactory parser =
        NamedParameterSqlFactory.of("SELECT * FROM table WHERE id = :id AND name = :name");
    Map<String, Object> params = new HashMap<>();
    params.put("id", 1);
    params.put("name", "John");

    ParameterizedSql result = parser.bind(params).create();

    assertEquals("SELECT * FROM table WHERE id = ? AND name = ?", result.getSql());
    assertArrayEquals(new Object[] {1, "John"}, result.getParameters());
  }

  @Test
  void testBind() {
    NamedParameterSqlFactory parser =
        NamedParameterSqlFactory.of("SELECT * FROM table WHERE id = :id");
    ParameterizedSql result = parser.bind("id", 1).create();

    assertEquals("SELECT * FROM table WHERE id = ?", result.getSql());
    assertArrayEquals(new Object[] {1}, result.getParameters());
  }

  @Test
  void testBindBean() {
    TestBean bean = new TestBean(1, "John");

    ParameterizedSql result =
        NamedParameterSqlFactory.of("SELECT * FROM table WHERE id = :id AND name = :name")
            .bind(bean)
            .create();

    assertEquals("SELECT * FROM table WHERE id = ? AND name = ?", result.getSql());
    assertArrayEquals(new Object[] {1, "John"}, result.getParameters());
  }

  @Test
  void testParseStaticMethod() {
    Map<String, Object> params = new HashMap<>();
    params.put("id", 1);
    params.put("name", "John");

    ParameterizedSql result =
        NamedParameterSqlFactory.create(
            "SELECT * FROM table WHERE id = :id AND name = :name", params);

    assertEquals("SELECT * FROM table WHERE id = ? AND name = ?", result.getSql());
    assertArrayEquals(new Object[] {1, "John"}, result.getParameters());
  }

  public static class TestBean {
    private final int id;
    private final String name;

    public TestBean(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }
}
