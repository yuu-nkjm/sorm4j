package org.nkjmlab.sorm4j.util.h2.functions.table;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.internal.util.StringCache;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class SelectCsvReadSql {

  private final List<String> columns;

  private final String csvReadAndSelectSql;

  private SelectCsvReadSql(List<String> columns, String csvReadAndSelectSql) {
    this.columns = columns;
    this.csvReadAndSelectSql = csvReadAndSelectSql;
  }

  public String getCsvReadAndInsertSql(String tableName) {
    return "insert into "
        + tableName
        + "("
        + String.join(",", columns)
        + ") "
        + getCsvReadAndSelectSql();
  }

  public String getCsvReadAndSelectSql() {
    return csvReadAndSelectSql;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(File csvFile, Class<?> valueType) {
    return new Builder(csvFile, valueType);
  }

  public static Builder builder(File csvFile) {
    return new Builder(csvFile);
  }

  public static class Builder {
    /** Columns for SQL. null or empty means the all columns. */
    private List<String> columns = new ArrayList<>();

    private Map<String, String> aliases = new HashMap<>();

    /** Columns in CSV files. null or empty means using the first row as the header. */
    private List<String> csvColumns = new ArrayList<>();

    private File csvFile;
    private Charset charset = StandardCharsets.UTF_8;
    private Character fieldSeparator = ',';
    private Character fieldDelimiter = null;

    public Builder(File csvFile) {
      csvFile(csvFile);
    }

    public Builder(File csvFile, Class<?> valueType) {
      this(csvFile);
      valueType(valueType);
    }

    private Builder() {}

    public SelectCsvReadSql build() {
      List<String> selectedColumns = new ArrayList<>(columns);

      aliases
          .entrySet()
          .forEach(
              en -> {
                int index = selectedColumns.indexOf(en.getKey());
                if (index == -1) {
                  Object[] params = {en.getKey(), columns};
                  throw new IllegalStateException(
                      ParameterizedStringFormatter.LENGTH_256.format(
                          "{} is not found in Columns {}", params));
                }
                selectedColumns.set(index, en.getValue());
              });

      return new SelectCsvReadSql(
          columns,
          getCsvReadAndSelectSql(
              selectedColumns, csvFile, csvColumns, charset, fieldSeparator, fieldDelimiter));
    }

    public Builder csvFile(File csvFile) {
      this.csvFile = csvFile;
      return this;
    }

    public Builder charset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder fieldSeparator(Character fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }

    public Builder fieldDelimiter(Character fieldDelimiter) {
      this.fieldDelimiter = fieldDelimiter;
      return this;
    }

    public Builder charset(String charset) {
      return charset(Charset.forName(charset));
    }

    public Builder tableColumns(List<String> tableColumns) {
      this.columns = new ArrayList<>(tableColumns);
      return this;
    }

    public Builder tableColumns(String... tableColumns) {
      return tableColumns(Arrays.asList(tableColumns));
    }

    public Builder csvColumns(List<String> csvColumns) {
      this.csvColumns = new ArrayList<>(csvColumns);
      return this;
    }

    public Builder csvColumns(String... csvColumns) {
      return csvColumns(Arrays.asList(csvColumns));
    }

    public Builder mapCsvColumnToTableColumn(String expression, String column) {
      aliases.put(column, expression + " as " + column);
      return this;
    }

    @Override
    public String toString() {
      return "Builder [columns="
          + columns
          + ", aliases="
          + aliases
          + ", csvColumns="
          + csvColumns
          + ", csvFile="
          + csvFile
          + ", charset="
          + charset
          + ", fieldSeparator="
          + fieldSeparator
          + ", fieldDelimiter="
          + fieldDelimiter
          + "]";
    }

    public Builder valueType(Class<?> valueType) {

      Annotation[][] parameterAnnotationsOfConstructor =
          TableDefinition.getCanonicalConstructor(valueType)
              .map(constructor -> constructor.getParameterAnnotations())
              .orElse(null);

      Field[] fields =
          Stream.of(valueType.getDeclaredFields())
              .filter(f -> !java.lang.reflect.Modifier.isStatic(f.getModifiers()))
              .toArray(Field[]::new);

      List<Field> csvSkipColumns = new ArrayList<>();

      for (int i = 0; i < fields.length; i++) {
        Field field = fields[i];
        List<String> opt = new ArrayList<>();
        opt.add(TableDefinition.toSqlDataType(field.getType()));

        Set<Annotation> anns = new LinkedHashSet<>();
        Arrays.stream(field.getAnnotations()).forEach(a -> anns.add(a));
        if (parameterAnnotationsOfConstructor != null) {
          Arrays.stream(parameterAnnotationsOfConstructor[i]).forEach(a -> anns.add(a));
        }
        for (Annotation ann : anns) {
          if (ann instanceof CsvColumn) {
            mapCsvColumnToTableColumn(
                ((CsvColumn) ann).value(), StringCache.toUpperSnakeCase(field.getName()));
          } else if (ann instanceof SkipCsvRead) {
            csvSkipColumns.add(field);
          }
        }
      }
      tableColumns(
          Stream.of(fields)
              .map(
                  f -> {
                    return csvSkipColumns.contains(f)
                        ? "null as " + StringCache.toUpperSnakeCase(f.getName())
                        : StringCache.toUpperSnakeCase(f.getName());
                  })
              .toArray(String[]::new));

      return this;
    }
  }

  /**
   * @param selectedColumns columns in select clause. null or empty means the all columns.
   * @param csvFile
   * @param csvColumns
   * @param charset
   * @param fieldSeparator
   * @return
   */
  private static String getCsvReadAndSelectSql(
      List<String> selectedColumns,
      File csvFile,
      List<String> csvColumns,
      Charset charset,
      char fieldSeparator,
      Character fieldDelimiter) {
    return "select "
        + (selectedColumns == null || selectedColumns.size() == 0
            ? "*"
            : String.join(",", selectedColumns))
        + " from "
        + (csvColumns == null || csvColumns.size() == 0
                ? CsvRead.builderForCsvWithHeader(csvFile)
                : CsvRead.builderForCsvWithoutHeader(csvFile, csvColumns))
            .charset(charset.toString())
            .fieldSeparator(fieldSeparator + "")
            .fieldDelimiter(fieldDelimiter + "")
            .build();
  }
}
