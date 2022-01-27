package org.nkjmlab.sorm4j.internal;

import static org.assertj.core.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.SormException;
import org.nkjmlab.sorm4j.context.DefaultColumnToFieldAccessorMapper;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToMapEntryConverter;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.DefaultTableNameMapper;
import org.nkjmlab.sorm4j.context.MultiRowProcessorFactory;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.mapping.SqlResultToColumnsMapping;
import org.nkjmlab.sorm4j.test.common.Guest;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.test.common.Sport;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

class SormContextImplTest {

  @Test
  void testBuild() {
    SormContext context =
        SormContext.builder().setColumnFieldMapper(new DefaultColumnToFieldAccessorMapper())
            .setTableNameMapper(new DefaultTableNameMapper())
            .setColumnValueToMapEntryConverter(new DefaultColumnValueToMapEntryConverter())
            .setColumnValueToJavaObjectConverter(new DefaultColumnValueToJavaObjectConverters())
            .setSqlParametersSetter(new DefaultSqlParametersSetter())
            .setMultiRowProcessorFactory(MultiRowProcessorFactory.builder().setBatchSize(10)
                .setBatchSizeWithMultiRow(20).build())
            .setLoggerContext(LoggerContext.builder().enable(LoggerContext.Category.MAPPING)
                .disable(LoggerContext.Category.MAPPING).build())
            .build();

    Sorm sorm = SormTestUtils.createSormWithContextAndTables(context);

    try (Connection conn = sorm.getJdbcConnection()) {
      assertThatThrownBy(() -> SormImpl.DEFAULT_CONTEXT.getTableName(conn, Baby.class))
          .isInstanceOfSatisfying(SormException.class,
              e -> assertThat(e.getMessage()).contains("BABIES"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    sorm.insert(SormTestUtils.GUEST_ALICE);
    sorm.insert(SormTestUtils.PLAYER_ALICE);

    SormContextImpl ctx = (SormContextImpl) context;
    SqlResultToColumnsMapping<Player> pm = ctx.getColumnsMapping(Player.class);
    SqlResultToColumnsMapping<Guest> gm = ctx.getColumnsMapping(Guest.class);
    SqlResultToColumnsMapping<Sport> sm = ctx.getColumnsMapping(Sport.class);
    System.out.println(context.toString());
    assertThat(pm.toString()).contains("created by");

  }

  public static class Baby {

  }


}
