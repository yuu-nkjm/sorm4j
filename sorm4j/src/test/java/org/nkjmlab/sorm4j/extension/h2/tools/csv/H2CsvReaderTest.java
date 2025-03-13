package org.nkjmlab.sorm4j.extension.h2.tools.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.h2.tools.Csv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.orm.H2SormFactory;
import org.nkjmlab.sorm4j.internal.util.Try;

class H2CsvReaderTest {

  private static final File OUTPUT_CSV_WITH_HEADER_FILE =
      Try.getOrElseNull(() -> File.createTempFile("test_output", ".csv"));

  private Sorm sorm;

  @BeforeEach
  void setUp() {
    this.sorm = H2SormFactory.createTemporalInMemory();
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
    H2CsvWriter h2CsvWriter = H2CsvWriter.builder().build();
    int writtenRows =
        h2CsvWriter.writeCsv(
            sorm.getDataSource(), OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    assertThat(writtenRows).isEqualTo(3);
    assertThat(OUTPUT_CSV_WITH_HEADER_FILE).exists();
  }

  @Test
  void testReadCsvReplacedHeader() {
    H2CsvWriter h2CsvWriter = H2CsvWriter.builder().build();
    h2CsvWriter.writeCsv(sorm.getDataSource(), OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    {
      H2CsvReader h2CsvReader = H2CsvReader.builder().build();
      List<RowMap> rows =
          h2CsvReader.readCsvReplacedHeader(
              OUTPUT_CSV_WITH_HEADER_FILE, new String[] {"NEW_ID", "NEW_NAME"});
      assertThat(rows).hasSize(3);
      assertThat(rows.get(0).get("NEW_ID")).isEqualTo("1");
      assertThat(rows.get(0).get("NEW_NAME")).isEqualTo("Alice");
    }
  }

  @Test
  void testReadCsvWithEmptyFile() throws IOException {
    File emptyFile = File.createTempFile("empty", ".csv");
    emptyFile.deleteOnExit();
    H2CsvReader h2CsvReader = H2CsvReader.builder().build();
    List<RowMap> rows = h2CsvReader.readCsvWithHeader(emptyFile);
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
    H2CsvReader h2CsvReader = H2CsvReader.builder().build();
    List<RowMap> rows = h2CsvReader.readCsvWithHeader(encodedFile, StandardCharsets.ISO_8859_1);
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
      H2CsvReader h2CsvReader = H2CsvReader.builder().build();
      List<RowMap> rows = h2CsvReader.readCsvWithHeader(reader);
      assertThat(rows.get(0).containsKey("name"));
    }
    try (Reader reader = new FileReader(bomFile)) {
      List<RowMap> rows =
          H2CsvReader.builder()
              .build()
              .readCsvReplacedHeader(bomFile, new String[] {"id", "guest_name"});
      assertThat(rows.get(0).containsKey("guest_name"));
    }
  }

  @Test
  void testReadCsvWithNonExistentFile() {
    File nonExistentFile = new File("non_existent_file.csv");
    H2CsvReader h2CsvReader = H2CsvReader.builder().build();
    assertThatThrownBy(() -> h2CsvReader.readCsvWithHeader(nonExistentFile))
        .isInstanceOf(Exception.class);
  }

  @Test
  void testDefaultBuilderValues() {
    H2CsvReader csv = H2CsvReader.builder().build();
    assertThat(csv).isNotNull();
  }

  @Test
  void testSetCaseSensitiveColumnNames() {
    H2CsvReader csv = H2CsvReader.builder().caseSensitiveColumnNames(true).build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getCaseSensitiveColumnNames());
  }

  @Test
  void testSetFieldSeparatorWrite() {
    H2CsvReader csv = H2CsvReader.builder().fieldSeparatorWrite(";").build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getFieldSeparatorWrite()).isEqualTo(";");
  }

  @Test
  void testSetFieldSeparatorRead() {
    H2CsvReader csv = H2CsvReader.builder().fieldSeparatorRead('|').build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getFieldSeparatorRead()).isEqualTo('|');
  }

  @Test
  void testSetLineCommentCharacter() {
    H2CsvReader csv = H2CsvReader.builder().lineCommentCharacter('#').build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getLineCommentCharacter()).isEqualTo('#');
  }

  @Test
  void testSetFieldDelimiter() {
    H2CsvReader csv = H2CsvReader.builder().fieldDelimiter('"').build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getFieldDelimiter()).isEqualTo('"');
  }

  @Test
  void testSetEscapeCharacter() {
    H2CsvReader csv = H2CsvReader.builder().escapeCharacter('\\').build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getEscapeCharacter()).isEqualTo('\\');
  }

  @Test
  void testSetLineSeparator() {
    H2CsvReader csv = H2CsvReader.builder().lineSeparator("\r\n").build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getLineSeparator()).isEqualTo("\r\n");
  }

  @Test
  void testSetQuotedNulls() {
    H2CsvReader csv = H2CsvReader.builder().quotedNulls(true).build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.isQuotedNulls()).isTrue();
  }

  @Test
  void testSetNullString() {
    H2CsvReader csv = H2CsvReader.builder().nullString("NULL").build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getNullString()).isEqualTo("NULL");
  }

  @Test
  void testSetPreserveWhitespace() {
    H2CsvReader csv = H2CsvReader.builder().preserveWhitespace(true).build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getPreserveWhitespace()).isTrue();
  }

  @Test
  void testMultipleConfigurations() {
    H2CsvReader csv =
        H2CsvReader.builder()
            .caseSensitiveColumnNames(true)
            .fieldSeparatorWrite(";")
            .fieldSeparatorRead('|')
            .lineCommentCharacter('#')
            .fieldDelimiter('"')
            .escapeCharacter('\\')
            .lineSeparator("\r\n")
            .quotedNulls(true)
            .nullString("NULL")
            .preserveWhitespace(true)
            .writeColumnHeader(false)
            .build();

    Csv csvConfig = extractCsvConfig(csv);

    assertThat(csvConfig.getCaseSensitiveColumnNames()).isTrue();
    assertThat(csvConfig.getFieldSeparatorWrite()).isEqualTo(";");
    assertThat(csvConfig.getFieldSeparatorRead()).isEqualTo('|');
    assertThat(csvConfig.getLineCommentCharacter()).isEqualTo('#');
    assertThat(csvConfig.getFieldDelimiter()).isEqualTo('"');
    assertThat(csvConfig.getEscapeCharacter()).isEqualTo('\\');
    assertThat(csvConfig.getLineSeparator()).isEqualTo("\r\n");
    assertThat(csvConfig.isQuotedNulls()).isTrue();
    assertThat(csvConfig.getNullString()).isEqualTo("NULL");
    assertThat(csvConfig.getPreserveWhitespace()).isTrue();
    assertThat(csvConfig.getWriteColumnHeader()).isFalse();
  }

  @Test
  void testSetWriteColumnHeader() {
    H2CsvReader csv = H2CsvReader.builder().writeColumnHeader(false).build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getWriteColumnHeader()).isFalse();
  }

  private Csv extractCsvConfig(H2CsvReader h2Csv) {
    try {
      java.lang.reflect.Field csvField = h2Csv.getClass().getDeclaredField("csv");
      csvField.setAccessible(true);
      return (Csv) csvField.get(h2Csv);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to access Csv field from H2Csv", e);
    }
  }
}
