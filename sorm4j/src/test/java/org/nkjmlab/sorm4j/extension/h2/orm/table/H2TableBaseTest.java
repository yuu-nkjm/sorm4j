package org.nkjmlab.sorm4j.extension.h2.orm.table;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.h2.orm.H2SormFactory;

class H2TableBaseTest {
  public static class TestEntity {
    public int id;
    public String name;

    public TestEntity(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  private Sorm sorm;
  private H2TableBase<TestEntity> table;

  @BeforeEach
  void setUp() {
    sorm = H2SormFactory.createTemporalInMemory();
    table = new H2TableBase.H2SimpleTable<>(sorm, TestEntity.class, "TEST_ENTITY");

    try (Connection conn = sorm.openJdbcConnection()) {
      conn.createStatement()
          .execute("CREATE TABLE TEST_ENTITY (id INT PRIMARY KEY, name VARCHAR(255))");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  void tearDown() {
    try (Connection conn = sorm.openJdbcConnection()) {
      conn.createStatement().execute("DROP TABLE TEST_ENTITY");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testInsertAndSelect() {
    table.insert(new TestEntity(1, "Alice"));
    table.insert(new TestEntity(2, "Bob"));

    List<TestEntity> results = table.selectAll();
    assertThat(results).hasSize(2);
    assertThat(results.get(0).name).isEqualTo("Alice");
    assertThat(results.get(1).name).isEqualTo("Bob");
  }

  @Test
  void testUpdate() {
    table.insert(new TestEntity(1, "Alice"));
    table.update(new TestEntity(1, "Alice Updated"));

    TestEntity result = table.selectByPrimaryKey(1);
    assertThat(result).isNotNull();
    assertThat(result.name).isEqualTo("Alice Updated");
  }

  @Test
  void testDelete() {
    table.insert(new TestEntity(1, "Alice"));
    table.insert(new TestEntity(2, "Bob"));

    table.delete(new TestEntity(1, "Alice"));
    List<TestEntity> results = table.selectAll();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).id).isEqualTo(2);
  }

  @Test
  void testSelectByPrimaryKey() {
    table.insert(new TestEntity(1, "Alice"));

    TestEntity result = table.selectByPrimaryKey(1);
    assertThat(result).isNotNull();
    assertThat(result.id).isEqualTo(1);
    assertThat(result.name).isEqualTo("Alice");
  }

  @Test
  void testSelectByCondition() {
    table.insert(new TestEntity(1, "Alice"));
    table.insert(new TestEntity(2, "Bob"));
    table.insert(new TestEntity(3, "Charlie"));

    List<TestEntity> results = table.readList("select * from TEST_ENTITY WHERE name LIKE ?", "%o%");
    assertThat(results).hasSize(1);
    assertThat(results.get(0).name).isEqualTo("Bob");
  }
}
