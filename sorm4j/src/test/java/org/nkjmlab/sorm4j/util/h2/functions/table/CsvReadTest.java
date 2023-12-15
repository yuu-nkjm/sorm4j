package org.nkjmlab.sorm4j.util.h2.functions.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils;

class CsvReadTest {

  @Test
  public void testBuilderForCsvWithHeader() {
    File csvFile = new File("test.csv");
    CsvRead csvRead =
        CsvRead.builderForCsvWithHeader(csvFile).charset("UTF-8").fieldSeparator(",").build();
    assertEquals(
        "csvread('"
            + csvFile.getAbsolutePath()
            + "', null, stringdecode('charset=UTF-8 fieldSeparator=,'))",
        csvRead.getSql());
  }

  @Test
  public void testBuilderForCsvWithoutHeaderWithColumns() {
    File csvFile = new File("test.csv");
    List<String> columns = Arrays.asList("col1", "col2", "col3");
    CsvRead csvRead =
        CsvRead.builderForCsvWithoutHeader(csvFile, columns)
            .charset("UTF-8")
            .fieldSeparator(";")
            .build();
    assertEquals(
        "csvread('"
            + csvFile.getAbsolutePath()
            + "', 'col1;col2;col3', stringdecode('charset=UTF-8 fieldSeparator=;'))",
        csvRead.getSql());
  }

  @Test
  public void testBuilderForCsvWithoutHeaderWithColumnsCount() {
    File csvFile = new File("test.csv");
    int columnsCount = 3;
    CsvRead csvRead =
        CsvRead.builderForCsvWithoutHeader(csvFile, columnsCount)
            .charset("UTF-8")
            .fieldSeparator(";")
            .build();
    assertEquals(
        "csvread('"
            + csvFile.getAbsolutePath()
            + "', 'COL_0;COL_1;COL_2', stringdecode('charset=UTF-8 fieldSeparator=;'))",
        csvRead.getSql());
  }

  @Test
  public void testBuilderWithAllOptions() {
    File csvFile = new File("test.csv");
    List<String> columns = Arrays.asList("col1", "col2");
    CsvRead csvRead =
        CsvRead.builder()
            .file(csvFile)
            .columns(columns)
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
        "csvread('"
            + csvFile.getAbsolutePath()
            + "', 'col1;col2', "
            + "stringdecode('"
            + LiteralUtils.escapeJavaString(
                "charset=UTF-8 fieldDelimiter=\" fieldSeparator=; lineComment=# lineSeparator=\n null=\\N preserveWhitespace=true quotedNulls=false writeColumnHeader=false")
            + "'))";
    assertEquals(expectedSql, csvRead.getSql());
  }

  // Additional tests can be added to cover more scenarios and edge cases.
}
