package org.nkjmlab.sorm4j.internal.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.result.BasicRowMap;
import org.nkjmlab.sorm4j.result.RowMap;

class BasicRowMapTest {

  private static final RowMap map0 = new BasicRowMap(Map.of("k1", "v1"));
  private static final RowMap map1 = new BasicRowMap(Map.of("k1", "v1"));
  private static final RowMap map2 = new BasicRowMap(Map.of("k2", "v2"));
  private static final RowMap map3 =
      new BasicRowMap(
          Map.of(
              "s",
              "vs",
              "i",
              1,
              "l",
              1,
              "f",
              1.0f,
              "d",
              1.0d,
              "ld",
              LocalDate.of(2022, 3, 24),
              "lt",
              LocalTime.of(1, 2, 3),
              "ltd",
              LocalDateTime.of(2022, 3, 24, 1, 2, 3)));

  @Test
  void convertRecord() {
    RowMap map = RowMap.of("id", 1, "name", "Alice", "flag", true, "count", 10);
    map.put("col", null);
    {
      TestRecord rec = map.toRecord(TestRecord.class);
      RowMap ret = RowMap.fromRecord(rec);
      assertThat(ret).isEqualTo(map);
    }
    {
      TestRecord2 rec = map.toRecord(TestRecord2.class);
      RowMap ret = RowMap.fromRecord(rec);
      assertThat(ret.size()).isEqualTo(3);
    }
  }

  @Test
  void convertRecord1() {
    assertThatThrownBy(
            () -> {
              RowMap map = RowMap.of("flag", true);
              TestRecord rec = map.toRecord(TestRecord.class);
              RowMap ret = RowMap.fromRecord(rec);
              assertThat(ret.size()).isEqualTo(rec.getClass().getRecordComponents().length);
            })
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void convertRecord2() {
    RowMap map = RowMap.of("flag", true);
    TestRecord2 rec = map.toRecord(TestRecord2.class);
    RowMap ret = RowMap.fromRecord(rec);
    assertThat(ret.size()).isEqualTo(rec.getClass().getRecordComponents().length);
  }

  public record TestRecord(Integer id, String name, Boolean flag, int count, String col) {}

  public record TestRecord2(Boolean flag, Integer count, String col) {}

  @Test
  void testHashCode() {
    assertThat(map1.hashCode()).isEqualTo(map0.hashCode());
    assertThat(map1.equals(map0)).isTrue();

    assertThat(map1.hashCode()).isNotEqualTo(map2.hashCode());
    assertThat(map1.equals(map2)).isFalse();
  }

  @Test
  void testToString() {
    assertThat(map0.toString()).isEqualTo("{K1=v1}");
  }

  @Test
  void testGetString() {
    assertThat(map3.getString("s")).isEqualTo("vs");
  }

  @Test
  void testGetInteger() {
    assertThat(map3.getInteger("i")).isEqualTo(1);
  }

  @Test
  void testGetLong() {
    assertThat(map3.getLong("l")).isEqualTo(1L);
  }

  @Test
  void testGetFloat() {
    assertThat(map3.getFloat("f")).isEqualTo(1.0f);
  }

  @Test
  void testGetDouble() {
    assertThat(map3.getDouble("d")).isEqualTo(1.0d);
  }

  @Test
  void testGetLocalDate() {
    assertThat(map3.getLocalDate("ld")).isEqualTo(LocalDate.of(2022, 3, 24));
  }

  @Test
  void testGetLocalTime() {
    assertThat(map3.getLocalTime("lt")).isEqualTo(LocalTime.of(1, 2, 3));
  }

  @Test
  void testGetLocalDateTime() {
    assertThat(map3.getLocalDateTime("ltd")).isEqualTo(LocalDateTime.of(2022, 3, 24, 1, 2, 3));
  }
}
