package org.nkjmlab.sorm4j.util.h2.resource_table;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
class TableResourcesTest {

  @TempDir File tempDir;

  private TableResources tableResources;

  @BeforeEach
  void setUp() {
    tableResources = new TableResources(tempDir);
  }

  @Test
  void testDeleteAllCsvExclude() throws IOException {
    Files.createFile(new File(tempDir, "table1.csv").toPath());
    Files.createFile(new File(tempDir, "table2.csv").toPath());
    Files.createFile(new File(tempDir, "table3.csv").toPath());

    tableResources.deleteAllCsvExclude("table2");

    assertThat(new File(tempDir, "table1.csv")).doesNotExist();
    assertThat(new File(tempDir, "table2.csv")).exists();
    assertThat(new File(tempDir, "table3.csv")).doesNotExist();
  }

  @Test
  void testDeleteCsv() throws IOException {
    Files.createFile(new File(tempDir, "table1.csv").toPath());
    Files.createFile(new File(tempDir, "table2.csv").toPath());

    tableResources.deleteCsv("table1", "table2");

    assertThat(new File(tempDir, "table1.csv")).doesNotExist();
    assertThat(new File(tempDir, "table2.csv")).doesNotExist();
  }

  @Test
  void testGetCsvFileWithString() throws IOException {
    Path filePath = new File(tempDir, "testTable.csv").toPath();
    Files.createFile(filePath);
    File result = tableResources.getCsvFile("testTable");
    assertThat(result).isEqualTo(filePath.toFile());
  }

  @Test
  void testGetResourcesDirectory() {
    assertThat(tableResources.getResourcesDirectory()).isEqualTo(tempDir);
  }
}
