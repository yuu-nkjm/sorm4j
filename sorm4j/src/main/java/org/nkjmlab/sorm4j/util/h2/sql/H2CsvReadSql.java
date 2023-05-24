package org.nkjmlab.sorm4j.util.h2.sql;

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

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(File csvFile, Class<?> ormRecordClass) {
    return new Builder(csvFile, ormRecordClass);
  }

  public static Builder builder(File csvFile) {
    return new Builder(csvFile);
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
    private File csvFile;
    private Charset charset = StandardCharsets.UTF_8;
    private char fieldSeparator = ',';
    private Character fieldDelimiter = null;

    public Builder(File csvFile) {
      setCsvFile(csvFile);
    }

    public Builder(File csvFile, Class<?> ormRecordClass) {
      this(csvFile);
      setOrmRecordClass(ormRecordClass);
    }


    private Builder() {}

    public H2CsvReadSql build() {
      List<String> selectedColumns = new ArrayList<>(columns);

      aliases.entrySet().forEach(en -> {
        int index = selectedColumns.indexOf(en.getKey());
        if (index == -1) {
          Object[] params = {en.getKey(), columns};
          throw new IllegalStateException(ParameterizedStringFormatter.LENGTH_256
              .format("{} is not found in Columns {}", params));
        }
        selectedColumns.set(index, en.getValue());
      });

      return new H2CsvReadSql(columns, H2CsvFunctions.getCsvReadAndSelectSql(selectedColumns,
          csvFile, csvColumns, charset, fieldSeparator, fieldDelimiter));
    }

    public Builder setCsvFile(File csvFile) {
      this.csvFile = csvFile;
      return this;
    }

    public Builder setCharset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public Builder setFieldSeparator(char fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }

    public Builder setFieldDelimiter(Character fieldDelimiter) {
      this.fieldDelimiter = fieldDelimiter;
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
          + ", fieldDelimiter=" + fieldDelimiter + "]";
    }

    public Builder setOrmRecordClass(Class<?> ormRecordClass) {

      Annotation[][] parameterAnnotationsOfConstructor =
          TableDefinition.getCanonicalConstructor(ormRecordClass)
              .map(constructor -> constructor.getParameterAnnotations()).orElse(null);

      Field[] fields = Stream.of(ormRecordClass.getDeclaredFields())
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
            mapCsvColumnToTableColumn(((CsvColumn) ann).value(),
                StringCache.toUpperSnakeCase(field.getName()));
          } else if (ann instanceof SkipCsvRead) {
            csvSkipColumns.add(field);
          }

        }
      }
      setTableColumns(Stream.of(fields).map(f -> {
        return csvSkipColumns.contains(f) ? "null as " + StringCache.toUpperSnakeCase(f.getName())
            : StringCache.toUpperSnakeCase(f.getName());
      }).toArray(String[]::new));

      return this;
    }


  }


}
