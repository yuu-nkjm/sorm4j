package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.result.RowMap;

class DefaultSqlParametersSetterTest {

  @Test
  void testSetParameters() {
    SORM.executeUpdate(
        "CREATE TABLE TA (id int auto_increment primary key, arry " + "INTEGER" + " ARRAY[10])");

    SORM.readFirst(RowMap.class, "select * from TA where arry=?", new boolean[] {true, false});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?",
        (Object) new Boolean[] {true, false});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?", new double[] {0.1d});
    SORM.readFirst(RowMap.class, "select * from TA where arry=?", (Object) new Double[] {0.1d});


  }

}
