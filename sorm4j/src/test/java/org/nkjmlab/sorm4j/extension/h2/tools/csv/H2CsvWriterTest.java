package org.nkjmlab.sorm4j.extension.h2.tools.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

class H2CsvWriterTest {

  private static final File OUTPUT_CSV_WITH_HEADER_FILE =
      Try.getOrElseNull(() -> File.createTempFile("test_output", ".csv"));

  private static final File CSV_WITHOUT_HEADER_FILE =
      new File(H2CsvReaderTest.class.getResource("./test_without_header.csv").getFile());

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
  void testReadCsvWithHeader() {
    H2CsvWriter h2CsvWriter = H2CsvWriter.builder().build();
    h2CsvWriter.writeCsv(sorm.getDataSource(), OUTPUT_CSV_WITH_HEADER_FILE, "SELECT * FROM test");
    H2CsvReader h2CsvReader = H2CsvReader.builder().build();
    {
      List<RowMap> rows = h2CsvReader.readCsvWithHeader(OUTPUT_CSV_WITH_HEADER_FILE);
      assertThat(rows).hasSize(3);
      assertThat(rows.get(0).get("ID")).isEqualTo("1");
      assertThat(rows.get(0).get("NAME")).isEqualTo("Alice");
    }
    {
      assertThatException()
          .isThrownBy(() -> h2CsvReader.readCsvWithHeader(OUTPUT_CSV_WITH_HEADER_FILE));
    }
  }

  @Test
  void testReadCsvWithoutHeader() throws IOException {
    {
      H2CsvReader h2CsvReader = H2CsvReader.builder().build();
      List<RowMap> rows =
          h2CsvReader.readCsvWithoutHeader(CSV_WITHOUT_HEADER_FILE, new String[] {"ID", "NAME"});
      assertThat(rows).hasSize(3);
      assertThat(rows.get(0).get("ID")).isEqualTo("1");
      assertThat(rows.get(0).get("NAME")).isEqualTo("Alice");
    }
    try (Reader in = new InputStreamReader(CSV_WITHOUT_HEADER_FILE.toURI().toURL().openStream())) {
      H2CsvReader h2CsvReader = H2CsvReader.builder().build();
      List<RowMap> rows = h2CsvReader.readCsvWithoutHeader(in, new String[] {"ID", "NAME"});
      assertThat(rows).hasSize(3);
      assertThat(rows.get(0).get("ID")).isEqualTo("1");
      assertThat(rows.get(0).get("NAME")).isEqualTo("Alice");
    }
  }
}
