package org.nkjmlab.sorm4j.util.table;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.result.TableMetaData;

/**
 * This class represent a table schema. This class is a utility for users to define tables and
 * indexes. It should be noted that there is no guarantee that this object will match the table
 * definition in the database.
 *
 * @see {@link TableMetaData}
 * @author nkjm
 *
 */
@Experimental
public final class TableSchema {

  /**
   * Creates a new {@link TableSchema.Builder} with the given table name.
   *
   * @return
   */
  public static TableSchema.Builder builder(String tableName) {
    return new TableSchema.Builder(tableName);
  }

  private final String tableName;
  private final String tableSchema;

  private final List<String> columnNames;

  private final String createTableStatement;

  private final String dropTableStatement;

  private final List<String> createIndexStatements;


  private TableSchema(String tableName, String tableSchema, List<String> columnNames,
      String createTableStatement, String dropTableStatement, List<String> createIndexStatements) {
    this.tableName = tableName;
    this.tableSchema = tableSchema;
    this.columnNames = columnNames;
    this.createTableStatement = createTableStatement;
    this.dropTableStatement = dropTableStatement;
    this.createIndexStatements = createIndexStatements;
  }

  public TableSchema createIndexesIfNotExists(Orm orm) {
    getCreateIndexIfNotExistsStatements().forEach(s -> orm.executeUpdate(s));
    return this;
  }

  public TableSchema createTableIfNotExists(Orm orm) {
    orm.executeUpdate(getCreateTableIfNotExistsStatement());
    return this;
  }

  public TableSchema dropTableIfExists(Orm orm) {
    orm.executeUpdate(getDropTableIfExistsStatement());
    return this;
  }


  public List<String> getColumnNames() {
    return columnNames;
  }

  /**
   * Gets create index if not exists statements.
   *
   * Example.
   *
   * <pre>
   * TableSchema.builder("reports") .addColumnDefinition("id", VARCHAR,
   * PRIMARY_KEY).addColumnDefinition("score", INT)
   * .addIndexDefinition("score").addIndexDefinition("id",
   * "score").build().getCreateIndexIfNotExistsStatements();
   *
   * generates
   *
   * "[create index if not exists index_reports_score on reports(score), create index if not exists
   * index_reports_id_score on reports(id, score)]"
   *
   * @return
   */
  public List<String> getCreateIndexIfNotExistsStatements() {
    return createIndexStatements;
  }

  /**
   * Returns a {@code String} object representing this {@link TableSchema}'s value.
   *
   * <pre>
   * TableSchema.builder("reports").addColumnDefinition("id", VARCHAR, PRIMARY_KEY)
   * .addColumnDefinition("score", INT).build().getTableSchema();
   *
   * generates
   *
   * "create table if not exists reports(id varchar primary key, score int)"
   *
   * @return
   */
  public String getCreateTableIfNotExistsStatement() {
    return createTableStatement;
  }

  /**
   * Gets drop table if exists statement.
   *
   * @return
   */
  public String getDropTableIfExistsStatement() {
    return dropTableStatement;
  }

  public String getTableName() {
    return tableName;
  }

  /**
   * Returns a {@code String} object representing this {@link TableSchema}'s value.
   *
   * <pre>
   * TableSchema.builder("reports").addColumnDefinition("id", VARCHAR, PRIMARY_KEY)
   * .addColumnDefinition("score", INT).build().getTableSchema();
   *
   * generates
   *
   * "reports(id varchar primary key, score int)"
   *
   * @return
   */
  public String getTableSchema() {
    return tableSchema;
  }

  public static class Builder {
    private static String createPrimaryKeyConstraint(String[] primaryKeys) {
      return (primaryKeys == null || primaryKeys.length == 0) ? ""
          : ", primary key" + "(" + join(", ", primaryKeys) + ")";
    }

    private static String createUniqueConstraint(List<String[]> uniqueColumnPairs) {
      return (uniqueColumnPairs == null || uniqueColumnPairs.size() == 0) ? ""
          : ", " + String.join(", ", uniqueColumnPairs.stream()
              .map(u -> "unique" + "(" + join(", ", u) + ")").toArray(String[]::new));
    }

    private static List<String> getColumunNames(Map<String, String[]> columnDefinitions) {
      return columnDefinitions.entrySet().stream().map(e -> e.getKey())
          .collect(Collectors.toList());
    }

    private static List<String> getColumuns(Map<String, String[]> columnDefinisions) {
      return columnDefinisions.keySet().stream()
          .map(columnName -> columnName + " "
              + join(" ", Arrays.stream(columnDefinisions.get(columnName)).map(s -> s.trim())
                  .collect(Collectors.toList())))
          .collect(Collectors.toList());
    }

    private static String getCreateIndexOnStatement(String indexName, String tableName,
        String... columns) {
      return "create index if not exists " + indexName + " on " + tableName + "("
          + String.join(", ", columns) + ")";
    }

