package org.nkjmlab.sorm4j.internal.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
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
  void testToString1() {
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
  void testGetLocalDate1() {
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

  @Test
  void testBasicRowMap() {
    BasicRowMap map = new BasicRowMap();
    assertTrue(map.isEmpty());

    map.put("key1", "value1");
    assertFalse(map.isEmpty());

    assertEquals("value1", map.get("key1"));
    assertEquals(1, map.size());

    assertTrue(map.containsKey("key1"));
    assertTrue(map.containsValue("value1"));

    map.remove("key1");
    assertTrue(map.isEmpty());
  }

  @Test
  void testBasicRowMapWithInitialCapacityAndLoadFactor() {
    BasicRowMap map = new BasicRowMap(10, 0.75f);
    map.put("key1", "value1");
    assertEquals("value1", map.get("key1"));
  }

  @Test
  void testBasicRowMapWithMap() {
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    sourceMap.put("key1", "value1");
    sourceMap.put("key2", 2);

    BasicRowMap map = new BasicRowMap(sourceMap);
    assertEquals("value1", map.get("key1"));
    assertEquals(2, map.get("key2"));
  }

  @Test
  void testPutAll() {
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    sourceMap.put("key1", "value1");
    sourceMap.put("key2", 2);

    BasicRowMap map = new BasicRowMap();
    map.putAll(sourceMap);

    assertEquals("value1", map.get("key1"));
    assertEquals(2, map.get("key2"));
  }

  @Test
  void testEqualsAndHashCode() {
    BasicRowMap map1 = new BasicRowMap();
    map1.put("key1", "value1");

    BasicRowMap map2 = new BasicRowMap();
    map2.put("key1", "value1");

    assertEquals(map1, map2);
    assertEquals(map1.hashCode(), map2.hashCode());
  }

  @Test
  void testToString() {
    BasicRowMap map = new BasicRowMap();
    map.put("key1", "value1");

    String expected = "{KEY1=value1}";
    assertEquals(expected, map.toString());
  }

  @Test
  void testGetNumber() {
    BasicRowMap map = new BasicRowMap();
    map.put("int", 123);
    map.put("long", 123L);
    map.put("float", 123.0f);
    map.put("double", 123.0d);

    assertEquals(Integer.valueOf(123), map.getInteger("int"));
    assertEquals(Long.valueOf(123), map.getLong("long"));
    assertEquals(Float.valueOf(123.0f), map.getFloat("float"));
    assertEquals(Double.valueOf(123.0d), map.getDouble("double"));
  }

  @Test
  void testGetLocalDate() {
    BasicRowMap map = new BasicRowMap();
    LocalDate date = LocalDate.now();
    map.put("date", java.sql.Date.valueOf(date));

    assertEquals(date, map.getLocalDate("date"));
  }

  @Test
  void testGetLocalTime1() {
    BasicRowMap map = new BasicRowMap();
    LocalTime time = LocalTime.now();
    map.put("time", java.sql.Time.valueOf(time));

    assertEquals(java.sql.Time.valueOf(time).toLocalTime(), map.getLocalTime("time"));
  }

  @Test
  void testGetLocalDateTime1() {
    BasicRowMap map = new BasicRowMap();
    LocalDateTime dateTime = LocalDateTime.now();
    map.put("dateTime", java.sql.Timestamp.valueOf(dateTime));

    assertEquals(java.sql.Timestamp.valueOf(dateTime).toLocalDateTime(), map.getLocalDateTime("dateTime"));
  }

  @Test
  void testGetArray() {
    BasicRowMap map = new BasicRowMap();
    Integer[] intArray = {1, 2, 3};
    map.put("intArray", intArray);

    assertArrayEquals(intArray, map.getArray("intArray", Integer.class));
  }

  @Test
  void testGetObjectList() {
    BasicRowMap map = new BasicRowMap();
    map.put("key1", "value1");
    map.put("key2", 2);

    List<Object> values = map.getObjectList("key1", "key2");
    assertEquals(Arrays.asList("value1", 2), values);
  }

  @Test
  void testGetStringList() {
    BasicRowMap map = new BasicRowMap();
    map.put("key1", "value1");
    map.put("key2", "value2");

    List<String> values = map.getStringList("key1", "key2");
    assertEquals(Arrays.asList("value1", "value2"), values);
  }
}
