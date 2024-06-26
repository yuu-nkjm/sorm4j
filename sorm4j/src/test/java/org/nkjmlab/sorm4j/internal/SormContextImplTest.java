package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.ColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.ColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.ColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToMapValueConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.PreparedStatementSupplier;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.context.TableNameMapper;
import org.nkjmlab.sorm4j.context.TableSqlFactory;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

class SormContextImplTest {

  @Test
  void testBuild() {
    SormContext context =
        SormContext.builder()
            .setColumnToFieldAccessorMapper(new DefaultColumnToFieldAccessorMapper())
            .setTableNameMapper(new DefaultTableNameMapper())
            .setColumnValueToMapValueConverters(new DefaultColumnValueToMapValueConverters())
            .setColumnValueToJavaObjectConverters(new DefaultColumnValueToJavaObjectConverters())
            .setSqlParametersSetter(new DefaultSqlParametersSetter())
            .setMultiRowProcessorFactory(
                MultiRowProcessorFactory.builder()
                    .setBatchSize(10)
                    .setBatchSizeWithMultiRow(20)
                    .build())
            .setLoggerContext(
                LoggerContext.builder()
                    .enable(LoggerContext.Category.MAPPING)
                    .disable(LoggerContext.Category.MAPPING)
                    .build())
            .build();

    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables(context);

    try (Connection conn = sorm.openJdbcConnection()) {
      assertThatThrownBy(() -> SormImpl.DEFAULT_CONTEXT.getTableName(conn, Temporary.class))
          .isInstanceOfSatisfying(
              SormException.class, e -> assertThat(e.getMessage()).contains("TEMPORARIES"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    sorm.insert(SormTestUtils.GUEST_ALICE);
    sorm.insert(SormTestUtils.PLAYER_ALICE);
    sorm.insert(SormTestUtils.TENNIS);

    sorm.selectAll(Guest.class);
    sorm.selectAll(Player.class);
    sorm.selectAll(Sport.class);

    assertThatThrownBy(() -> sorm.readList(Temp.class, "select * from guests"))
        .isInstanceOfSatisfying(
            SormException.class,
            e ->
                assertThat(e.getMessage())
                    .isEqualTo(
                        "The given container class [class org.nkjmlab.sorm4j.internal.SormContextImplTest$Temp] annotated by @OrmRecord should have the canonical constructor."));

    assertThatThrownBy(() -> sorm.readList(Temporary.class, "select * from guests"))
        .isInstanceOfSatisfying(
            SormException.class,
            e ->
                assertThat(e.getMessage())
                    .isEqualTo(
                        "The given container class [class org.nkjmlab.sorm4j.internal.SormContextImplTest$Temporary] should have one or less constructor annotated by @OrmConstructor."));

    // assertThat(context.toString()).contains("created by");

    System.out.println(context.toString());
  }

  @Test
  void testBuilder() {
    SormConfig mockConfig = mock(SormConfig.class);
    SormContextImpl sormContextImpl = new SormContextImpl(mockConfig);
    ColumnToFieldAccessorMapper mockColumnFieldMapper = mock(ColumnToFieldAccessorMapper.class);
    ColumnValueToJavaObjectConverters mockColumnValueToJavaObjectConverter =
        mock(ColumnValueToJavaObjectConverters.class);
    ColumnValueToMapValueConverters mockColumnValueToMapValueConverter =
        mock(ColumnValueToMapValueConverters.class);
    LoggerContext mockLoggerContext = mock(LoggerContext.class);
    MultiRowProcessorFactory mockMultiRowProcessorFactory = mock(MultiRowProcessorFactory.class);
    PreparedStatementSupplier mockPreparedStatementSupplier = mock(PreparedStatementSupplier.class);
    SqlParametersSetter mockSqlParametersSetter = mock(SqlParametersSetter.class);
    TableNameMapper mockTableNameMapper = mock(TableNameMapper.class);
    TableSqlFactory mockTableSqlFactory = mock(TableSqlFactory.class);

    when(mockConfig.getColumnToFieldAccessorMapper()).thenReturn(mockColumnFieldMapper);
    when(mockConfig.getColumnValueToJavaObjectConverter())
        .thenReturn(mockColumnValueToJavaObjectConverter);
    when(mockConfig.getColumnValueToMapValueConverter())
        .thenReturn(mockColumnValueToMapValueConverter);
    when(mockConfig.getLoggerContext()).thenReturn(mockLoggerContext);
    when(mockConfig.getMultiRowProcessorFactory()).thenReturn(mockMultiRowProcessorFactory);
    when(mockConfig.getPreparedStatementSupplier()).thenReturn(mockPreparedStatementSupplier);
    when(mockConfig.getSqlParametersSetter()).thenReturn(mockSqlParametersSetter);
    when(mockConfig.getTableNameMapper()).thenReturn(mockTableNameMapper);
    when(mockConfig.getTableSqlFactory()).thenReturn(mockTableSqlFactory);

    SormContext.Builder builder = sormContextImpl.builder();
    assertNotNull(builder);
  }

  @OrmRecord
  public static class Temp {
    public int id;
    public String name;

    public Temp() {}
  }

  public static class Temporary {

    @OrmConstructor({""})
    public Temporary() {}

    @OrmConstructor({"id"})
    public Temporary(int id) {}
  }
}
