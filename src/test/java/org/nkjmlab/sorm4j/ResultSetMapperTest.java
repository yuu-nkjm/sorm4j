package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.tool.SormTestUtils.*;
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
    sorm.apply(conn -> conn.executeQuery(SqlStatement.from("select * from players"),
        (rs, rowNum) -> conn.mapRow(Player.class, rs)));
  }

  @Test
  void testMapRowResultSet() {
    sorm.apply(conn -> conn.executeQuery(SqlStatement.from("select * from players"),
        (rs, rowNum) -> conn.mapRowToMap(rs)));
  }

  @Test
  void testMapRowsClassOfTResultSet() {
    sorm.apply(conn -> conn.executeQuery(SqlStatement.from("select * from players"),
        rs -> conn.mapRows(Player.class, rs)));
  }

  @Test
  void testMapRowsResultSet() {
    sorm.apply(conn -> conn.executeQuery(SqlStatement.from("select * from players"),
        rs -> conn.mapRowsToMapList(rs)));
  }

}
