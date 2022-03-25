package org.nkjmlab.sorm4j.util.h2.sql;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

@Experimental
public class H2CsvReadSql {

  private final List<String> columns;

  private final String csvReadAndSelectSql;

  public H2CsvReadSql(List<String> columns, String csvReadAndSelectSql) {
    this.columns = columns;
    this.csvReadAndSelectSql = csvReadAndSelectSql;
  }

  public String getCsvReadAndInsertSql(String tableName) {
    return "insert into " + tableName + "(" + String.join(",", columns) + ") "
        + getCsvReadAndSelectSql();
  }

  public String getCsvReadAndSelectSql() {
    return csvReadAndSelectSql;
  }

  public static Builder builder(File csvFile) {
    return new Builder(csvFile);
  }

  @Override
  public String toString() {
    return "H2CsvReadSql [columns=" + columns + ", csvReadAndSelectSql=" + csvReadAndSelectSql
        + "]";
  }

  public static class Builder {
    /**
     * Columns for SQL. null or empty means the all columns.
     */
    private List<String> columns = new ArrayList<>();
    private Map<String, String> aliases = new HashMap<>();

    /**
     * Columns in CSV files. null or empty means using the first row as the header.
     */
    private List<String> csvColumns = new ArrayList<>();
    private final File csvFile;
    private Charset charset = StandardCharsets.UTF_8;
    private String fieldSeparator = "char(" + ((int) ',') + ")";


    public Builder(File csvFile) {
      this.csvFile = csvFile;
    }

    public H2CsvReadSql build() {
      List<String> selectedColumns = new ArrayList<>(columns);

      aliases.entrySet().forEach(en -> {
        int index = selectedColumns.indexOf(en.getKey());
        if (index == -1) {
          throw new IllegalStateException(ParameterizedStringUtils
              .newString("{} is not found in Columns {}", en.getKey(), columns));
        }
        selectedColumns.set(index, en.getValue());
      });

      return new H2CsvReadSql(columns, H2CsvFunctions.getCsvReadAndSelectSql(selectedColumns,
          csvFile, csvColumns, charset, fieldSeparator));
    }

    public Builder setCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder setFieldSeparator(char fieldSeparator) {
      this.fieldSeparator = "char(" + ((int) fieldSeparator) + ")";
      return this;
    }


    public Builder setCharset(String charset) {
      return setCharset(Charset.forName(charset));
    }

    public Builder setTableColumns(List<String> tableColumns) {
      this.columns = new ArrayList<>(tableColumns);
      return this;
    }

    public Builder setTableColumns(String... tableColumns) {
      return setTableColumns(Arrays.asList(tableColumns));
    }

    public Builder setCsvColumns(List<String> csvColumns) {
      this.csvColumns = new ArrayList<>(csvColumns);
      return this;
    }

    public Builder setCsvColumns(String... csvColumns) {
      return setCsvColumns(Arrays.asList(csvColumns));
    }


    public Builder mapCsvColumnToTableColumn(String expression, String column) {
      aliases.put(column, expression + " as " + column);
      return this;
    }

    @Override
    public String toString() {
      return "Builder [columns=" + columns + ", aliases=" + aliases + ", csvColumns=" + csvColumns
          + ", csvFile=" + csvFile + ", charset=" + charset + ", fieldSeparator=" + fieldSeparator
          + "]";
    }


  }

}
