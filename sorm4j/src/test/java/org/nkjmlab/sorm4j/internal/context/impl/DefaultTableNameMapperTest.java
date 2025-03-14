package org.nkjmlab.sorm4j.internal.context.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.sql.TableName;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DefaultTableNameMapperTest {

  static Sorm sorm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
  }

  @Test
  void testToValidTableName() {
    sorm.acceptHandler(
        m -> {
          assertThat(
                  new DefaultTableNameMapper()
                      .getTableName("guests", m.getJdbcConnection().getMetaData()))
              .isEqualTo("GUESTS");
        });
    try {
      sorm.acceptHandler(
          m -> {
            new DefaultTableNameMapper().getTableName("aaa", m.getJdbcConnection().getMetaData());
            failBecauseExceptionWasNotThrown(Exception.class);
          });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  void testGetTableName() {
    sorm.acceptHandler(
        m -> {
          String name =
              new DefaultTableNameMapper()
                  .getTableName(Guest.class, m.getJdbcConnection().getMetaData());
          assertThat(name).isEqualTo(TableName.of("GUESTS").getName());
        });
  }
}
