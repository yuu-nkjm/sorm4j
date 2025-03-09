package org.nkjmlab.sorm4j.extension.h2.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.h2.tools.Csv;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.container.RowMap;
import org.nkjmlab.sorm4j.extension.h2.orm.H2SormFactory;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;

public class H2Csv {
  private final Csv csv;

  /**
   * Creates an instance of {@code H2Csv} with the given {@code Csv} configuration.
   *
   * <p>It is recommended to use the builder pattern for easier instantiation:
   *
   * <pre>
   * H2Csv csvConfig = H2Csv.builder().build();
   * </pre>
   *
   * @param csv the CSV configuration
   */
  private H2Csv(Csv csv) {
    this.csv = csv;
  }

  /** See {@link Csv#write(Connection, String, String, String)}. */
  public int writeCsv(Sorm sorm, File outputFileName, String sql, Charset charset) {
    try (Connection conn = sorm.openJdbcConnection()) {
      return csv.write(conn, outputFileName.toString(), sql, charset.name());
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#write(Connection, String, String, String)}. */
  public int writeCsv(Sorm sorm, File outputFileName, String sql) {
    return writeCsv(sorm, outputFileName, sql, StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(File inputFileName) {
    return readCsvWithHeader(inputFileName, StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(Sorm sorm, File inputFileName) {
    return readCsvWithHeader(sorm, inputFileName, StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(File inputFileName, Charset charset) {
    return readCsvWithHeader(H2SormFactory.createTemporalInMemory(), inputFileName, charset);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(Sorm sorm, File inputFileName, Charset charset) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvWithHeader(sorm, in);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  private static Reader newBufferedReader(File inputFileName, Charset charset) throws IOException {
    return FileUtils.newBufferedReader(
        inputFileName.toString(), charset != null ? charset : StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithHeader(Reader in) {
    return readCsvWithHeader(H2SormFactory.createTemporalInMemory(), in);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithHeader(Sorm sorm, Reader in) {
    try (ResultSet rs = csv.read(in, null)) {
      return traverseAndMap(sorm, rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithoutHeader(File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvWithoutHeader(H2SormFactory.createTemporalInMemory(), in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithoutHeader(
      Sorm sorm, File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvWithoutHeader(sorm, in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithoutHeader(File inputFileName, String[] colNames) {
    return readCsvWithoutHeader(inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithhoutHeader(Sorm sorm, File inputFileName, String[] colNames) {
    return readCsvWithoutHeader(sorm, inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithoutHeader(Reader in, String[] colNames) {
    return readCsvWithoutHeader(H2SormFactory.createTemporalInMemory(), in, colNames);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithoutHeader(Sorm sorm, Reader in, String[] colNames) {
    try (ResultSet rs = csv.read(in, colNames)) {
      return traverseAndMap(sorm, rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(
      File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvReplacedHeader(H2SormFactory.createTemporalInMemory(), in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(
      Sorm sorm, File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvReplacedHeader(sorm, in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvReplacedHeader(Reader in, String[] colNames) {
    return readCsvReplacedHeader(H2SormFactory.createTemporalInMemory(), in, colNames);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvReplacedHeader(Sorm sorm, Reader in, String[] colNames) {
    try (ResultSet rs = csv.read(new HeaderSkippingReader(in), colNames)) {
      return traverseAndMap(sorm, rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(File inputFileName, String[] colNames) {
    return readCsvReplacedHeader(inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(Sorm sorm, File inputFileName, String[] colNames) {
    return readCsvReplacedHeader(sorm, inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link ResultSetTraverser#traverseAndMap(ResultSet)}. */
  private List<RowMap> traverseAndMap(Sorm sorm, ResultSet rs) throws SQLException {
    ResultSetTraverser<List<RowMap>> t = sorm.getResultSetTraverser(RowMap.class);
    return t.traverseAndMap(rs);
  }

  /**
   * Returns a new {@link H2Csv.Builder} instance for constructing an {@link H2Csv} object.
   *
   * <p>This method provides a convenient way to create an instance using the builder pattern:
   *
   * <pre>
   * H2Csv csvConfig = H2Csv.builder()
   *     .fieldSeparatorWrite(";")
   *     .caseSensitiveColumnNames(true)
   *     .build();
   * </pre>
   *
   * @return a new {@code H2Csv.Builder} instance
   */
  public static H2Csv.Builder builder() {
    return new Builder();
  }

  /**
   * A builder class for creating an {@link H2Csv} instance with customized settings.
   *
   * <p>This class allows for a fluent API to configure various CSV properties before building the
   * final {@link H2Csv} object.
   *
   * <pre>
   * H2Csv csvConfig = new H2Csv.Builder()
   *     .fieldSeparatorWrite(";")
   *     .caseSensitiveColumnNames(true)
   *     .build();
   * </pre>
   */
  public static class Builder {
    private final Csv csv = new Csv();

    public Builder caseSensitiveColumnNames(boolean caseSensitiveColumnNames) {
      csv.setCaseSensitiveColumnNames(caseSensitiveColumnNames);
      return this;
    }

    public Builder fieldSeparatorWrite(String fieldSeparatorWrite) {
      csv.setFieldSeparatorWrite(fieldSeparatorWrite);
      return this;
    }

    public Builder fieldSeparatorRead(char fieldSeparatorRead) {
      csv.setFieldSeparatorRead(fieldSeparatorRead);
      return this;
    }

    public Builder lineCommentCharacter(char lineComment) {
      csv.setLineCommentCharacter(lineComment);
      return this;
    }

    public Builder fieldDelimiter(char fieldDelimiter) {
      csv.setFieldDelimiter(fieldDelimiter);
      return this;
    }

    public Builder escapeCharacter(char escapeCharacter) {
      csv.setEscapeCharacter(escapeCharacter);
      return this;
    }

    public Builder lineSeparator(String lineSeparator) {
      csv.setLineSeparator(lineSeparator);
      return this;
    }

    public Builder quotedNulls(boolean quotedNulls) {
      csv.setQuotedNulls(quotedNulls);
      return this;
    }

    public Builder nullString(String nullString) {
      csv.setNullString(nullString);
      return this;
    }

    public Builder preserveWhitespace(boolean preserveWhitespace) {
      csv.setPreserveWhitespace(preserveWhitespace);
      return this;
    }

    public Builder writeColumnHeader(boolean writeColumnHeader) {
      csv.setWriteColumnHeader(writeColumnHeader);
      return this;
    }

    public H2Csv build() {
      return new H2Csv(csv);
    }
  }

  public static class HeaderSkippingReader extends Reader {
    private final BufferedReader reader;
    private final List<String> columns;
    private static final int bs = 8192;

    private HeaderSkippingReader(Reader input) throws IOException {
      this.reader = new BufferedReader(input, bs);
      String firstLine = reader.readLine();
      if (firstLine == null) {
        columns = Collections.emptyList();
      } else {
        if (firstLine.startsWith("\uFEFF")) {
          firstLine = firstLine.substring(1);
        }
        this.columns = Arrays.asList(firstLine.split(","));
      }
      reader.mark(bs);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
      return reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
      reader.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
      reader.reset();
    }

    @Override
    public boolean markSupported() {
      return reader.markSupported();
    }

    public List<String> getColumns() {
      return columns;
    }
  }
}
