package org.nkjmlab.sorm4j.result;

import static org.assertj.core.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RowMapTest {

  @Test
  void testOf() {



    RowMap rm = RowMap.create();
    rm.put("key1", "value");
    RowMap rm1 = RowMap.create();
    rm1.put("key1", "value");

    assertThat(rm.hashCode() == rm1.hashCode()).isTrue();

    assertThat(rm.equals(rm1)).isTrue();


    assertThat(rm.containsKey("key1")).isTrue();
    assertThat(rm.containsValue("value")).isTrue();
    assertThat(rm.get("key1")).isEqualTo("value");
    assertThat(rm.keySet()).containsExactlyInAnyOrder("KEY1");
    assertThat(rm.values()).containsExactlyInAnyOrder("value");
    assertThat(rm.entrySet().toString()).isEqualTo("[KEY1=value]");


    assertThat(rm.size()).isEqualTo(1);
    assertThat(rm.isEmpty()).isFalse();

    rm.remove("key1");
    assertThat(rm.size()).isEqualTo(0);
    rm.putAll(Map.of("a", "b", "c", "d", "e", "1"));
    assertThat(rm.size()).isEqualTo(3);

    assertThat(rm.getString("C")).isEqualTo("d");
    assertThat(rm.getObject("C")).isEqualTo("d");
    assertThat(rm.getStringList("A", "c", "e")).isEqualTo(List.of("b", "d", "1"));
    assertThat(rm.getObjectList("A", "c", "e")).isEqualTo(List.of("b", "d", "1"));

    assertThat(rm.getInteger("e")).isEqualTo(1);
    assertThat(rm.getLong("e")).isEqualTo(1L);
    assertThat(rm.getDouble("e")).isEqualTo(1.0d);
    assertThat(rm.getFloat("e")).isEqualTo(1.0f);

    rm.clear();
    assertThat(rm.size()).isEqualTo(0);

    String[] arr = {"a", "b"};
    rm.put("arr", arr);
    assertThat(rm.get("arr").equals(arr)).isTrue();


    LocalDate ld = LocalDate.parse("2022-11-16");
    LocalTime lt = LocalTime.parse("12:01:02");
    LocalDateTime ldt = LocalDateTime.parse("2022-11-16T12:01:02");
    rm.put("local_date", ld);
    rm.put("local_time", lt);
    rm.put("local_datetime", ldt);

    assertThat(rm.getLocalDateTime("local_datetime")).isEqualTo(ldt);
    assertThat(rm.getLocalTime("local_time")).isEqualTo(lt);
    assertThat(rm.getLocalDate("local_date")).isEqualTo(ld);


    byte[] by = "aaa".getBytes();
    rm.put("by", by);
    assertThat(rm.getString("by").equals("aaa")).isTrue();
  }

}
