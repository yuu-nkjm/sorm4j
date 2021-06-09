package org.nkjmlab.sorm4j.internal.mapping;

import static org.assertj.core.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.internal.SormOptionsImpl;

class OrmOptionsImplTest {

  SormOptions opts = new SormOptionsImpl(Map.of("db", "h2"));

  @Test
  void testGet() {
    assertThat(opts.get("db")).isEqualTo("h2");
  }

  @Test
  void testGetOrDefault() {
    assertThat(opts.getOrDefault("dbname", 1)).isEqualTo(1);
  }

}
