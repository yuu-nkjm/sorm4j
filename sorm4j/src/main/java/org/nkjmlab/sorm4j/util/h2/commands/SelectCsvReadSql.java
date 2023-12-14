package org.nkjmlab.sorm4j.util.h2.commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
import org.nkjmlab.sorm4j.util.h2.commands.annotation.CsvColumn;
import org.nkjmlab.sorm4j.util.h2.commands.annotation.SkipCsvRead;
import org.nkjmlab.sorm4j.util.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class SelectCsvReadSql {

  private final List<String> tableColumns;

  private final String sql;

  private SelectCsvReadSql(String sql, List<String> tableColumns) {
    this.tableColumns = tableColumns;
    this.sql = sql;
  }

  public List<String> getTableColumns() {
    return tableColumns;
  }

  public String getSql() {
    return sql;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(CsvRead csvRead) {
    return new Builder(csvRead);
  }

  public static Builder builder(CsvRead csvRead, Class<?> valueType) {
    return new Builder(csvRead, valueType);
  }

  public static class Builder {
    /** Columns for SQL. null or empty means the all columns. */
    private List<String> tableColumns = new ArrayList<>();

    private Map<String, String> aliases = new HashMap<>();

    private CsvRead csvRead = null;

    public Builder(CsvRead csvRead) {
      csvRead(csvRead);
    }

    public Builder(CsvRead csvRead, Class<?> valueType) {
      this(csvRead);
      valueType(valueType);
    }

    private Builder() {}

    public SelectCsvReadSql build() {
      List<String> selectedColumns = new ArrayList<>(tableColumns);

      aliases
          .entrySet()
          .forEach(
              en -> {
                int index = selectedColumns.indexOf(en.getKey());
                if (index == -1) {
                  Object[] params = {en.getKey(), tableColumns};
                  throw new IllegalStateException(
                      ParameterizedStringFormatter.LENGTH_256.format(
                          "{} is not found in Columns {}", params));
                }
                selectedColumns.set(index, en.getValue());
              });

      return new SelectCsvReadSql(
          "select "
              + (selectedColumns == null || selectedColumns.size() == 0
                  ? "*"
                  : String.join(",", selectedColumns))
              + " from "
              + csvRead.getSql(),
          tableColumns);
    }

    public Builder mapCsvColumnToTableColumn(String expression, String column) {
      aliases.put(column, expression + " as " + column);
      return this;
    }

    public Builder tableColumns(List<String> tableColumns) {
      this.tableColumns = new ArrayList<>(tableColumns);
      return this;
    }

    public Builder tableColumns(String... tableColumns) {
      return tableColumns(Arrays.asList(tableColumns));
    }

    @Override
    public String toString() {
      return "Builder [columns="
          + tableColumns
          + ", aliases="
          + aliases
          + ", csvRead="
          + csvRead
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

    public void csvRead(CsvRead csvRead) {
      this.csvRead = csvRead;
    }
  }
}
