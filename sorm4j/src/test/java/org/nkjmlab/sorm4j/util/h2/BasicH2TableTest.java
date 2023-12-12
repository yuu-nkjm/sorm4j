package org.nkjmlab.sorm4j.util.h2;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.datatype.JsonByte;
import org.nkjmlab.sorm4j.util.h2.sql.CsvColumn;

class BasicH2TableTest {

  @Test
  void test2() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    BasicH2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();

    try (Connection con = sorm.openJdbcConnection()) {
      PreparedStatement ps =
          con.prepareStatement("insert into ORM_RECORD_EXAMPLES VALUES(?,?,? format json)");
      ps.setInt(1, 3);
      ps.setString(2, "C");
      ps.setObject(3, "{\"3\":\"val1\"}".getBytes());
      ps.execute();
      // H2ServerUtils.openBrowser(con, true);
      // Thread.sleep(1000000);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void test() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    File tmpCsv = new File(getTempDirectory(), System.nanoTime() + ".csv");
    try (BufferedWriter writer = Files.newBufferedWriter(tmpCsv.toPath())) {
      writer.write(String.join(",", "id", "name", "json_col"));
      writer.newLine();
      writer.write(String.join(",", "1", "Alice", "{\"1\":\"val1\"}"));
      writer.newLine();
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
    BasicH2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();
    OrmRecordExample ret = table.readCsvWithHeader(tmpCsv).get(0);
    assertThat(ret.id).isEqualTo(1);
    assertThat(ret.name).isEqualTo("Alice");
    table.insert(ret);
    table.insertMapIn(RowMap.of("id", "2", "name", "Bob"));

    table.writeCsv(tmpCsv);

    int s = table.readCsvWithHeader(tmpCsv).size();
    assertThat(s).isEqualTo(2);
    List<OrmRecordExample> rows = table.selectAll();
    table.deleteAll();
    table.insert(rows);
  }

  @Test
  void test3() {
    H2Table<Item> table = new BasicH2Table<>(SormTestUtils.createSormWithNewContext(), Item.class);
    String ret =
        table.getReadCsvWithHeaderSql(new File("file.csv"), StandardCharsets.UTF_8, '\t', '"');
    System.out.println(ret);
  }

  @OrmRecord
  public static class Item {
    @CsvColumn("parsedatetime(delivery_date, 'y/MM/d')")
    public LocalDate deliveryDate;

    @CsvColumn("`price/prices`")
    public int price;
  }

  @OrmRecord
  public static class OrmRecordExample {
    private final int id;
    private final String name;
    private final JsonByte jsonCol;

    public OrmRecordExample(int id, String name, JsonByte jsonCol) {
      this.id = id;
      this.name = name;
      this.jsonCol = jsonCol;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public JsonByte getJsonCol() {
      return jsonCol;
    }

    @Override
    public String toString() {
      return "OrmRecordExample [id=" + id + ", name=" + name + ", jsonCol=" + jsonCol + "]";
    }
  }

  public static File getTempDirectory() {
    return new File(System.getProperty("java.io.tmpdir"));
  }
}
