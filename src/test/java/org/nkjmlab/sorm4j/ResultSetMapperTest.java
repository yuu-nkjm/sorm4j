package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.sql.SqlStatement;

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
        rs -> conn.mapRowList(Player.class, rs)));
  }

  @Test
  void testMapRowsResultSet() {
    sorm.apply(conn -> conn.executeQuery(SqlStatement.from("select * from players"),
        rs -> conn.mapRowsToMapList(rs)));
  }

}
