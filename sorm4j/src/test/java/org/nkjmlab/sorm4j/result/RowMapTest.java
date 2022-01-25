package org.nkjmlab.sorm4j.result;

import static org.assertj.core.api.Assertions.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.result.RowMapImpl;

class RowMapTest {

  @Test
  void testOf() {
    LinkedHashMap<String, String> lm = new LinkedHashMap<>();
    lm.put("a", "b");

    RowMap rm = new RowMapImpl();
    rm.put("a", "b");
    assertThat(rm.containsKey("a")).isTrue();
    assertThat(rm.containsValue("b")).isTrue();
    assertThat(rm.get("a")).isEqualTo("b");
    assertThat(rm.keySet()).containsExactlyInAnyOrder("a");
    assertThat(rm.values()).containsExactlyInAnyOrder("b");
    assertThat(rm.entrySet().toString()).isEqualTo("[a=b]");
    assertThat(rm.equals(lm)).isTrue();


    assertThat(rm.size()).isEqualTo(1);
    assertThat(rm.isEmpty()).isFalse();
    assertThat(rm.hashCode()).isEqualTo(lm.hashCode());

    rm.remove("a");
    assertThat(rm.size()).isEqualTo(0);
    rm.putAll(Map.of("c", "d"));
    assertThat(rm.size()).isEqualTo(1);
    rm.clear();
    assertThat(rm.size()).isEqualTo(0);



  }

}
