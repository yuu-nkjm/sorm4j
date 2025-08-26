package org.nkjmlab.sorm4j.sql.util.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.sql.metadata.OrmTableMetaData;
import org.nkjmlab.sorm4j.sql.statement.ConditionSql;
import org.nkjmlab.sorm4j.sql.statement.JoinSql;
import org.nkjmlab.sorm4j.table.orm.TableOrm;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class JoinSqlTest {
  @Test
  void testLeftJoinUsingTable() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    String sql =
        JoinSql.builder(sorm.getTable(Player.class))
            .leftJoinUsing(sorm.getTable(Guest.class), "id")
            .build();
    assertThat(sql)
        .isEqualTo(
            "select PLAYERS.ID as P_DOT_ID, PLAYERS.NAME as P_DOT_NAME, PLAYERS.ADDRESS as P_DOT_ADDRESS, GUESTS.ID as G_DOT_ID, GUESTS.NAME as G_DOT_NAME, GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS left join GUESTS using (id)");
  }

  @Test
  void testLeftJoinOnTable() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    String sql =
        JoinSql.builder(sorm.getTable(Player.class))
            .leftJoinOn(sorm.getTable(Guest.class), "player.id=guests.id")
            .build();
    assertThat(sql)
        .isEqualTo(
            "select PLAYERS.ID as P_DOT_ID, PLAYERS.NAME as P_DOT_NAME, PLAYERS.ADDRESS as P_DOT_ADDRESS, GUESTS.ID as G_DOT_ID, GUESTS.NAME as G_DOT_NAME, GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS left join GUESTS on player.id=guests.id");
  }

  @Test
  void testJoinUsingTable() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    String sql =
        JoinSql.builder(sorm.getTable(Player.class))
            .joinUsing(sorm.getTable(Guest.class), "id")
            .build();
    assertThat(sql)
        .isEqualTo(
            "select PLAYERS.ID as P_DOT_ID, PLAYERS.NAME as P_DOT_NAME, PLAYERS.ADDRESS as P_DOT_ADDRESS, GUESTS.ID as G_DOT_ID, GUESTS.NAME as G_DOT_NAME, GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS using (id)");
  }

  @Test
  void testJoinOnTable() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    String sql =
        JoinSql.builder(sorm.getTable(Player.class))
            .joinOn(sorm.getTable(Guest.class), "player.id=guests.id")
            .build();
    assertThat(sql)
        .isEqualTo(
            "select PLAYERS.ID as P_DOT_ID, PLAYERS.NAME as P_DOT_NAME, PLAYERS.ADDRESS as P_DOT_ADDRESS, GUESTS.ID as G_DOT_ID, GUESTS.NAME as G_DOT_NAME, GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS on player.id=guests.id");
  }

  @Test
  void testJoinTable() {
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    String sql =
        JoinSql.builder(sorm.getTable(Player.class))
            .join("join", sorm.getTable(Guest.class), "on player.id=guests.id")
            .build();
    assertThat(sql)
        .isEqualTo(
            "select PLAYERS.ID as P_DOT_ID, PLAYERS.NAME as P_DOT_NAME, PLAYERS.ADDRESS as P_DOT_ADDRESS, GUESTS.ID as G_DOT_ID, GUESTS.NAME as G_DOT_NAME, GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS on player.id=guests.id");
  }

  @Test
  void testBuilder() {
    TableOrm<?> mockTableOrm1 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData1 = mock(OrmTableMetaData.class);
    when(mockTableOrm1.getOrmTableMetaData()).thenReturn(mockTableMetaData1);
    when(mockTableMetaData1.getTableName()).thenReturn("table1");
    when(mockTableMetaData1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableOrm<?> mockTableOrm2 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData2 = mock(OrmTableMetaData.class);
    when(mockTableOrm2.getOrmTableMetaData()).thenReturn(mockTableMetaData2);
    when(mockTableMetaData2.getTableName()).thenReturn("table2");
    when(mockTableMetaData2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1, column2, column3, column4 from table1 join table2 using (column1, column2)";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm1);
    String actualSql = builder.joinUsing(mockTableOrm2, "column1", "column2").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithWhereOrderByAndLimit() {
    TableOrm<?> mockTableOrm = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData = mock(OrmTableMetaData.class);
    when(mockTableOrm.getOrmTableMetaData()).thenReturn(mockTableMetaData);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql =
        "select column1, column2 from table1 where (column1 > 100 and column2 > 100) order by column2 limit 10 offset 5";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm);
    String actualSql =
        builder
            .where(ConditionSql.and("column1 > 100", "column2 > 100"))
            .orderBy("column2")
            .limit(10, 5)
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithWhereOrderByAndLimit1() {
    TableOrm<?> mockTableOrm = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData = mock(OrmTableMetaData.class);
    when(mockTableOrm.getOrmTableMetaData()).thenReturn(mockTableMetaData);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql =
        "select column1, column2 from table1 where (column1 > 100 and column2 > 100) order by column2 limit 1";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm);
    String actualSql =
        builder
            .where(ConditionSql.and("column1 > 100", "column2 > 100"))
            .orderBy("column2")
            .limit(1)
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithDistinct() {
    TableOrm<?> mockTableOrm = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData = mock(OrmTableMetaData.class);
    when(mockTableOrm.getOrmTableMetaData()).thenReturn(mockTableMetaData);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql = "select distinct column1, column2 from table1";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm);
    String actualSql = builder.distinct().build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithMultipleJoins() {
    TableOrm<?> mockTableOrm1 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData1 = mock(OrmTableMetaData.class);
    when(mockTableOrm1.getOrmTableMetaData()).thenReturn(mockTableMetaData1);
    when(mockTableMetaData1.getTableName()).thenReturn("table1");
    when(mockTableMetaData1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableOrm<?> mockTableOrm2 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData2 = mock(OrmTableMetaData.class);
    when(mockTableOrm2.getOrmTableMetaData()).thenReturn(mockTableMetaData2);
    when(mockTableMetaData2.getTableName()).thenReturn("table2");
    when(mockTableMetaData2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    TableOrm<?> mockTableOrm3 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData3 = mock(OrmTableMetaData.class);
    when(mockTableOrm3.getOrmTableMetaData()).thenReturn(mockTableMetaData3);
    when(mockTableMetaData3.getTableName()).thenReturn("table3");
    when(mockTableMetaData3.getColumnAliases()).thenReturn(List.of("column5", "column6"));

    String expectedSql =
        "select column1, column2, column3, column4, column5, column6 "
            + "from table1 "
            + "join table2 on table1.column1 = table2.column3 "
            + "left join table3 using (column5)";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm1);
    String actualSql =
        builder
            .joinOn(mockTableOrm2, "table1.column1 = table2.column3")
            .leftJoinUsing(mockTableOrm3, "column5")
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithComplexJoinConditions() {
    TableOrm<?> mockTableOrm1 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData1 = mock(OrmTableMetaData.class);
    when(mockTableOrm1.getOrmTableMetaData()).thenReturn(mockTableMetaData1);
    when(mockTableMetaData1.getTableName()).thenReturn("table1");
    when(mockTableMetaData1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableOrm<?> mockTableOrm2 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData2 = mock(OrmTableMetaData.class);
    when(mockTableOrm2.getOrmTableMetaData()).thenReturn(mockTableMetaData2);
    when(mockTableMetaData2.getTableName()).thenReturn("table2");
    when(mockTableMetaData2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    TableOrm<?> mockTableOrm3 = mock(TableOrm.class);
    OrmTableMetaData mockTableMetaData3 = mock(OrmTableMetaData.class);
    when(mockTableOrm3.getOrmTableMetaData()).thenReturn(mockTableMetaData3);
    when(mockTableMetaData3.getTableName()).thenReturn("table3");
    when(mockTableMetaData3.getColumnAliases()).thenReturn(List.of("column5", "column6"));

    String expectedSql =
        "select column1, column2, column3, column4, column5, column6 "
            + "from table1 "
            + "join table2 on table1.column1 = table2.column3 and table1.column2 = table2.column4 "
            + "left join table3 on table3.column5 = table1.column1";

    JoinSql.Builder builder = JoinSql.builder(mockTableOrm1);
    String actualSql =
        builder
            .joinOn(
                mockTableOrm2,
                "table1.column1 = table2.column3 and table1.column2 = table2.column4")
            .leftJoinOn(mockTableOrm3, "table3.column5 = table1.column1")
            .build();

    assertEquals(expectedSql, actualSql);
  }
}
