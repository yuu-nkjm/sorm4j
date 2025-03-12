package org.nkjmlab.sorm4j.sql.util.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.sql.statement.ConditionSql;
import org.nkjmlab.sorm4j.util.sql.statement.JoinSql;

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
            "select PLAYERS.ID as P_DOT_ID,PLAYERS.NAME as P_DOT_NAME,PLAYERS.ADDRESS as P_DOT_ADDRESS,GUESTS.ID as G_DOT_ID,GUESTS.NAME as G_DOT_NAME,GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS left join GUESTS using (id)");
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
            "select PLAYERS.ID as P_DOT_ID,PLAYERS.NAME as P_DOT_NAME,PLAYERS.ADDRESS as P_DOT_ADDRESS,GUESTS.ID as G_DOT_ID,GUESTS.NAME as G_DOT_NAME,GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS left join GUESTS on player.id=guests.id");
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
            "select PLAYERS.ID as P_DOT_ID,PLAYERS.NAME as P_DOT_NAME,PLAYERS.ADDRESS as P_DOT_ADDRESS,GUESTS.ID as G_DOT_ID,GUESTS.NAME as G_DOT_NAME,GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS using (id)");
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
            "select PLAYERS.ID as P_DOT_ID,PLAYERS.NAME as P_DOT_NAME,PLAYERS.ADDRESS as P_DOT_ADDRESS,GUESTS.ID as G_DOT_ID,GUESTS.NAME as G_DOT_NAME,GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS on player.id=guests.id");
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
            "select PLAYERS.ID as P_DOT_ID,PLAYERS.NAME as P_DOT_NAME,PLAYERS.ADDRESS as P_DOT_ADDRESS,GUESTS.ID as G_DOT_ID,GUESTS.NAME as G_DOT_NAME,GUESTS.ADDRESS as G_DOT_ADDRESS from PLAYERS join GUESTS on player.id=guests.id");
  }

  @Test
  void testBuilder() {
    TableMetaData mockTableMetaData = mock(TableMetaData.class);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockOtherTableMetaData = mock(TableMetaData.class);
    when(mockOtherTableMetaData.getTableName()).thenReturn("table2");
    when(mockOtherTableMetaData.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 join table2 using (column1,column2)";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql = builder.joinUsing(mockOtherTableMetaData, "column1", "column2").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithWhereOrderByAndLimit() {
    TableMetaData mockTableMetaData = mock(TableMetaData.class);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql =
        "select column1,column2 from table1 where (column1 > 100 and column2 > 100) order by column2 limit 10 offset 5";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql =
        builder
            .where(ConditionSql.and("column1 > 100", "column2 > 100"))
            .orderBy("column2")
            .limit(10, 5)
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithDistinct() {
    TableMetaData mockTableMetaData = mock(TableMetaData.class);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql = "select distinct column1,column2 from table1";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql = builder.distinct().build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithMultipleJoins() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    TableMetaData mockTable3 = mock(TableMetaData.class);
    when(mockTable3.getTableName()).thenReturn("table3");
    when(mockTable3.getColumnAliases()).thenReturn(List.of("column5", "column6"));

    String expectedSql =
        "select column1,column2,column3,column4,column5,column6 "
            + "from table1 "
            + "join table2 on table1.column1 = table2.column3 "
            + "left join table3 using (column5)";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql =
        builder
            .joinOn(mockTable2, "table1.column1 = table2.column3")
            .leftJoinUsing(mockTable3, "column5")
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithComplexJoinConditions() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    TableMetaData mockTable3 = mock(TableMetaData.class);
    when(mockTable3.getTableName()).thenReturn("table3");
    when(mockTable3.getColumnAliases()).thenReturn(List.of("column5", "column6"));

    String expectedSql =
        "select column1,column2,column3,column4,column5,column6 "
            + "from table1 "
            + "join table2 on table1.column1 = table2.column3 and table1.column2 = table2.column4 "
            + "left join table3 on table3.column5 = table1.column1";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql =
        builder
            .joinOn(
                mockTable2, "table1.column1 = table2.column3 and table1.column2 = table2.column4")
            .leftJoinOn(mockTable3, "table3.column5 = table1.column1")
            .build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithOrderByAndLimit() {
    TableMetaData mockTableMetaData = mock(TableMetaData.class);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql =
        "select column1,column2 from table1 order by column1 desc limit 5 offset 10";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql = builder.orderBy("column1 desc").limit(5, 10).build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testBuilderWithOrderByAndLimit1() {
    TableMetaData mockTableMetaData = mock(TableMetaData.class);
    when(mockTableMetaData.getTableName()).thenReturn("table1");
    when(mockTableMetaData.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    String expectedSql = "select column1,column2 from table1 where column1=1 limit 10";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql = builder.where("column1=1").limit(10).build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  public void testBasicInnerJoinWithMock() {
    TableMetaData table1 = mock(TableMetaData.class);
    TableMetaData table2 = mock(TableMetaData.class);

    when(table1.getColumnAliases()).thenReturn(List.of("id", "name"));
    when(table1.getTableName()).thenReturn("table1");
    when(table2.getColumnAliases()).thenReturn(List.of("table1_id", "description"));
    when(table2.getTableName()).thenReturn("table2");

    String sql = JoinSql.builder(table1).joinOn(table2, "table1.id = table2.table1_id").build();

    assertEquals(
        "select id,name,table1_id,description from table1 join table2 on table1.id = table2.table1_id",
        sql);
  }

  @Test
  public void testJoinWithMultipleConditions() {
    TableMetaData table1 = mock(TableMetaData.class);
    TableMetaData table2 = mock(TableMetaData.class);

    when(table1.getColumnAliases()).thenReturn(List.of("id", "name"));
    when(table1.getTableName()).thenReturn("table1");
    when(table2.getColumnAliases()).thenReturn(List.of("id", "description"));
    when(table2.getTableName()).thenReturn("table2");

    String sql =
        JoinSql.builder(table1)
            .joinOn(table2, "table1.id = table2.id")
            .where("table1.name = 'Alice'")
            .orderBy("table2.description")
            .build();

    assertEquals(
        "select id,name,id,description from table1 join table2 on table1.id = table2.id where table1.name = 'Alice' order by table2.description",
        sql);
  }

  @Test
  void testLeftJoinUsing() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 left join table2 using (column1,column2)";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql = builder.leftJoinUsing(mockTable2, "column1", "column2").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testLeftJoinOn() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 left join table2 on table1.column1 = table2.column3";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql = builder.leftJoinOn(mockTable2, "table1.column1 = table2.column3").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testJoinUsing() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 join table2 using (column1,column2)";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql = builder.joinUsing(mockTable2, "column1", "column2").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testJoinOn() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 join table2 on table1.column1 = table2.column3";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql = builder.joinOn(mockTable2, "table1.column1 = table2.column3").build();

    assertEquals(expectedSql, actualSql);
  }

  @Test
  void testJoin() {
    TableMetaData mockTable1 = mock(TableMetaData.class);
    when(mockTable1.getTableName()).thenReturn("table1");
    when(mockTable1.getColumnAliases()).thenReturn(List.of("column1", "column2"));

    TableMetaData mockTable2 = mock(TableMetaData.class);
    when(mockTable2.getTableName()).thenReturn("table2");
    when(mockTable2.getColumnAliases()).thenReturn(List.of("column3", "column4"));

    String expectedSql =
        "select column1,column2,column3,column4 from table1 inner join table2 on table1.column1 = table2.column3";

    JoinSql.Builder builder = JoinSql.builder(mockTable1);
    String actualSql =
        builder.join("inner join", mockTable2, "on table1.column1 = table2.column3").build();

    assertEquals(expectedSql, actualSql);
  }
}
