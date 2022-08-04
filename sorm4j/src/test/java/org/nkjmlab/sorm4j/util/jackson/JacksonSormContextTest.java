package org.nkjmlab.sorm4j.util.jackson;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.datatype.JsonByte;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class JacksonSormContextTest {

  @Test
  void testBuilder() {
    Sorm sorm = Sorm.create(SormTestUtils.createNewDatabaseDataSource(),
        JacksonSormContext.builder(new ObjectMapper()).build());

    BasicH2Table<JacksonRecord> table = new BasicH2Table<>(sorm, JacksonRecord.class);
    table.createTableIfNotExists();
    table.insert(new JacksonRecord(new JsonByte("{\"name\":\"Alice\",\"age\":20}")));
    assertThat(table.selectAll().get(0).jsonCol.toString()).contains("Alice");
    assertThat(table.selectAll().get(0).jsonCol.toString()).contains(Integer.toString(20));


    assertThat(table.getOrm()
        .readFirst(SimpleOrmJsonContainer.class, "select * from jackson_records").toString())
            .contains("Alice");
  }

  @Test
  void testBuilder1() {
    Sorm sorm = Sorm.create(SormTestUtils.createNewDatabaseDataSource(),
        JacksonSormContext.builder(new ObjectMapper()).build());

    BasicH2Table<HasJsonColumn> table = new BasicH2Table<>(sorm, HasJsonColumn.class);
    assertThat(table.getTableDefinition().getCreateTableIfNotExistsStatement())
        .isEqualTo("create table if not exists HAS_JSON_COLUMNS(ID integer, JSON_COL json)");


    table.createTableIfNotExists();
    table.insert(new HasJsonColumn(1, new SimpleOrmJsonContainer("Alice", 20)));


    assertThat(table.selectAll().get(0).id).isEqualTo(1);
    assertThat(table.selectAll().get(0).jsonCol.name).isEqualTo("Alice");
    assertThat(table.selectAll().get(0).jsonCol.age).isEqualTo(20);

  }

  @OrmRecord
  public static class JacksonRecord {

    public final JsonByte jsonCol;


    public JacksonRecord(JsonByte jsonCol) {
      this.jsonCol = jsonCol;
    }


    @Override
    public String toString() {
      return "JacksonRecord [jsonCol=" + jsonCol + "]";
    }


  }

  @OrmJsonColumnContainer
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


  @SuppressWarnings("exports")
  @OrmRecord
  public static class HasJsonColumn {
    public int id;
    @OrmJsonColumnContainer
    public SimpleOrmJsonContainer jsonCol;

    public HasJsonColumn() {}

    public HasJsonColumn(int id, SimpleOrmJsonContainer jsonCol) {
      this.id = id;
      this.jsonCol = jsonCol;
    }

  }

}
