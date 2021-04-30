package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.common.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.Player;
import org.nkjmlab.sorm4j.common.SormTestUtils;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

class ResultSetMapperTest {

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    sorm = SormTestUtils.createSormAndDropAndCreateTableAll();
    sorm.accept(conn -> conn.insert(PLAYER_ALICE, PLAYER_BOB));
  }


  @Test
  void testMapRowsClassOfTResultSet() {
    sorm.apply(conn -> conn.executeQuery(ParameterizedSql.of("select * from players"),
        rs -> conn.mapRowList(Player.class, rs)));
  }

  @Test
  void testMapRowsResultSet() {
    sorm.apply(conn -> conn.executeQuery(ParameterizedSql.of("select * from players"),
        rs -> conn.mapRowsToMapList(rs)));
  }

}
