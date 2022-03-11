package org.nkjmlab.sorm4j.util.h2.sql;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringUtils;

@Experimental
public class H2CsvReadSql {
  public static Builder builder(File csvFile) {
    return new Builder(csvFile);
  }

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
    private String fieldSeparator = ",";


    public Builder(File csvFile) {
      this.csvFile = csvFile;
    }

    public H2CsvReadSql build() {
      List<String> columnsWithAlias = new ArrayList<>(columns);

      aliases.entrySet().forEach(en -> {
        int index = columnsWithAlias.indexOf(en.getKey());
        if (index == -1) {
          throw new IllegalStateException(ParameterizedStringUtils
              .newString("{} is not found in Columns {}", en.getKey(), columns));
        }
        columnsWithAlias.set(index, en.getValue());
      });

      return new H2CsvReadSql(columns, H2CsvFunctions.getCsvReadAndSelectSql(columnsWithAlias,
          csvFile, csvColumns, charset, fieldSeparator));
    }

    public Builder setCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder setFieldSeparator(String fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }


    public Builder setCharset(String charset) {
      return setCharset(Charset.forName(charset));
    }

    public Builder setColumns(List<String> columns) {
      this.columns = new ArrayList<>(columns);
      return this;
    }

    public Builder setDateTimePatternToColumns(String dateTimePattern, List<String> columns) {
      columns.forEach(column -> aliases.put(column,
          "parsedatetime(`" + column + "`,'" + dateTimePattern + "') as " + column));
      return this;
    }

    public Builder setExpressionToColumn(String expression, String column) {
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
