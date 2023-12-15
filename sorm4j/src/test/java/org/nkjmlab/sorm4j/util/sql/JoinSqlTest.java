package org.nkjmlab.sorm4j.util.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.common.TableMetaData;

class JoinSqlTest {

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
        "select column1,column2 from table1 where column1 > 100 order by column2 limit 10 offset 5";

    JoinSql.Builder builder = JoinSql.builder(mockTableMetaData);
    String actualSql = builder.where("column1 > 100").orderBy("column2").limit(10, 5).build();

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
}
