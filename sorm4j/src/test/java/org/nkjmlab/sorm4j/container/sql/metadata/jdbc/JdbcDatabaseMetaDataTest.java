package org.nkjmlab.sorm4j.container.sql.metadata.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcDatabaseMetaData.TableName;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class JdbcDatabaseMetaDataTest {
  @Test
  void test() {
    DefinedTable<Player> playersTable =
        SormTestUtils.createPlayersTable(SormTestUtils.createSormWithNewContext());
    String version = "2.";
    JdbcDatabaseMetaData md = playersTable.getOrm().getJdbcDatabaseMetaData();
    assertThat(md.getDatabaseProductName()).isEqualTo("H2");
    assertThat(md.getDatabaseProductVersion()).contains(version);
    assertThat(md.getDefaultTransactionIsolation())
        .isEqualTo(Connection.TRANSACTION_READ_COMMITTED);
    assertThat(md.getDriverName()).isEqualTo("H2 JDBC Driver");
    assertThat(md.getDriverVersion()).contains(version);
    assertThat(md.getJdbcTablesMetaData().get(TableName.of("PLAYERS")).getTableName())
        .isEqualTo("PLAYERS");
    assertThat(md.getMaxConnections()).isEqualTo(0);
    assertThat(md.getSearchStringEscape()).isEqualTo("\\");
    assertThat(md.getTableNames().get(0)).isEqualTo(TableName.of("PLAYERS"));
    assertThat(md.toString()).contains("H2");
    assertThat(md.getUrl()).contains("jdbc:h2:mem:test");
    assertThat(md.getUserName()).isEqualTo("");
    assertThat(md.getDriverVersion()).startsWith("2.");
    System.out.println(md);
  }

  @Test
  void testEquality() {
    TableName t1 = TableName.of("users");
    TableName t2 = TableName.of("users");
    TableName t3 = TableName.of("orders");

    assertThat(t1).isEqualTo(t2);
    assertThat(t1).isNotEqualTo(t3);
  }

  @Test
  void testHashCodeConsistency() {
    TableName t1 = TableName.of("users");
    TableName t2 = TableName.of("users");

    assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
  }

  @Test
  void testCompareTo() {
    TableName t1 = TableName.of("alpha");
    TableName t2 = TableName.of("beta");

    assertThat(t1.compareTo(t2)).isLessThan(0);
    assertThat(t2.compareTo(t1)).isGreaterThan(0);
    assertThat(t1.compareTo(t1)).isEqualTo(0);
  }

  @Test
  void testToString() {
    TableName t1 = TableName.of("customers");

    assertThat(t1.toString()).isEqualTo("CUSTOMERS");
  }

  @Test
  void testCanonicalStringCacheUsage() {
    TableName t1 = TableName.of("Test");
    TableName t2 = TableName.of("Test");

    assertThat(t1.getName()).isSameAs(t2.getName());
  }

  @Test
  void testNullTableName() {
    assertThatThrownBy(() -> TableName.of(null)).isInstanceOf(NullPointerException.class);
  }
}
