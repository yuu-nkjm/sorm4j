package org.nkjmlab.sorm4j.sql.schema;

import static org.nkjmlab.sorm4j.sql.schema.TableSchema.GrammarUtils.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public abstract class TableSchema {

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
  protected void addColumnDefinition(String columnName, String... dataTypeAndOptions) {
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
  protected void addUniqueConstraint(String... uniqueColumnPair) {
    uniqueColumnPairs.add(uniqueColumnPair);
  }

  public List<String> getColumnNames() {
    return TableSchemaGrammar.getColumunNames(columnDefinitions);
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
    return TableSchemaGrammar.getTableSchema(getName(), columnDefinitions, primaryKeys,
        uniqueColumnPairs);
  }

  public String getIndexSchema(String... columns) {
    final String indexName = "index_" + name + "_" + String.join("_", columns);
    return IndexSchemaGrammar.createIndexOn(indexName, name, columns);
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
  protected void setPrimaryKey(String... attributes) {
    this.primaryKeys = attributes;
  }

  protected static interface GrammarUtils {
    public static String joinCommaAndSpace(String... elements) {
      return String.join(", ", elements);
    }

    public static String joinSpace(String... elements) {
      return String.join(" ", elements);
    }

    public static String joinCommaAndSpace(List<String> elements) {
      return String.join(", ", elements);
    }

    public static String wrapParentheses(String str) {
      return "(" + str + ")";
    }

  }
  protected static class IndexSchemaGrammar {
    private static String createIndexOn(String indexName, String tableName, String... columns) {
      return "create index if not exists " + indexName + " on " + tableName
          + wrapParentheses(joinCommaAndSpace(columns));
    }
  }
  protected static interface TableSchemaGrammar {


    private static String createPrimaryKeyConstraint(String[] primaryKeys) {
      if (primaryKeys == null || primaryKeys.length == 0) {
        return "";
      }
      return ", primary key" + wrapParentheses(joinCommaAndSpace(primaryKeys));
    }

    private static String createUniqueConstraint(List<String[]> uniqueColumnPairs) {
      if (uniqueColumnPairs == null || uniqueColumnPairs.size() == 0) {
        return "";
      }
      return ", " + joinCommaAndSpace(uniqueColumnPairs.stream()
          .map(u -> "unique" + wrapParentheses(joinCommaAndSpace(u))).toArray(String[]::new));
    }


    private static List<String> getColumunNames(Map<String, String[]> columnDefinitions) {
      return columnDefinitions.entrySet().stream().map(e -> e.getKey())
          .collect(Collectors.toList());
    }


    private static String getTableSchema(String tableName, Map<String, String[]> columns,
        String[] primaryKeys, List<String[]> uniqueColumnPairs) {
      String schema = tableName + wrapParentheses(joinCommaAndSpace(getColumuns(columns))
          + createPrimaryKeyConstraint(primaryKeys) + createUniqueConstraint(uniqueColumnPairs));
      return schema;
    }



    private static List<String> getColumuns(Map<String, String[]> columnDefinisions) {
      return columnDefinisions.keySet().stream()
          .map(columnName -> columnName + " " + joinSpace(columnDefinisions.get(columnName)))
          .collect(Collectors.toList());
    }



  }
  protected static interface TableSchemaKeyword {

    /** Data type **/
    public static final String VARCHAR = "VARCHAR";
    public static final String CHAR = "CHAR";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String TIMESTAMP_AS_CURRENT_TIMESTAMP = "TIMESTAMP AS CURRENT_TIMESTAMP";
    public static final String REAL = "REAL";
    public static final String DOUBLE = "DOUBLE";
    public static final String BIGINT = "BIGINT";
    public static final String INT = "INT";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String DECIMAL = "DECIMAL";
    public static final String TINYINT = "TINYINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String IDENTITY = "IDENTITY";

    /** Constraint and misc **/
    public static final String UNIQUE = "UNIQUE";
    public static final String NOT_NULL = "NOT NULL";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";

  }

}
