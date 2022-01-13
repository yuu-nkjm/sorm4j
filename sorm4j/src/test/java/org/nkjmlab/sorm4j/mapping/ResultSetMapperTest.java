package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.test.common.SormTestUtils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

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
        conn.getResultSetTraverser(Player.class)));
  }

  @Test
  void testMapRowsResultSet() {
    sorm.apply(conn -> conn.executeQuery(ParameterizedSql.of("select * from players"),
        conn.getResultSetToMapTraverser()));
  }

}
