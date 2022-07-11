package org.nkjmlab.sorm4j.util.jackson;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;
import org.nkjmlab.sorm4j.util.json.JsonByte;
import org.nkjmlab.sorm4j.util.json.OrmJsonColumnContainer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class JacksonSormContextTest {

  @Test
  void testBuilder() {
    Sorm sorm = Sorm.create(SormTestUtils.createSormWithNewContext().getDataSource(),
        JacksonSormContext.builder(new ObjectMapper()).build());

    BasicH2Table<JacksonTest> table = new BasicH2Table<>(sorm, JacksonTest.class);
    table.createTableIfNotExists();
    table.insert(new JacksonTest(new JsonByte("{\"name\":\"hoge\",\"age\":30}")));
    System.out.println(table.selectAll());

    System.out.println(
        table.getOrm().readFirst(OrmJsonContainerTest.class, "select * from jackson_tests"));
  }

  @OrmRecord
  public static class JacksonTest {

    public final JsonByte jsonByte;


    public JacksonTest(JsonByte jsonByte) {
      this.jsonByte = jsonByte;
    }


    @Override
    public String toString() {
      return "JacksonTest [jsonByte=" + jsonByte + "]";
    }


  }

  @OrmJsonColumnContainer
  public static class OrmJsonContainerTest {
    public final String name;
    public final int age;

    @JsonCreator
    public OrmJsonContainerTest(@JsonProperty("name") String name, @JsonProperty("age") int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      return "OrmJsonContainerTest [name=" + name + ", age=" + age + "]";
    }

  }
}
