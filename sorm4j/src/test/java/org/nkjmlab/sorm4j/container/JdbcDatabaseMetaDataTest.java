package org.nkjmlab.sorm4j.container;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.container.sql.TableName;
import org.nkjmlab.sorm4j.container.sql.metadata.jdbc.JdbcDatabaseMetaData;
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
    assertThat(md.getUserName()).isEqualTo("SA");
    assertThat(md.getDriverVersion()).startsWith("2.");
    System.out.println(md);
  }
}
