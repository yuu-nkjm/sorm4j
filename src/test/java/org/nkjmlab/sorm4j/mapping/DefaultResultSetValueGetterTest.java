package org.nkjmlab.sorm4j.mapping;

import static org.assertj.core.api.Assertions.*;
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
import org.nkjmlab.sorm4j.annotation.OrmIgnore;
import org.nkjmlab.sorm4j.tool.SormTestUtils;

class DefaultResultSetValueGetterTest {

  @Test
  void testGetValueBySetterType() {
    String sql =
        "CREATE TABLE IF NOT EXISTS LocalDateTimeSample(time TIME, date DATE, date_time dateTime, arry ARRAY, fl FLOAT)";
    Sorm sormImpl = SormTestUtils.createSorm();
    sormImpl.run(con -> con.executeUpdate(sql));
    LocalDateTimeSample a = LocalDateTimeSample.buildRandom();
    sormImpl.run(con -> con.insert(a));

    LocalDateTimeSample r = sormImpl.execute(
        con -> con.readFirst(LocalDateTimeSample.class, "select * from LocalDateTimeSample"));
    assertThat(r).isEqualTo(a);
    sormImpl.run(con -> con.insert(Stream.generate(() -> LocalDateTimeSample.buildRandom())
        .limit(10000).toArray(LocalDateTimeSample[]::new)));

    try {
      sormImpl.run(con -> con.update(a));
      failBecauseExceptionWasNotThrown(Exception.class);
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("doesn't have them");
    }


    try {
      sormImpl.execute(con -> con.readMapFirst("select * from LocalDateTimeSample"));
    } catch (Exception e) {
    }

  }

  @Test
  void testGetValueBySqlType() {
    List.of(java.sql.Types.ROWID, java.sql.Types.REF, java.sql.Types.ROWID, java.sql.Types.REF,
        java.sql.Types.NUMERIC, java.sql.Types.NULL, java.sql.Types.LONGVARCHAR,
        java.sql.Types.LONGVARBINARY, java.sql.Types.JAVA_OBJECT, java.sql.Types.FLOAT,
        java.sql.Types.DATALINK, java.sql.Types.BIT, java.sql.Types.ARRAY);


  }

  @Test
  void testSqlTypeToString() {}

  public static class LocalDateTimeSample {
    LocalTime time;
    LocalDate date;
    LocalDateTime dateTime;
    Object[] arry;
    Float fl;

    @OrmIgnore
    int ignoreCol;

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
      ret.arry = new Object[] {"a", 1};
      ret.fl = null;
      return ret;
    }

    public static ZonedDateTime randomZonedDateTime() {
      long t = (long) (ThreadLocalRandom.current().nextDouble() * System.currentTimeMillis());
      return Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault());
    }

    @Override
    public String toString() {
      return "LocalDateTimeSample [time=" + time + ", date=" + date + ", dateTime=" + dateTime
          + ", arry=" + Arrays.toString(arry) + "]";
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
      if (this == obj)
        return true;
      if (!(obj instanceof LocalDateTimeSample))
        return false;
      LocalDateTimeSample other = (LocalDateTimeSample) obj;
      return Arrays.deepEquals(arry, other.arry) && Objects.equals(date, other.date)
          && Objects.equals(dateTime, other.dateTime);
    }

  }

}
