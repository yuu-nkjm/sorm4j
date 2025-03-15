package org.nkjmlab.sorm4j.extension.h2.sql.statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation.CsvColumnExpression;
import org.nkjmlab.sorm4j.extension.h2.sql.statement.annotation.CsvIgnore;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public class SelectCsvReadSql {

  private final String sql;

  private SelectCsvReadSql(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static Builder builder(CsvRead csvRead) {
    return new Builder(csvRead);
  }

  /**
   * A builder class for constructing SQL queries using CSV data.
   *
   * <p>This builder allows customization of selected columns and column mappings when generating a
   * SQL query using the {@code csvread} function of the H2 database. It provides methods to specify
   * table columns and map CSV columns to table columns.
   *
   * <h5>Example Usage</h5>
   *
   * <pre>
   * <code>
   * CsvRead csvRead = CsvRead.builderForCsvWithHeader(new File("data.csv")).build();
   * SelectCsvReadSql sql = SelectCsvReadSql.builder(csvRead)
   *         .tableColumns("id", "name", "age")
   *         .mapCsvColumnToTableColumn("parsedatetime(birth_date, 'y/MM/d')", "birth_date")
   *         .build();
   * String query = sql.getSql();
   * </code>
   * </pre>
   */
  public static class Builder {
    /** Columns for SQL. null or empty means the all columns. */
    private List<String> tableColumns = new ArrayList<>();

    private Map<String, String> aliases = new LinkedHashMap<>();

    private final CsvRead csvRead;

    /**
     * Constructs a builder for creating a SQL query based on a given CSV source.
     *
     * @param csvRead The {@link CsvRead} instance representing the CSV source.
     */
    public Builder(CsvRead csvRead) {
      this.csvRead = csvRead;
    }

    /**
     * Builds the SQL query string using the configured columns and mappings.
     *
     * @return A {@link SelectCsvReadSql} instance containing the generated SQL query.
     * @throws IllegalStateException If a mapped column is not found in the table columns.
     */
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
                  : String.join(", ", selectedColumns))
              + " from "
              + csvRead.getSql());
    }

    /**
     * Maps a CSV column expression to a table column in the SQL query.
     *
     * @param csvColumnExpression The SQL-compatible expression representing the CSV column.
     * @param tableColumn The name of the corresponding table column.
     * @return This builder instance for method chaining.
     */
    public Builder mapCsvColumnToTableColumn(String csvColumnExpression, String tableColumn) {
      aliases.put(tableColumn, csvColumnExpression + " as " + tableColumn);
      return this;
    }

    /**
     * Specifies the table columns to be included in the SQL query.
     *
     * @param tableColumns The list of table column names.
     * @return This builder instance for method chaining.
     */
    public Builder tableColumns(List<String> tableColumns) {
      this.tableColumns = new ArrayList<>(tableColumns);
      return this;
    }

    /**
     * Specifies the table columns to be included in the SQL query.
     *
     * @param tableColumns The table column names as varargs.
     * @return This builder instance for method chaining.
     */
    public Builder tableColumns(String... tableColumns) {
      return tableColumns(Arrays.asList(tableColumns));
    }

    /**
     * Sets the value type and automatically maps class fields to CSV columns.
     *
     * <p>This method processes the fields of the specified class and applies mappings based on the
     * {@link CsvColumnExpression} and {@link CsvIgnore} annotations.
     *
     * <ul>
     *   <li>If a field is annotated with {@link CsvColumnExpression}, the specified expression is
     *       mapped to the corresponding table column.
     *   <li>If a field is annotated with {@link CsvIgnore}, it is excluded from the SQL query by
     *       setting it to {@code null}.
     * </ul>
     *
     * @param valueType The class type whose fields should be mapped.
     * @return The {@link Builder} instance with the configured mappings.
     */
    public <T extends Record> Builder valueType(Class<T> valueType) {

      Annotation[][] parameterAnnotationsOfConstructor =
          TableDefinition.getCanonicalConstructor(valueType)
              .map(constructor -> constructor.getParameterAnnotations())
              .orElse(null);

      Field[] fields =
          Stream.of(valueType.getDeclaredFields())
              .filter(
                  f ->
                      !java.lang.reflect.Modifier.isStatic(f.getModifiers())
                          && !f.getName().startsWith(("this$")))
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
          if (ann instanceof CsvColumnExpression) {
            mapCsvColumnToTableColumn(
                ((CsvColumnExpression) ann).value(),
                SormContext.getDefaultCanonicalStringCache().toCanonicalName(field.getName()));
          } else if (ann instanceof CsvIgnore) {
            csvSkipColumns.add(field);
          }
        }
      }
      tableColumns(
          Stream.of(fields)
              .map(
                  f ->
                      (csvSkipColumns.contains(f) ? "null as " : "")
                          + SormContext.getDefaultCanonicalStringCache()
                              .toCanonicalName(f.getName()))
              .toArray(String[]::new));

      return this;
    }
  }
}
