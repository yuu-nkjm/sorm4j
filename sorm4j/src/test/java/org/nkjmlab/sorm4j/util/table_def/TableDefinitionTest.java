package org.nkjmlab.sorm4j.util.table_def;

import static org.assertj.core.api.Assertions.*;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.h2.sql.H2CsvReadSql;
import org.nkjmlab.sorm4j.util.table_def.annotation.AutoIncrement;
import org.nkjmlab.sorm4j.util.table_def.annotation.Check;
import org.nkjmlab.sorm4j.util.table_def.annotation.Default;
import org.nkjmlab.sorm4j.util.table_def.annotation.Index;
import org.nkjmlab.sorm4j.util.table_def.annotation.NotNull;
import org.nkjmlab.sorm4j.util.table_def.annotation.PrimaryKey;
import org.nkjmlab.sorm4j.util.table_def.annotation.Unique;

class TableDefinitionTest {

  @Test
  void test() throws URISyntaxException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    TableDefinition def = TableDefinition.builder(TableDefExample.class).build();
    assertThat(def.getTableNameAndColumnDefinitions()).isEqualTo(
        "TABLEDEFEXAMPLES(id bigint primary key auto_increment, booleanCol boolean, byteCol tinyint, charCol character, shortCol smallint, intCol integer default 0, floatCol float, doubleCol double check (doubleCol>0), bigDecimal numeric, phoneNumber varchar not null, localDateCol date, localTimeCol time, localDateTime timestamp, offsetTime time with time zone, offsetDateTime timestamp with time zone, blob blob, clob clob, inputStream longvarbinary, reader longvarchar, unique(phoneNumber))");
    sorm.executeUpdate(def.getCreateTableIfNotExistsStatement());

    String selectSql = H2CsvReadSql
        .builder(Paths.get(TableDefinitionTest.class.getResource("test.csv").toURI()).toFile())
        .build().getCsvReadAndSelectSql();

    List<TableDefExample> ret = sorm.readList(TableDefExample.class, selectSql);
    sorm.insert(ret);


    assertThat(sorm.selectAll(TableDefExample.class).get(0).phoneNumber).isEqualTo("000-000-0000");
    System.out.println(sorm.selectAll(TableDefExample.class));
  }

  @OrmRecord
  public static class TableDefExample {
    public final Long id;
    public final Boolean booleanCol;
    public final Byte byteCol;
    public final Character charCol;
    public final Short shortCol;
    public final Integer intCol;
    public final Float floatCol;
    public final Double doubleCol;
    public final BigDecimal bigDecimal;
    public final String phoneNumber;
    public final LocalDate localDateCol;
    public final LocalTime localTimeCol;
    public final LocalDateTime localDateTime;
    public final java.time.OffsetTime offsetTime;
    public final java.time.OffsetDateTime offsetDateTime;
    public final java.sql.Blob blob;
    public final java.sql.Clob clob;
    public final java.io.InputStream inputStream;
    public final java.io.Reader reader;


    public TableDefExample(@PrimaryKey @AutoIncrement Long id, Boolean booleanCol, Byte byteCol,
        Character charCol, Short shortCol, @Default("0") Integer intCol, Float floatCol,
        @Check("doubleCol>0") Double doubleCol, BigDecimal bigDecimal,
        @Index @Unique @NotNull String phoneNumber, LocalDate localDateCol, LocalTime localTimeCol,
        LocalDateTime localDateTimeCol, OffsetTime offsetTime, OffsetDateTime offsetDateTime,
        Blob blob, Clob clob, InputStream inputStream, Reader reader) {
      this.id = id;
      this.booleanCol = booleanCol;
      this.byteCol = byteCol;
      this.charCol = charCol;
      this.shortCol = shortCol;
      this.intCol = intCol;
      this.floatCol = floatCol;
      this.doubleCol = doubleCol;
      this.bigDecimal = bigDecimal;
      this.phoneNumber = phoneNumber;
      this.localDateCol = localDateCol;
      this.localTimeCol = localTimeCol;
      this.localDateTime = localDateTimeCol;
      this.offsetTime = offsetTime;
      this.offsetDateTime = offsetDateTime;
      this.blob = blob;
      this.clob = clob;
      this.inputStream = inputStream;
      this.reader = reader;
    }



    @Override
    public String toString() {
      return "TableDefExample [id=" + id + ", dateCol=" + localDateCol + ", timeCol=" + localTimeCol
          + ", intCol=" + intCol + ", doubleCol=" + doubleCol + ", phoneNumber=" + phoneNumber
          + ", booleanCol=" + booleanCol + ", byteCol=" + byteCol + ", charCol=" + charCol
          + ", shortCol=" + shortCol + ", floatCol=" + floatCol + ", offsetTime=" + offsetTime
          + ", offsetDateTime=" + offsetDateTime + ", localDateTime=" + localDateTime
          + ", bigDecimal=" + bigDecimal + ", blob=" + blob + ", clob=" + clob + ", inputStream="
          + inputStream + ", reader=" + reader + "]";
    }


  }

}
