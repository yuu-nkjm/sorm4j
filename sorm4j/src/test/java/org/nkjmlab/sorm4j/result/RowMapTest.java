package org.nkjmlab.sorm4j.result;

import static org.assertj.core.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.result.RowMapImpl;

class RowMapTest {

  @Test
  void testOf() {



    RowMap rm = new RowMapImpl();
    rm.put("key1", "value");
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
    rm.putAll(Map.of("c", "d"));
    assertThat(rm.size()).isEqualTo(1);
    rm.clear();
    assertThat(rm.size()).isEqualTo(0);

  }

}
