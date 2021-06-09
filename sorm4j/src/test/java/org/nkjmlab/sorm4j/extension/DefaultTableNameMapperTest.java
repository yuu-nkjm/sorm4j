package org.nkjmlab.sorm4j.extension;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Guest;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.extension.impl.DefaultTableNameMapper;

class DefaultTableNameMapperTest {

  static Sorm sorm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }


  @Test
  void testToValidTableName() {
    sorm.accept(m -> {
      assertThat(
          new DefaultTableNameMapper().getTableName("guests", m.getJdbcConnection().getMetaData()))
              .isEqualTo(new TableName("GUESTS"));

    });
    try {
      sorm.accept(m -> {
        new DefaultTableNameMapper().getTableName("aaa", m.getJdbcConnection().getMetaData());
        failBecauseExceptionWasNotThrown(Exception.class);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  void testGetTableName() {
    sorm.accept(m -> {
      TableName name = new DefaultTableNameMapper().getTableName(Guest.class,
          m.getJdbcConnection().getMetaData());
      assertThat(name).isEqualTo(new TableName("GUESTS"));
    });
  }


}