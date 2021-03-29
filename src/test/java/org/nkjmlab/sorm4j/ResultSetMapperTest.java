package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.SqlStatement;
import org.nkjmlab.sorm4j.tool.Player;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class ResultSetMapperTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    sorm.accept(conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
  }

  @Test
  void testMapRowClassOfTResultSet() {
    List<Player> ret =
        sorm.apply(conn -> conn.executeQuery(SqlStatement.of("select * from players"),
            (rs, rowNum) -> conn.mapRow(Player.class, rs)));
  }

  @Test
  void testMapRowResultSet() {
    List<Map<String, Object>> ret = sorm.apply(conn -> conn
        .executeQuery(SqlStatement.of("select * from players"), (rs, rowNum) -> conn.mapRowToMap(rs)));
  }

  @Test
  void testMapRowsClassOfTResultSet() {
    List<Player> ret =
        sorm.apply(conn -> conn.executeQuery(SqlStatement.of("select * from players"),
            rs -> conn.mapRows(Player.class, rs)));
  }

  @Test
  void testMapRowsResultSet() {
    List<Map<String, Object>> ret = sorm.apply(conn -> conn
        .executeQuery(SqlStatement.of("select * from players"), rs -> conn.mapRowsToMapList(rs)));
  }

}
