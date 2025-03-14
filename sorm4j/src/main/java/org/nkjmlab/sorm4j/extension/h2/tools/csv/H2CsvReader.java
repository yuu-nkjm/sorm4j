package org.nkjmlab.sorm4j.extension.h2.tools.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.h2.tools.Csv;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.container.RowMap;
import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.extension.h2.orm.H2SormFactory;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.util.function.exception.Try;

public class H2CsvReader implements H2Csv {
  private final Csv csv;
  private boolean used = false;

  H2CsvReader(Csv csv) {
    this.csv = csv;
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(File inputFileName) {
    return readCsvWithHeader(inputFileName, StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithHeader(File inputFileName, Charset charset) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvWithHeader(in);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  private static Reader newBufferedReader(File inputFileName, Charset charset) throws IOException {
    return Files.newBufferedReader(
        inputFileName.toPath(), charset != null ? charset : StandardCharsets.UTF_8);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithHeader(Reader in) {
    try (ResultSet rs = readCsv(in, null)) {
      return traverseAndMap(rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  private ResultSet readCsv(Reader in, String[] colNames) throws IOException {
    if (used) {
      throw new SormException("This H2Csv instance has already been used and cannot be reused.");
    }
    used = true;
    return csv.read(in, colNames);
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithoutHeader(File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvWithoutHeader(in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvWithoutHeader(File inputFileName, String[] colNames) {
    return readCsvWithoutHeader(inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvWithoutHeader(Reader in, String[] colNames) {
    try (ResultSet rs = readCsv(in, colNames)) {
      return traverseAndMap(rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(
      File inputFileName, Charset charset, String[] colNames) {
    try (Reader in = newBufferedReader(inputFileName, charset)) {
      return readCsvReplacedHeader(in, colNames);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(Reader, String[])}. */
  public List<RowMap> readCsvReplacedHeader(Reader in, String[] colNames) {
    try (ResultSet rs = csv.read(toHeaderSkippingReader(in), colNames)) {
      return traverseAndMap(rs);
    } catch (IOException | SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** See {@link Csv#read(String, String[], String)}. */
  public List<RowMap> readCsvReplacedHeader(File inputFileName, String[] colNames) {
    return readCsvReplacedHeader(inputFileName, StandardCharsets.UTF_8, colNames);
  }

  /** See {@link ResultSetTraverser#traverseAndMap(ResultSet)}. */
  private List<RowMap> traverseAndMap(ResultSet rs) throws SQLException {
    Sorm sorm = H2SormFactory.createTemporalInMemory();
    ResultSetTraverser<List<RowMap>> t = sorm.getResultSetTraverser(RowMap.class);
    return t.traverseAndMap(rs);
  }

  public static H2CsvReader.Builder builder() {
    return new H2CsvReader.Builder();
  }

  private static Reader toHeaderSkippingReader(Reader input) throws IOException {
    final int bs = 8192;
    BufferedReader reader = new BufferedReader(input, bs);
    reader.readLine();
    reader.mark(bs);
    return reader;
  }

  public static class Builder extends H2Csv.Builder<H2CsvReader, H2CsvReader.Builder> {

    @Override
    public H2CsvReader build() {
      return new H2CsvReader(csv);
    }
  }
}
