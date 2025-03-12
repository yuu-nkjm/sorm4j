package org.nkjmlab.sorm4j.extension.h2;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.extension.datatype.container.JsonByte;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTable;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.annotation.OrmRecordCompatibleConstructor;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class H2DefinedTableTest {

  @Test
  void test3() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    H2DefinedTable<Example> table = H2DefinedTable.of(sorm, Example.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();
    table.getOrm();
    table.getTableName();
    table.getTableDefinition();
    table.getValueType();
    table.insert(new Example(0, "name"));
  }

  public record Example(int id, String name) {}

  @Test
  void test2() throws InterruptedException {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    H2DefinedTable<OrmRecordExample> table = H2DefinedTable.of(sorm, OrmRecordExample.class);
    table.dropTableIfExists();
    table.createTableIfNotExists();

    try (Connection con = sorm.openJdbcConnection()) {
      PreparedStatement ps =
          con.prepareStatement("insert into ORM_RECORD_EXAMPLES VALUES(?,?,? format json)");
      ps.setInt(1, 3);
      ps.setString(2, "C");
      ps.setObject(3, "{\"3\":\"val1\"}".getBytes());
      ps.execute();
    } catch (SQLException e) {
      throw Try.rethrow(e);
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
    H2DefinedTable<OrmRecordExample> table = H2DefinedTable.of(sorm, OrmRecordExample.class);
    {
      DefinedTable.of(sorm, OrmRecordExample.class);
    }
    table.dropTableIfExists();
    table.createTableIfNotExists(CsvRead.builderForCsvWithHeader(tmpCsv).build());
    OrmRecordExample ret = table.selectAll().get(0);
    assertThat(ret.id).isEqualTo(1);
    assertThat(ret.name).isEqualTo("Alice");
    table.insert(ret);
    table.insertMapIn(RowMap.of("id", "2", "name", "Bob"));

    table.writeCsv(tmpCsv);

    List<OrmRecordExample> rows = table.selectAll();
    table.deleteAll();
    table.insert(rows);
  }

  public static class OrmRecordExample {
    private final int id;
    private final String name;
    private final JsonByte jsonCol;

    @OrmRecordCompatibleConstructor
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
