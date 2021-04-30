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

  private final String name;
  private final Map<String, String[]> columnDefinitions;
  private final List<String[]> uniqueColumnPairs;
  private String[] primaryKeys;


  public TableSchema(String name) {
    this.name = name;
    this.columnDefinitions = new LinkedHashMap<>();
    this.uniqueColumnPairs = new ArrayList<>();
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
   */
  public void addColumnDefinition(String columnName, String... dataTypeAndOptions) {
    columnDefinitions.put(columnName, dataTypeAndOptions);
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
  public void addUniqueConstraint(String... uniqueColumnPair) {
    uniqueColumnPairs.add(uniqueColumnPair);
  }

  public List<String> getColumnNames() {
    return getColumunNames(columnDefinitions);
  }

  public String getName() {
    return name;
  }

  /**
   * Gets a table schema.
   *
   * @return
   */

  public String getTableSchema() {
    return getTableSchema(getName(), columnDefinitions, primaryKeys, uniqueColumnPairs);
  }

  public String getIndexSchema(String... columns) {
    final String indexName = "index_" + name + "_" + join("_", columns);
    return createIndexOn(indexName, name, columns);
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
  public void setPrimaryKey(String... attributes) {
    this.primaryKeys = attributes;
  }

  private static String createIndexOn(String indexName, String tableName, String... columns) {
    return "create index if not exists " + indexName + " on " + tableName + "("
        + String.join(", ", columns) + ")";
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
    return columnDefinitions.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
  }


  private static String getTableSchema(String tableName, Map<String, String[]> columns,
      String[] primaryKeys, List<String[]> uniqueColumnPairs) {
    String schema = tableName + "(" + join(", ", getColumuns(columns))
        + createPrimaryKeyConstraint(primaryKeys) + createUniqueConstraint(uniqueColumnPairs) + ")";
    return schema;
  }



  private static List<String> getColumuns(Map<String, String[]> columnDefinisions) {
    return columnDefinisions.keySet().stream()
        .map(columnName -> columnName + " " + join(" ", columnDefinisions.get(columnName)))
        .collect(Collectors.toList());
  }

}
