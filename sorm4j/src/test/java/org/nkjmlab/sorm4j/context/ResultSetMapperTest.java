package org.nkjmlab.sorm4j.context;

import static org.assertj.core.api.Assertions.*;
import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class ResultSetMapperTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    sorm.acceptHandler(conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testMapRowsClassOfTResultSet() {
    sorm.applyHandler(
        conn ->
            conn.executeQuery(
                ParameterizedSql.of("select * from players"),
                conn.getResultSetTraverser(Player.class)));
  }

  @Test
  void testMapRowsResultSet() {
    assertThat(
            sorm.applyHandler(
                    conn ->
                        conn.executeQuery(
                            ParameterizedSql.of("select * from players"),
                            conn.getResultSetTraverser(RowMap.class)))
                .size())
        .isEqualTo(2);
  }
}
