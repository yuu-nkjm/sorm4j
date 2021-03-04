package org.nkjmlab.sorm4j.sqlstatement;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class SqlWithOrderedParametersTest {

  @Test
  void testToSqlStatementStringObjectArray() {
    assertThat(
        SqlWithOrderedParameters.toSqlStatement("select * from a where in(?)", List.of("alice", 1)).toString())
            .contains("[select * from a where in(?)] with [['alice', 1]]");
  }

}
