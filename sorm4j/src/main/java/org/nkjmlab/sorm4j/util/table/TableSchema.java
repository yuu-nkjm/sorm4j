package org.nkjmlab.sorm4j.util.table;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.lowlevel_orm.SqlExecutor;
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
public class TableSchema {

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

  public void createIndexesIfNotExists(SqlExecutor sqlExecutor) {
    getCreateIndexIfNotExistsStatements().forEach(s -> sqlExecutor.executeUpdate(s));

  }


  public void createTableAndIndexesIfNotExists(SqlExecutor sqlExecutor) {
    createTableIfNotExists(sqlExecutor);
    createIndexesIfNotExists(sqlExecutor);

  }

  public void createTableIfNotExists(SqlExecutor sqlExecutor) {
    sqlExecutor.executeUpdate(getCreateTableIfNotExistsStatement());
  }

  public void dropTableIfExists(SqlExecutor sqlExecutor) {
    sqlExecutor.executeUpdate(getDropTableIfExistsStatement());
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

    private static String[] toStringArray(Enum<?>[] enums) {
      return Arrays.stream(enums).map(e -> e.name()).toArray(String[]::new);
    }

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
      return new TableSchema(this);
    }

    private List<String> getColumunNames() {
      return getColumunNames(columnDefinitions);
    }

    private List<String> getCreateIndexIfNotExistsStatements() {
      return indexColumns.stream()
          .map(columns -> getCreateIndexOnStatement("index_" + tableName + "_" + join("_", columns),
              tableName, columns))
          .collect(Collectors.toList());
    }

    /**
     * Gets a table schema.
     *
     * @return
     */

    private String getTableSchema() {
      return getTableSchema(tableName, columnDefinitions, primaryKeys, uniqueColumnPairs);
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
  public static class Keyword {

    /** Data type **/
    public static final String ARRAY = "array";
    public static final String BIGINT = "bigint";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";
    public static final String DATE = "date";
    public static final String DECIMAL = "decimal";
    public static final String DOUBLE = "double";
    public static final String IDENTITY = "identity";
    public static final String INT = "int";
    public static final String REAL = "real";
    public static final String SMALLINT = "smallint";
    public static final String TIME = "time";
    public static final String TIMESTAMP = "timestamp";
    public static final String TINYINT = "tinyint";
    public static final String VARCHAR = "varchar";

    /** Constraint and misc **/
    public static final String AUTO_INCREMENT = "auto_increment";
    public static final String NOT_NULL = "not null";
    public static final String PRIMARY_KEY = "primary key";
    public static final String UNIQUE = "unique";

    public static String chars(int num) {
      return CHAR + "(" + num + ")";
    }

    public static String decimal(int precision) {
      return DECIMAL + "(" + precision + ")";
    }

    public static String decimal(int precision, int scale) {
      return DECIMAL + "(" + precision + "," + scale + ")";
    }

    private Keyword() {}

  }

}
