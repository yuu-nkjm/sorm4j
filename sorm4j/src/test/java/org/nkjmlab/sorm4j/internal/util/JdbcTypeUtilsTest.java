package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.*;
import java.sql.JDBCType;
import org.junit.jupiter.api.Test;

class JdbcTypeUtilsTest {

  @Test
  void test() {
    assertThat(JdbcTypeUtils.convert(new int[] {4}).get(0)).isEqualTo(JDBCType.INTEGER);
  }

}
