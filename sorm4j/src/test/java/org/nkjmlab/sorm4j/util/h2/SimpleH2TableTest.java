package org.nkjmlab.sorm4j.util.h2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.test.common.Player;

class SimpleH2TableTest {

  @Test
  void testSimpleH2TableWithValueType() {
    Class<?> valueType =Player.class;
    Sorm mockSorm = mock(Sorm.class);

    SimpleH2Table<?> table = new SimpleH2Table<>(mockSorm, valueType);

    assertNotNull(table);
  }

  @Test
  void testSimpleH2TableWithValueTypeAndTableName() {
    Class<?> valueType = Player.class;
    String tableName = "test_table";
    Sorm mockSorm = mock(Sorm.class);

    SimpleH2Table<?> table = new SimpleH2Table<>(mockSorm, valueType, tableName);

    assertNotNull(table);
  }
}
