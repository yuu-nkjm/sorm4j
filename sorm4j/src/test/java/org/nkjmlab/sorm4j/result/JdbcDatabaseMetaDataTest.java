package org.nkjmlab.sorm4j.result;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.table.TableWithSchema;

class JdbcDatabaseMetaDataTest {

  @Test
  void test() {
    TableWithSchema<Player> playersTable =
        SormTestUtils.createPlayersTable(SormTestUtils.createSormWithNewContext());
    String version = "2.1.210 (2022-01-17)";
    JdbcDatabaseMetaData md = playersTable.getSorm().getJdbcDatabaseMetaData();
    assertThat(md.getDatabaseProductName()).isEqualTo("H2");
    assertThat(md.getDatabaseProductVersion()).isEqualTo(version);
    assertThat(md.getDefaultTransactionIsolation())
        .isEqualTo(Connection.TRANSACTION_READ_COMMITTED);
    assertThat(md.getDriverName()).isEqualTo("H2 JDBC Driver");
    assertThat(md.getDriverVersion()).isEqualTo(version);
    assertThat(md.getJdbcTablesMetaData().get("PLAYERS").get("TABLE_NAME")).isEqualTo("PLAYERS");
    assertThat(md.getMaxConnections()).isEqualTo(0);
    assertThat(md.getSearchStringEscape()).isEqualTo("\\");
    assertThat(md.getTableNames().get(0)).isEqualTo("PLAYERS");
    assertThat(md.toString()).contains("H2");
    assertThat(md.getUrl()).contains("jdbc:h2:mem:test");
    assertThat(md.getUserName()).isEqualTo("SA");
    assertThat(md.getJdbcDriverVersion()).isEqualTo("4.2");
  }

}
