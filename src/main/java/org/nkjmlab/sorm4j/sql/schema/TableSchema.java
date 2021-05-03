package org.nkjmlab.sorm4j.sql.schema;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class TableSchema {

  private final String tableName;
  private final String tableSchema;
  private final List<String> columnNames;
  private final String createTableStatement;
  private final String dropTableStatement;
  private final List<String> createIndexStatements;

  private TableSchema(Builder builder) {
    this.tableName = builder.tableName;
    this.tableSchema = builder.getTableSchema();
    this.createTableStatement = "create table if not exists " + tableSchema;
    this.dropTableStatement = "drop table if exists " + tableName;
    this.columnNames = builder.getColumunNames();
    this.createIndexStatements = builder.getCreateIndexStatements();

  }

  public String getTableName() {
    return tableName;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  public List<String> getCreateIndexStatements() {
    return createIndexStatements;
  }

  public String getCreateTableStatement() {
    return createTableStatement;
  }

  public String getDropTableStatement() {
    return dropTableStatement;
  }


  public String getTableSchema() {
    return tableSchema;
  }


  public static class Builder {
    private String tableName;
    private final Map<String, String[]> columnDefinitions;
    private String[] primaryKeys;
    private final List<String[]> uniqueColumnPairs;
    private final List<String[]> indexColumns;

    public Builder() {
      this.columnDefinitions = new LinkedHashMap<>();
      this.uniqueColumnPairs = new ArrayList<>();
      this.indexColumns = new ArrayList<>();
    }

    public TableSchema build() {
      return new TableSchema(this);
    }

    public Builder setTableName(String tableName) {
      this.tableName = tableName;
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
     * @param columnName
     * @param dataTypeAndOptions
     * @return
     */
    public Builder addColumnDefinition(String columnName, String... dataTypeAndOptions) {
      columnDefinitions.put(columnName, dataTypeAndOptions);
      return this;
    }

    /**
     * Adds a column pair for an index key.
     *
     * @param indexColumnPair
     */
    public Builder addIndexColumn(String... indexColumnPair) {
      indexColumns.add(indexColumnPair);
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
     * Gets a table schema.
     *
     * @return
     */

    private String getTableSchema() {
      return getTableSchema(tableName, columnDefinitions, primaryKeys, uniqueColumnPairs);
    }

    private List<String> getCreateIndexStatements() {
      return indexColumns.stream()
          .map(columns -> getCreateIndexOnStatement("index_" + tableName + "_" + join("_", columns),
              tableName, columns))
          .collect(Collectors.toList());
    }

    private List<String> getColumunNames() {
      return getColumunNames(columnDefinitions);
    }

    private static String createPrimaryKeyConstraint(String[] primaryKeys) {
      if (primaryKeys == null || primaryKeys.length == 0) {
        return "";
      }
      return ", primary key" + "(" + join(", ", primaryKeys) + ")";
    }

    private static String createUniqueConstraint(List<String[]> uniqueColumnPairs) {
      if (uniqueColumnPairs == null || uniqueColumnPairs.size() == 0) {
        return "";
      }
      return ", " + String.join(", ", uniqueColumnPairs.stream()
          .map(u -> "unique" + "(" + join(", ", u) + ")").toArray(String[]::new));
    }

    private static List<String> getColumunNames(Map<String, String[]> columnDefinitions) {
      return columnDefinitions.entrySet().stream().map(e -> e.getKey())
          .collect(Collectors.toList());
    }

    private static List<String> getColumuns(Map<String, String[]> columnDefinisions) {
      return columnDefinisions.keySet().stream()
          .map(columnName -> columnName + " " + join(" ", columnDefinisions.get(columnName)))
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

  }

}
