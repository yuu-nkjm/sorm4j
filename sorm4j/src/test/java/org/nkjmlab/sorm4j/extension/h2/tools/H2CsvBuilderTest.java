package org.nkjmlab.sorm4j.extension.h2.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.h2.tools.Csv;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.h2.tools.csv.H2Csv;
import org.nkjmlab.sorm4j.extension.h2.tools.csv.H2CsvReader;

class H2CsvBuilderTest {

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
  void testSetWriteColumnHeader() {
    H2CsvReader csv = H2CsvReader.builder().writeColumnHeader(false).build();
    Csv csvConfig = extractCsvConfig(csv);
    assertThat(csvConfig.getWriteColumnHeader()).isFalse();
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

  private Csv extractCsvConfig(H2CsvReader h2Csv) {
    try {
      java.lang.reflect.Field csvField = H2Csv.class.getDeclaredField("csv");
      csvField.setAccessible(true);
      return (Csv) csvField.get(h2Csv);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to access Csv field from H2Csv", e);
    }
  }
}
