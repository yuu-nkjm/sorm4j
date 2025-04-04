package org.nkjmlab.sorm4j.internal.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.mapping.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DefaultResultSetValueGetterTest {

  @Test
  void testGetValueBySetterType() {
    String sql =
        "CREATE TABLE IF NOT EXISTS Local_Date_Time_Sample(time TIME, date DATE, date_time dateTime, arry VARCHAR ARRAY, fl FLOAT)";
    Sorm sorm = SormTestUtils.createSormWithNewDatabaseAndCreateTables();
    sorm.acceptHandler(con -> con.executeUpdate(sql));
    LocalDateTimeSample a = LocalDateTimeSample.buildRandom();
    sorm.acceptHandler(con -> con.insert(a));

    LocalDateTimeSample r =
        sorm.applyHandler(
            con ->
                con.readFirst(LocalDateTimeSample.class, "select * from Local_Date_Time_Sample"));
    assertThat(r).isEqualTo(a);
    sorm.acceptHandler(
        con ->
            con.insert(
                Stream.generate(() -> LocalDateTimeSample.buildRandom())
                    .limit(10000)
                    .toArray(LocalDateTimeSample[]::new)));

    try {
      sorm.acceptHandler(con -> con.update(a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("doesn't have them");
    }

    RowMap map = sorm.readFirst(RowMap.class, "select * from Local_Date_Time_Sample");

    assertThat(Arrays.asList(map.getArray("arry", String.class))).isEqualTo(List.of("a", "1"));
  }

  @Test
  void testGetValueBySqlType() {
    List.of(
        java.sql.Types.ROWID,
        java.sql.Types.REF,
        java.sql.Types.ROWID,
        java.sql.Types.REF,
        java.sql.Types.NUMERIC,
        java.sql.Types.NULL,
        java.sql.Types.LONGVARCHAR,
        java.sql.Types.LONGVARBINARY,
        java.sql.Types.JAVA_OBJECT,
        java.sql.Types.FLOAT,
        java.sql.Types.DATALINK,
        java.sql.Types.BIT,
        java.sql.Types.ARRAY);
  }

  @Test
  void testSqlTypeToString() {}

  public static class LocalDateTimeSample {
    public LocalTime time;
    public LocalDate date;
    public LocalDateTime dateTime;
    public String[] arry;
    public Float fl;

    @OrmIgnore int ignoreCol;

    @OrmIgnore
    public int getIgnoreCol() {
      return ignoreCol;
    }

    public void setIgnoreCol(int i) {
      this.ignoreCol = i;
    }

    public static LocalDateTimeSample buildRandom() {
      LocalDateTimeSample ret = new LocalDateTimeSample();
      ret.date = randomZonedDateTime().toLocalDate();
      ret.time = randomZonedDateTime().toLocalTime();
      ret.dateTime = randomZonedDateTime().toLocalDateTime();
      ret.arry = new String[] {"a", "1"};
      ret.fl = null;
      return ret;
    }

    public static ZonedDateTime randomZonedDateTime() {
      long t = (long) (ThreadLocalRandom.current().nextDouble() * System.currentTimeMillis());
      return Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault());
    }

    @Override
    public String toString() {
      return "LocalDateTimeSample [time="
          + time
          + ", date="
          + date
          + ", dateTime="
          + dateTime
          + ", arry="
          + Arrays.toString(arry)
          + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.deepHashCode(arry);
      result = prime * result + Objects.hash(date, dateTime, time);
      return result;
    }

    // without array
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof LocalDateTimeSample)) {
        return false;
      }
      LocalDateTimeSample other = (LocalDateTimeSample) obj;
      return Arrays.deepEquals(arry, other.arry)
          && Objects.equals(date, other.date)
          && Objects.equals(dateTime, other.dateTime);
    }
  }
}
