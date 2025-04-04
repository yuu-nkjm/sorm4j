package org.nkjmlab.sorm4j.extension.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.container.JsonByte;
import org.nkjmlab.sorm4j.extension.datatype.jackson.JacksonSupport;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJacksonColumn;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTable;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecordCompatibleConstructor;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class JacksonSormContextTest {

  public record SimpleJackson(Person person) {}

  @OrmJacksonColumn
  public record Person(String name, int age) {}

  @Test
  void testJackson() {
    Sorm sorm =
        Sorm.create(
            SormTestUtils.createNewDatabaseDataSource(),
            new JacksonSupport(new ObjectMapper()).addSupport(SormContext.builder()).build());
    TableDefinition def = TableDefinition.builder(SimpleJackson.class).build();
    def.createTableIfNotExists(sorm);
    sorm.insert(new SimpleJackson(new Person("taro", 33)));
    assertEquals(
        33, sorm.readFirst(SimpleJackson.class, "SELECT * FROM SIMPLE_JACKSONS").person().age());
  }

  @Test
  void testBuilder() {
    Sorm sorm =
        Sorm.create(
            SormTestUtils.createNewDatabaseDataSource(),
            new JacksonSupport(new ObjectMapper()).addSupport(SormContext.builder()).build());

    H2DefinedTable<JacksonRecord> table = H2DefinedTable.of(sorm, JacksonRecord.class);
    table.createTableIfNotExists();
    table.insert(new JacksonRecord(JsonByte.of("{\"name\":\"Alice\",\"age\":20}")));
    assertThat(table.selectAll().get(0).jsonCol.toString()).contains("Alice");
    assertThat(table.selectAll().get(0).jsonCol.toString()).contains(Integer.toString(20));

    assertThat(
            table
                .getOrm()
                .readFirst(SimpleOrmJsonContainer.class, "select * from jackson_records")
                .toString())
        .contains("Alice");
  }

  @Test
  void testBuilder1() {
    Sorm sorm =
        Sorm.create(
            SormTestUtils.createNewDatabaseDataSource(),
            new JacksonSupport(new ObjectMapper()).addSupport(SormContext.builder()).build());

    H2DefinedTable<HasJsonColumn> table = H2DefinedTable.of(sorm, HasJsonColumn.class);
    assertThat(table.getTableDefinition().getCreateTableIfNotExistsStatement())
        .isEqualTo("create table if not exists HAS_JSON_COLUMNS(ID integer, JSON_COL json)");

    table.createTableIfNotExists();
    table.insert(new HasJsonColumn(1, new SimpleOrmJsonContainer("Alice", 20)));

    assertThat(table.selectAll().get(0).id).isEqualTo(1);
    assertThat(table.selectAll().get(0).jsonCol.name).isEqualTo("Alice");
    assertThat(table.selectAll().get(0).jsonCol.age).isEqualTo(20);
  }

  public static class JacksonRecord {

    public final JsonByte jsonCol;

    @OrmRecordCompatibleConstructor
    public JacksonRecord(JsonByte jsonCol) {
      this.jsonCol = jsonCol;
    }

    @Override
    public String toString() {
      return "JacksonRecord [jsonCol=" + jsonCol + "]";
    }
  }

  @OrmJacksonColumn
  public static class SimpleOrmJsonContainer {
    public final String name;
    public final int age;

    @JsonCreator
    public SimpleOrmJsonContainer(@JsonProperty("name") String name, @JsonProperty("age") int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      return "SimpleOrmJsonContainer [name=" + name + ", age=" + age + "]";
    }
  }

  // @SuppressWarnings("exports")

  public static class HasJsonColumn {
    public int id;
    public SimpleOrmJsonContainer jsonCol;

    public HasJsonColumn() {}

    public HasJsonColumn(int id, SimpleOrmJsonContainer jsonCol) {
      this.id = id;
      this.jsonCol = jsonCol;
    }
  }
}