    private static String getTableSchema(String tableName, Map<String, String[]> columns,
        String[] primaryKeys, List<String[]> uniqueColumnPairs) {
      String schema = tableName + "(" + join(", ", getColumuns(columns))
          + createPrimaryKeyConstraint(primaryKeys) + createUniqueConstraint(uniqueColumnPairs)
          + ")";
      return schema;
    }

    private static String[] toStringArray(Enum<?>[] enums) {
      return Arrays.stream(enums).map(e -> e.name()).toArray(String[]::new);
    }

    private String tableName;

    private final Map<String, String[]> columnDefinitions;

    private String[] primaryKeys;

    private final List<String[]> uniqueColumnPairs;


    private final List<String[]> indexColumns;



    private Builder(String tableName) {
      this.columnDefinitions = new LinkedHashMap<>();
      this.uniqueColumnPairs = new ArrayList<>();
      this.indexColumns = new ArrayList<>();
      this.tableName = tableName;
    }

    /**
     * @see {@link #addColumnDefinition(String, String...)
     * @param columnName
     * @param dataTypeAndOptions
     * @return
     */
    public Builder addColumnDefinition(Enum<?> columnName, String... dataTypeAndOptions) {
      addColumnDefinition(columnName.name(), dataTypeAndOptions);
      return this;
    }

    /**
     * Adds an column definition.
     *
     * <p>
     * For example,
     *
     * <pre>
     * addUniqueConstraint("id","name")  converted to "UNIQUE (id, name)"
     * </pre>
     *
     * @param columnName
     * @param dataTypeAndOptions
     * @return
     */
    public Builder addColumnDefinition(String columnName, String... dataTypeAndOptions) {
      columnDefinitions.put(columnName, dataTypeAndOptions);
      return this;
    }

    /**
     * @see #addIndexDefinition(String...)
     * @param indexColumnPair
     * @return
     */
    public Builder addIndexDefinition(Enum<?>... indexColumnPair) {
      addIndexDefinition(toStringArray(indexColumnPair));
      return this;
    }


    /**
     * Adds a column pair for an index key. The name of index is automatically generated.
     *
     * Example.
     *
     * <pre>
     * TableSchema.builder("reports") .addColumnDefinition("score",
     * INT).addIndexDefinition("id","score").build();
     *
     * generates an index name like
     *
     * "index_in_reports_on_id_score"
     *
     * @param indexColumnPair
     */
    public Builder addIndexDefinition(String... indexColumnPair) {
      indexColumns.add(indexColumnPair);
      return this;
    }

    public Builder addUniqueConstraint(Enum<?>... uniqueColumnPair) {
      uniqueColumnPairs.add(toStringArray(uniqueColumnPair));
      return this;
    }

    /**
     * Adds an unique constraint.
     *
     * <p>
     * For example,
     *
     * <pre>
     * addUniqueConstraint("id","name")  converted to "UNIQUE (id, name)"
     * </pre>
     *
     * @param uniqueColumnPair
     */
    public Builder addUniqueConstraint(String... uniqueColumnPair) {
      uniqueColumnPairs.add(uniqueColumnPair);
      return this;
    }

    /**
     * Builds a {@link TableSchema}.
     *
     * @return
     */
    public TableSchema build() {
      if (columnDefinitions.isEmpty()) {
        return new TableSchema(tableName, "", Collections.emptyList(), "", "",
            Collections.emptyList());
      } else {
        String tableSchema =
            getTableSchema(tableName, columnDefinitions, primaryKeys, uniqueColumnPairs);
        String createTableStatement = "create table if not exists " + tableSchema;
        String dropTableStatement = "drop table if exists " + tableName;
        return new TableSchema(tableName, tableSchema, getColumunNames(), createTableStatement,
            dropTableStatement, getCreateIndexIfNotExistsStatements());
      }
    }

    private List<String> getColumunNames() {
      return getColumunNames(columnDefinitions);
    }

    private List<String> getCreateIndexIfNotExistsStatements() {
      return indexColumns.stream()
          .map(columns -> getCreateIndexOnStatement(
              "index_in_" + tableName + "_on_" + join("_", columns), tableName, columns))
          .collect(Collectors.toList());
    }


    public Builder setPrimaryKey(Enum<?>... attributes) {
      setPrimaryKey(toStringArray(attributes));
      return this;
    }

    /**
     * Sets attributes as primary key attributes.
     *
     * <p>
     * For example,
     *
     * <pre>
     * setPrimaryKey("id","name")  converted to "PRIMARY KEY (id, name)"
     * </pre>
     *
     * @param attributes
     */
    public Builder setPrimaryKey(String... attributes) {
      this.primaryKeys = attributes;
      return this;
    }

    /**
     * Sets table name.
     *
     * @param tableName
     * @return
     */
    public Builder setTableName(String tableName) {
      this.tableName = tableName;
      return this;
    }
  }
}
