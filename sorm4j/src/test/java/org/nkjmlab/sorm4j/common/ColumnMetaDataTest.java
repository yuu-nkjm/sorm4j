package org.nkjmlab.sorm4j.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.ColumnMetaData;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class ColumnMetaDataTest {

  @Test
  void testCompareTo() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    List<ColumnMetaData> t1 =
        ((TableMetaData) sorm.getOrmTableMetaData(Guest.class)).getColumnsMetaData();
    ColumnMetaData c1 = t1.get(0);
    ColumnMetaData c2 = t1.get(0);
    ColumnMetaData c3 = t1.get(1);
    List<ColumnMetaData> t2 =
        ((TableMetaData) sorm.getOrmTableMetaData(Player.class)).getColumnsMetaData();
    ColumnMetaData c4 = t2.get(0);

    // c1 and c2 are same object
    assertThat(c1.hashCode() == c2.hashCode()).isTrue();
    assertThat(c1.equals(c2)).isTrue();
    assertThat(c1.compareTo(c2)).isEqualTo(0);

    // c1 and c4 are diffrent
    assertThat(c1.equals(c4)).isFalse();

    // c1 and c3 are different
    assertThat(c1.equals(c3)).isFalse();
    assertThat(c1.compareTo(c3)).isLessThan(0);
  }
}
