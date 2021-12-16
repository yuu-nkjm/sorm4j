package org.nkjmlab.sorm4j.sql.schema;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.SqlExecutor;
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
    this.createIndexStatements = builder.getCreateIndexIfNotExistsStatements();
  }

  public String getTableName() {
    return tableName;
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

  /**
   * Creates a new {@link TableSchema.Builder}.
   *
   * @return
   */
  public static TableSchema.Builder builder() {
    return new TableSchema.Builder();
  }

  /**
   * Creates a new {@link TableSchema.Builder} with the given table name.
   *
   * @return
   */
  public static TableSchema.Builder builder(String tableName) {
    return new TableSchema.Builder(tableName);
  }


  public static class Builder {
    private String tableName;
    private final Map<String, String[]> columnDefinitions;
    private String[] primaryKeys;
    private final List<String[]> uniqueColumnPairs;
    private final List<String[]> indexColumns;

    private Builder() {
      this.columnDefinitions = new LinkedHashMap<>();
      this.uniqueColumnPairs = new ArrayList<>();
      this.indexColumns = new ArrayList<>();
    }

    public Builder(String tableName) {
      this();
      this.tableName = tableName;
    }

    /**
     * Builds a {@link TableSchema}.
     *
     * @return
     */
    public TableSchema build() {
      return new TableSchema(this);
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
     * Adds a column pair for an index key. The name of index is automatically generated.
     *
     * Example.
     *
     * <pre>
     * TableSchema.builder("reports") .addColumnDefinition("score",
     * INT).addIndexDefinition("score").build();
     *
     * generates an index name like
     *
     * "index_reports_score"
     *
     * @param indexColumnPair
     */
    public Builder addIndexDefinition(String... indexColumnPair) {
      indexColumns.add(indexColumnPair);
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

    public Builder addUniqueConstraint(Enum<?>... uniqueColumnPair) {
      uniqueColumnPairs.add(toStringArray(uniqueColumnPair));
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

    public Builder setPrimaryKey(Enum<?>... attributes) {
      setPrimaryKey(toStringArray(attributes));
      return this;
    }


    private static String[] toStringArray(Enum<?>[] enums) {
      return Arrays.stream(enums).map(e -> e.name()).toArray(String[]::new);
    }

    /**
     * Gets a table schema.
     *
     * @return
     */

    private String getTableSchema() {
      return getTableSchema(tableName, columnDefinitions, primaryKeys, uniqueColumnPairs);
    }

    private List<String> getCreateIndexIfNotExistsStatements() {
      return indexColumns.stream()
          .map(columns -> getCreateIndexOnStatement("index_" + tableName + "_" + join("_", columns),
              tableName, columns))
          .collect(Collectors.toList());
    }

    private List<String> getColumunNames() {
      return getColumunNames(columnDefinitions);
    }

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

  public void createTableAndIndexesIfNotExists(SqlExecutor sqlExecutor) {
    createTableIfNotExists(sqlExecutor);
    createIndexesIfNotExists(sqlExecutor);

  }

  public void createTableIfNotExists(SqlExecutor sqlExecutor) {
    sqlExecutor.executeUpdate(getCreateTableIfNotExistsStatement());
  }

  public void createIndexesIfNotExists(SqlExecutor sqlExecutor) {
    getCreateIndexIfNotExistsStatements().forEach(s -> sqlExecutor.executeUpdate(s));

  }

  public void dropTableIfExists(SqlExecutor sqlExecutor) {
    sqlExecutor.executeUpdate(getDropTableIfExistsStatement());
  }

  public static class Keyword {

    /** Data type **/
    public static final String ARRAY = "ARRAY".toLowerCase();
    public static final String BIGINT = "BIGINT".toLowerCase();
    public static final String BOOLEAN = "BOOLEAN".toLowerCase();
    public static final String CHAR = "CHAR".toLowerCase();
    public static final String DATE = "DATE".toLowerCase();
    public static final String DECIMAL = "DECIMAL".toLowerCase();
    public static final String DOUBLE = "DOUBLE".toLowerCase();
    public static final String IDENTITY = "IDENTITY".toLowerCase();
    public static final String INT = "INT".toLowerCase();
    public static final String REAL = "REAL".toLowerCase();
    public static final String SMALLINT = "SMALLINT".toLowerCase();
    public static final String TIME = "TIME".toLowerCase();
    public static final String TIMESTAMP = "TIMESTAMP".toLowerCase();
    public static final String TINYINT = "TINYINT".toLowerCase();
    public static final String VARCHAR = "VARCHAR".toLowerCase();

    /** Constraint and misc **/
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT".toLowerCase();
    public static final String NOT_NULL = "NOT NULL".toLowerCase();
    public static final String PRIMARY_KEY = "PRIMARY KEY".toLowerCase();
    public static final String UNIQUE = "UNIQUE".toLowerCase();

    private Keyword() {}

  }

}
