package org.nkjmlab.sorm4j.extension.h2.tools.csv;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.tools.Csv;
import org.nkjmlab.sorm4j.internal.util.Try;

public class H2CsvWriter {
  private final Csv csv;

  private H2CsvWriter(Csv csv) {
    this.csv = csv;
  }

  /** See {@link Csv#write(Connection, String, String, String)}. */
  public int writeCsv(DataSource dataSource, File outputFileName, String sql, Charset charset) {
    try (Connection conn = dataSource.getConnection()) {
      return writeCsv(conn, outputFileName, sql, charset);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  public int writeCsv(Connection conn, File outputFileName, String sql, Charset charset) {
    try {
      return csv.write(conn, outputFileName.toString(), sql, charset.name());
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#write(Connection, String, String, String)}. */
  public int writeCsv(DataSource dataSource, File outputFileName, String sql) {
    return writeCsv(dataSource, outputFileName, sql, StandardCharsets.UTF_8);
  }

  public static H2CsvWriter.Builder builder() {
    return new H2CsvWriter.Builder();
  }

  public static class Builder extends H2Csv.Builder<H2CsvWriter, H2CsvWriter.Builder> {
    @Override
    public H2CsvWriter build() {
      return new H2CsvWriter(csv);
    }
  }
}
