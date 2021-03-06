package org.nkjmlab.sorm4j.extension;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.tool.Guest;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class DefaultTableNameMapperTest {

  static Sorm sorm;

  @BeforeAll
  static void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
  }


  @Test
  void testToValidTableName() {
    sorm.accept(Guest.class, m -> {
      assertThat(new DefaultTableNameMapper().getTableName("guests",
          m.getJdbcConnection().getMetaData())).isEqualTo(new TableName("GUESTS"));

    });
    try {
      sorm.accept(Guest.class, m -> {
        new DefaultTableNameMapper().getTableName("aaa", m.getJdbcConnection().getMetaData());
        failBecauseExceptionWasNotThrown(Exception.class);
      });
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("does not match any existing table");
    }
  }

  @Test
  void testGetTableName() {
    sorm.accept(Guest.class, m -> {
      TableName name = new DefaultTableNameMapper().getTableName(Guest.class,
          m.getJdbcConnection().getMetaData());
      assertThat(name).isEqualTo(new TableName("GUESTS"));
    });
  }


}
