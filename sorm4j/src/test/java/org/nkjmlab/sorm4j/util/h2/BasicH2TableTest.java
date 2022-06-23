package org.nkjmlab.sorm4j.util.h2;

import static org.assertj.core.api.Assertions.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.test.common.H2ServerUtils;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;
import org.nkjmlab.sorm4j.util.h2.datatype.Json;

class BasicH2TableTest {

  void test2() {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    H2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();

    try (Connection con = sorm.getJdbcConnection()) {
      PreparedStatement ps = con.prepareStatement("insert into ORM_RECORD_EXAMPLES VALUES(?,?,?)");
      ps.setInt(1, 3);
      ps.setString(2, "C");
      ps.setObject(3, "{\"3\":\"val1\"}".getBytes());
      ps.execute();
      H2ServerUtils.openBrowser(con, true);
      Thread.sleep(1000000);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
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
    H2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
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

  @OrmRecord
  public static class OrmRecordExample {
    private final int id;
    private final String name;
    private final Json jsonCol;

    public OrmRecordExample(int id, String name, Json jsonCol) {
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

    public Json getJsonCol() {
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
