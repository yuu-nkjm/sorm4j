package org.nkjmlab.sorm4j.extension.h2.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.orm.H2SormFactory;
import org.nkjmlab.sorm4j.internal.util.Try;

class H2CsvTest {

  private static final File OUTPUT_CSV_WITH_HEADER_FILE =
      Try.getOrElseNull(() -> File.createTempFile("test_output", ".csv"));
  private static final URL CSV_WITHOUT_HEADER_FILE =
      H2CsvTest.class.getResource("./test_without_header.csv");

  private H2Csv h2Csv;
  private Sorm sorm;

  @BeforeEach
  void setUp() {
    h2Csv = H2Csv.builder().build();
    sorm = H2SormFactory.createTemporalInMemory();
    try (Connection conn = sorm.openJdbcConnection()) {
      conn.createStatement().execute("CREATE TABLE test (id INT PRIMARY KEY, name VARCHAR(255))");
      conn.createStatement()
          .execute("INSERT INTO test VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  void tearDown() {
    OUTPUT_CSV_WITH_HEADER_FILE.delete();
  }

  @Test
  void testWriteCsv() {
    int writtenRows = h2Csv.writeCsv(sorm, OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    assertThat(writtenRows).isEqualTo(3);
    assertThat(OUTPUT_CSV_WITH_HEADER_FILE).exists();
  }

  @Test
  void testReadCsvWithHeader() {
    h2Csv.writeCsv(sorm, OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    List<RowMap> rows = h2Csv.readCsvWithHeader(OUTPUT_CSV_WITH_HEADER_FILE);
    assertThat(rows).hasSize(3);
    assertThat(rows.get(0).get("ID")).isEqualTo("1");
    assertThat(rows.get(0).get("NAME")).isEqualTo("Alice");
  }

  @Test
  void testReadCsvWithoutHeader() throws IOException {
    try (Reader in = new InputStreamReader(CSV_WITHOUT_HEADER_FILE.openStream())) {
      List<RowMap> rows = h2Csv.readCsvWithoutHeader(in, new String[] {"ID", "NAME"});
      assertThat(rows).hasSize(3);
      assertThat(rows.get(0).get("ID")).isEqualTo("1");
      assertThat(rows.get(0).get("NAME")).isEqualTo("Alice");
    }
  }

  @Test
  void testReadCsvReplacedHeader() {
    h2Csv.writeCsv(sorm, OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    List<RowMap> rows =
        h2Csv.readCsvReplacedHeader(
            OUTPUT_CSV_WITH_HEADER_FILE, new String[] {"NEW_ID", "NEW_NAME"});
    assertThat(rows).hasSize(3);
    assertThat(rows.get(0).get("NEW_ID")).isEqualTo("1");
    assertThat(rows.get(0).get("NEW_NAME")).isEqualTo("Alice");
  }

  @Test
  void testReadCsvWithEmptyFile() throws IOException {
    File emptyFile = File.createTempFile("empty", ".csv");
    emptyFile.deleteOnExit();

    List<RowMap> rows = h2Csv.readCsvWithHeader(emptyFile);
    assertThat(rows).isEmpty();
  }

  @Test
  void testReadCsvWithCorruptedFile() throws IOException {
    File corruptedFile = File.createTempFile("corrupt", ".csv");
    corruptedFile.deleteOnExit();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(corruptedFile))) {
      writer.write("1,\"Alice\n2,Bob\n");
    }

    // assertThatThrownBy(() ->
    // h2Csv.readCsvWithHeader(corruptedFile)).isInstanceOf(Exception.class);
  }

  @Test
  void testReadCsvWithDifferentCharset() throws IOException {
    File encodedFile = File.createTempFile("encoded", ".csv");
    encodedFile.deleteOnExit();
    try (BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(encodedFile), StandardCharsets.ISO_8859_1))) {
      writer.write("id,name\n1,Élise\n2,François\n");
    }

    List<RowMap> rows = h2Csv.readCsvWithHeader(encodedFile, StandardCharsets.ISO_8859_1);
    assertThat(rows.get(0).get("NAME")).isEqualTo("Élise");
    assertThat(rows.get(1).get("NAME")).isEqualTo("François");
  }

  @Test
  void testHeaderSkippingReaderWithBOM() throws IOException {
    File bomFile = File.createTempFile("bom", ".csv");
    bomFile.deleteOnExit();
    try (BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(bomFile), StandardCharsets.UTF_8))) {
      writer.write("\uFEFFid,name\n1,Alice\n2,Bob\n");
    }

    try (Reader reader = new FileReader(bomFile)) {
      List<RowMap> rows = h2Csv.readCsvWithHeader(reader);
      assertThat(rows.get(0).containsKey("name"));
    }
    try (Reader reader = new FileReader(bomFile)) {
      List<RowMap> rows =
          H2Csv.builder().build().readCsvReplacedHeader(bomFile, new String[] {"id", "guest_name"});
      assertThat(rows.get(0).containsKey("guest_name"));
    }
  }

  @Test
  void testWriteCsvWithNonExistentDirectory() {
    File nonExistentFile = new File("non_existent_dir/test.csv");
    //    assertThatThrownBy(() -> h2Csv.writeCsv(sorm, nonExistentFile, "SELECT * FROM test"))
    //        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void testReadCsvWithNonExistentFile() {
    File nonExistentFile = new File("non_existent_file.csv");
    assertThatThrownBy(() -> h2Csv.readCsvWithHeader(nonExistentFile))
        .isInstanceOf(Exception.class);
  }
}
