package org.nkjmlab.sorm4j.extension.h2.functions.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

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
    CsvWrite csvWrite =
        CsvWrite.builder(csvFile).query("SELECT * FROM table where id='sorm'").build();
    assertEquals(
        "csvwrite('"
            + csvFile.getAbsolutePath()
            + "', 'SELECT * FROM table where id=''sorm''', null)",
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
            .escape("\\")
            .fieldDelimiter("\"")
            .fieldSeparator(";")
            .lineComment("#")
            .lineSeparator("\n")
            .nullString("\\N")
            .quotedNulls(false)
            .preserveWhitespace(true)
            .writeColumnHeader(false)
            .caseSensitiveColumnNames(false)
            .build();
    String expectedSql =
        "csvwrite('"
            + csvFile.getAbsolutePath()
            + "', 'SELECT * FROM table', stringdecode('"
            + SqlStringUtils.escapeJavaString(
                "caseSensitiveColumnNames=false charset=UTF-8 escape=\\ fieldDelimiter=\" fieldSeparator=; lineComment=# lineSeparator=\n null=\\N preserveWhitespace=true quotedNulls=false writeColumnHeader=false")
            + "'))";
    assertEquals(expectedSql, csvWrite.getSql());
  }

  @Test
  public void testEscapeMethod() {
    CsvWrite.Builder builder = CsvWrite.builder(new File("/test"));
    builder.escape("'");
    builder.query("select * from table");
    builder.caseSensitiveColumnNames(true);
    assertThat(builder.build().toString()).contains("escape=\\'");
    assertThat(builder.build().toString()).contains("caseSensitiveColumnNames=true");
  }
}
