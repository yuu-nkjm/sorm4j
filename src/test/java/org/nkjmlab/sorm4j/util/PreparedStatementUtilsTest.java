package org.nkjmlab.sorm4j.util;

import org.junit.jupiter.api.Test;

class PreparedStatementUtilsTest {

  @Test
  void testGetPreparedStatementConnectionString() {
    try {
      PreparedStatementUtils.getPreparedStatement(null, "a");
    } catch (Exception e) {
    }
  }

  @Test
  void testGetPreparedStatementConnectionStringStringArray() {
    try {
      PreparedStatementUtils.getPreparedStatement(null, "a", new String[] {});
    } catch (Exception e) {
    }
  }

}
