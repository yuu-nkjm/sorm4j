package org.nkjmlab.sorm4j.util.jackson;

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
    table.insert(new JacksonRecord(new JsonByte("{\"name\":\"hoge\",\"age\":30}")));
    System.out.println(table.selectAll());

    System.out.println(
        table.getOrm().readFirst(SimpleOrmJsonContainer.class, "select * from jackson_records"));
  }

  @OrmRecord
  public static class JacksonRecord {

    public final JsonByte jsonByte;


    public JacksonRecord(JsonByte jsonByte) {
      this.jsonByte = jsonByte;
    }


    @Override
    public String toString() {
      return "JacksonRecord [jsonByte=" + jsonByte + "]";
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
}
