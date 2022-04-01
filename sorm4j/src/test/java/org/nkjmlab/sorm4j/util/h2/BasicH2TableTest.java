package org.nkjmlab.sorm4j.util.h2;

import static org.assertj.core.api.Assertions.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class BasicH2TableTest {

  @Test
  void test() {
    Sorm sorm = SormTestUtils.createSormWithNewContext();
    File tmpCsv = new File(getTempDirectory(), System.nanoTime() + ".csv");
    try (BufferedWriter writer = Files.newBufferedWriter(tmpCsv.toPath())) {
      writer.write(String.join(",", "id", "name"));
      writer.newLine();
      writer.write(String.join(",", "1", "Alice"));
      writer.newLine();
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
    H2Table<OrmRecordExample> table = new BasicH2Table<>(sorm, OrmRecordExample.class);
    table.createTableIfNotExists();
    OrmRecordExample ret = table.readCsvWithHeader(tmpCsv).get(0);
    assertThat(ret.id).isEqualTo(1);
    assertThat(ret.name).isEqualTo("Alice");
    table.insert(ret);
    table.insertMapIn(RowMap.of("id", "2", "name", "Bob"));

    table.writeCsv(tmpCsv);
    int s = table.readCsvWithHeader(tmpCsv).size();
    assertThat(s).isEqualTo(2);
  }

  @OrmRecord
  public static class OrmRecordExample {
    private final int id;
    private final String name;

    public OrmRecordExample(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  public static File getTempDirectory() {
    return new File(System.getProperty("java.io.tmpdir"));
  }

}
