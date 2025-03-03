package org.nkjmlab.sorm4j.util.h2.resource_table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
class SqlResourcesTest {

  @TempDir File tempDir;

  private SqlResources sqlResources;

  @BeforeEach
  void setUp() {
    sqlResources = new SqlResources(tempDir);
  }

  @Test
  void testReadSqlWithString() throws IOException {
    String fileName = "test.sql";
    String sqlContent =
        "SELECT * FROM test_table;\n-- This is a comment\nINSERT INTO test_table (id) VALUES (1);";
    Files.writeString(new File(tempDir, fileName).toPath(), sqlContent);

    String result = sqlResources.readSql(fileName);

    assertThat(result)
        .isEqualToIgnoringWhitespace(
            "SELECT * FROM test_table;\nINSERT INTO test_table (id) VALUES (1);");
  }

  @Test
  void testGetSqlPath() {
    String fileName = "test.sql";
    Path expectedPath = new File(tempDir, fileName).toPath();

    Path result = sqlResources.getSqlPath(fileName);

    assertThat(result).isEqualTo(expectedPath);
  }

  @Test
  void testReadSql_IOException() throws IOException {
    String fileName = "nonexistent.sql";

    assertThatThrownBy(() -> sqlResources.readSql(fileName))
        .isInstanceOf(NoSuchFileException.class);
  }
}
