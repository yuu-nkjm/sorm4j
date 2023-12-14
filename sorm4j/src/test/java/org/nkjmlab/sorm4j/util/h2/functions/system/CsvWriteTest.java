package org.nkjmlab.sorm4j.util.h2.functions.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils;

class CsvWriteTest {

  @Test
  public void testBuilderWithFileAndQuery() {
    File csvFile = new File("output.csv");
    CsvWrite csvWrite =
        CsvWrite.builder(csvFile).query("SELECT * FROM table").charset("UTF-8").build();
    assertEquals(
        "csvwrite('"
            + csvFile.getAbsolutePath()
            + "', 'SELECT * FROM table', stringdecode('charset=UTF-8'))",
        csvWrite.getSql());
  }

  @Test
  public void testBuilderWithFileOnly() {
    File csvFile = new File("output.csv");
    CsvWrite csvWrite = CsvWrite.builder(csvFile).query("SELECT * FROM table").build();
    assertEquals(
        "csvwrite('" + csvFile.getAbsolutePath() + "', 'SELECT * FROM table', null)",
        csvWrite.getSql());
  }

  @Test
  public void testBuilderWithAllOptions() {
    File csvFile = new File("output.csv");
    CsvWrite csvWrite =
        CsvWrite.builder()
            .file(csvFile)
            .query("SELECT * FROM table")
            .charset("UTF-8")
            .fieldDelimiter("\"")
            .fieldSeparator(";")
            .lineComment("#")
            .lineSeparator("\n")
            .nullString("\\N")
            .quotedNulls("false")
            .preserveWhitespace("true")
            .writeColumnHeader("false")
            .build();
    String expectedSql =
        "csvwrite('"
            + csvFile.getAbsolutePath()
            + "', 'SELECT * FROM table', stringdecode('"
            + LiteralUtils.escapeJavaString(
                "charset=UTF-8 fieldDelimiter=\" fieldSeparator=; lineComment=# lineSeparator=\n null=\\N preserveWhitespace=true quotedNulls=false writeColumnHeader=false")
            + "'))";
    assertEquals(expectedSql, csvWrite.getSql());
  }

  // Additional tests can be added to cover more scenarios and edge cases.
}
