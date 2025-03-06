package org.nkjmlab.sorm4j.table.definition;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
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
import org.nkjmlab.sorm4j.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2SimpleDefinedTable;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecord;
import org.nkjmlab.sorm4j.table.definition.annotation.AutoIncrement;
import org.nkjmlab.sorm4j.table.definition.annotation.Check;
import org.nkjmlab.sorm4j.table.definition.annotation.Default;
import org.nkjmlab.sorm4j.table.definition.annotation.Index;
import org.nkjmlab.sorm4j.table.definition.annotation.IndexColumnPair;
import org.nkjmlab.sorm4j.table.definition.annotation.NotNull;
import org.nkjmlab.sorm4j.table.definition.annotation.PrimaryKey;
import org.nkjmlab.sorm4j.table.definition.annotation.Unique;
import org.nkjmlab.sorm4j.table.definition.annotation.UniqueConstraint;
import org.nkjmlab.sorm4j.test.common.Player;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class TableDefinitionTest {

  @Test
  void testEnum() {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    TableDefinition def = TableDefinition.builder(SimpleEnum.class).build();
    def.createTableIfNotExists(sorm);
    sorm.insert(new SimpleEnum(1, EnumExample.A));
    assertThat(sorm.readFirst(SimpleEnum.class, "SELECT * FROM SIMPLE_ENUMS").enumCol)
        .isInstanceOf(EnumExample.class);
    assertThat(TableDefinition.toTableName(Player.class)).isEqualTo("PLAYERS");
    assertThat(TableDefinition.builder("").setTableName("tbn").build().getTableName())
        .isEqualTo("tbn");
  }

  @Test
  void test() throws URISyntaxException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();

    TableDefinition def = TableDefinition.builder(TableDefExample.class).build();
    assertThat(def.toString())
        .isEqualTo(
            "TableDefinition [tableName=TABLE_DEF_EXAMPLES, tableNameAndColumnDefinitions=TABLE_DEF_EXAMPLES(ID bigint primary key auto_increment, BOOLEAN_COL boolean, BYTE_COL tinyint, CHAR_COL character, SHORT_COL smallint, INT_COL integer default 0, FLOAT_COL float, DOUBLE_COL double check (double_col>0), BIG_DECIMAL numeric, PHONE_NUMBER varchar not null, LOCAL_DATE_COL date, LOCAL_TIME_COL time, LOCAL_DATE_TIME timestamp, OFFSET_TIME time with time zone, OFFSET_DATE_TIME timestamp with time zone, BLOB blob, CLOB clob, INPUT_STREAM longvarbinary, READER longvarchar, STR_ARRAY varchar array, unique(boolean_col, byte_col), unique(boolean_col, char_col), unique(PHONE_NUMBER), check(int_col>=0)), columnNames=[ID, BOOLEAN_COL, BYTE_COL, CHAR_COL, SHORT_COL, INT_COL, FLOAT_COL, DOUBLE_COL, BIG_DECIMAL, PHONE_NUMBER, LOCAL_DATE_COL, LOCAL_TIME_COL, LOCAL_DATE_TIME, OFFSET_TIME, OFFSET_DATE_TIME, BLOB, CLOB, INPUT_STREAM, READER, STR_ARRAY], createTableStatement=create table if not exists TABLE_DEF_EXAMPLES(ID bigint primary key auto_increment, BOOLEAN_COL boolean, BYTE_COL tinyint, CHAR_COL character, SHORT_COL smallint, INT_COL integer default 0, FLOAT_COL float, DOUBLE_COL double check (double_col>0), BIG_DECIMAL numeric, PHONE_NUMBER varchar not null, LOCAL_DATE_COL date, LOCAL_TIME_COL time, LOCAL_DATE_TIME timestamp, OFFSET_TIME time with time zone, OFFSET_DATE_TIME timestamp with time zone, BLOB blob, CLOB clob, INPUT_STREAM longvarbinary, READER longvarchar, STR_ARRAY varchar array, unique(boolean_col, byte_col), unique(boolean_col, char_col), unique(PHONE_NUMBER), check(int_col>=0)), dropTableStatement=drop table if exists TABLE_DEF_EXAMPLES, createIndexStatements=[create index if not exists index_in_TABLE_DEF_EXAMPLES_on_boolean_col_byte_col on TABLE_DEF_EXAMPLES(boolean_col, byte_col), create index if not exists index_in_TABLE_DEF_EXAMPLES_on_boolean_col_char_col on TABLE_DEF_EXAMPLES(boolean_col, char_col), create index if not exists index_in_TABLE_DEF_EXAMPLES_on_PHONE_NUMBER on TABLE_DEF_EXAMPLES(PHONE_NUMBER)]]");

    assertThat(def.getTableNameAndColumnDefinitions())
        .isEqualTo(
            "TABLE_DEF_EXAMPLES(ID bigint primary key auto_increment, BOOLEAN_COL boolean, BYTE_COL tinyint, CHAR_COL character, SHORT_COL smallint, INT_COL integer default 0, FLOAT_COL float, DOUBLE_COL double check (double_col>0), BIG_DECIMAL numeric, PHONE_NUMBER varchar not null, LOCAL_DATE_COL date, LOCAL_TIME_COL time, LOCAL_DATE_TIME timestamp, OFFSET_TIME time with time zone, OFFSET_DATE_TIME timestamp with time zone, BLOB blob, CLOB clob, INPUT_STREAM longvarbinary, READER longvarchar, STR_ARRAY varchar array, unique(boolean_col, byte_col), unique(boolean_col, char_col), unique(PHONE_NUMBER), check(int_col>=0))");
    sorm.executeUpdate(def.getCreateTableIfNotExistsStatement());

    List<RowMap> csvRows =
        sorm.readList(
            RowMap.class,
            "select * from "
                + CsvRead.builderForCsvWithHeader(
                        new File(TableDefinitionTest.class.getResource("test.csv").toURI()))
                    .build()
                    .getSql());

    H2SimpleDefinedTable<TableDefExample> table =
        new H2SimpleDefinedTable<>(sorm, TableDefExample.class, def);
    table.insertMapIn(csvRows);

    assertThat(sorm.selectAll(TableDefExample.class).get(0).phoneNumber).isEqualTo("000-000-0000");
    System.out.println(sorm.selectAll(TableDefExample.class));
  }

  public static enum EnumExample {
    A,
    B,
    C
  }

  @OrmRecord
  public static class SimpleEnum {
    public final long id;
    // @SuppressWarnings("exports")
    public final EnumExample enumCol;

    // @SuppressWarnings("exports")
    public SimpleEnum(long id, EnumExample en) {
      this.id = id;
      this.enumCol = en;
    }
  }

  @OrmRecord
  @IndexColumnPair({"boolean_col", "byte_col"})
  @IndexColumnPair({"boolean_col", "char_col"})
  @UniqueConstraint({"boolean_col", "byte_col"})
  @UniqueConstraint({"boolean_col", "char_col"})
  @Check("int_col>=0")
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
    public final String[] strArray;

    public TableDefExample(
        @PrimaryKey @AutoIncrement Long id,
        Boolean booleanCol,
        Byte byteCol,
        Character charCol,
        Short shortCol,
        @Default("0") Integer intCol,
        Float floatCol,
        @Check("double_col>0") Double doubleCol,
        BigDecimal bigDecimal,
        @Index @Unique @NotNull String phoneNumber,
        LocalDate localDateCol,
        LocalTime localTimeCol,
        LocalDateTime localDateTimeCol,
        OffsetTime offsetTime,
        OffsetDateTime offsetDateTime,
        Blob blob,
        Clob clob,
        InputStream inputStream,
        Reader reader,
        String[] strArray) {
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
      this.strArray = strArray;
    }

    @Override
    public String toString() {
      return "TableDefExample [id="
          + id
          + ", dateCol="
          + localDateCol
          + ", timeCol="
          + localTimeCol
          + ", intCol="
          + intCol
          + ", doubleCol="
          + doubleCol
          + ", phoneNumber="
          + phoneNumber
          + ", booleanCol="
          + booleanCol
          + ", byteCol="
          + byteCol
          + ", charCol="
          + charCol
          + ", shortCol="
          + shortCol
          + ", floatCol="
          + floatCol
          + ", offsetTime="
          + offsetTime
          + ", offsetDateTime="
          + offsetDateTime
          + ", localDateTime="
          + localDateTime
          + ", bigDecimal="
          + bigDecimal
          + ", blob="
          + blob
          + ", clob="
          + clob
          + ", inputStream="
          + inputStream
          + ", reader="
          + reader
          + "]";
    }
  }
}
