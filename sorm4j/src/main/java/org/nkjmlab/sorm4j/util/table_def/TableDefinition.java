package org.nkjmlab.sorm4j.util.table_def;

import static java.lang.String.join;
import static org.nkjmlab.sorm4j.internal.util.StringCache.toUpperSnakeCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmTable;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.util.datatype.OrmJsonColumnContainer;
import org.nkjmlab.sorm4j.util.table_def.annotation.AutoIncrement;
import org.nkjmlab.sorm4j.util.table_def.annotation.Check;
import org.nkjmlab.sorm4j.util.table_def.annotation.CheckConstraint;
import org.nkjmlab.sorm4j.util.table_def.annotation.Default;
import org.nkjmlab.sorm4j.util.table_def.annotation.Index;
import org.nkjmlab.sorm4j.util.table_def.annotation.IndexColumns;
import org.nkjmlab.sorm4j.util.table_def.annotation.NotNull;
import org.nkjmlab.sorm4j.util.table_def.annotation.PrimaryKey;
import org.nkjmlab.sorm4j.util.table_def.annotation.PrimaryKeyColumns;
import org.nkjmlab.sorm4j.util.table_def.annotation.Unique;
import org.nkjmlab.sorm4j.util.table_def.annotation.UniqueColumns;

/**
 * This class represent a table schema. This class is a utility for users to define tables and
 * indexes. It should be noted that there is no guarantee that this object will match the table
 * definition in the database.
 *
 * @see {@link TableMetaData}
 * @author nkjm
 */
@Experimental
public final class TableDefinition {

  /**
   * Creates a new {@link TableDefinition.Builder} with the given table name.
   *
   * @return
   */
  public static TableDefinition.Builder builder(String tableName) {
    return new TableDefinition.Builder(tableName);
  }

  public static TableDefinition.Builder builder(Class<?> valueType, String tableName) {
    Builder builder = TableDefinition.builder(tableName);

    Optional.ofNullable(valueType.getAnnotation(PrimaryKeyColumns.class))
        .map(a -> a.value())
        .ifPresent(val -> builder.setPrimaryKey(val));

    Optional.ofNullable(valueType.getAnnotationsByType(IndexColumns.class))
        .ifPresent(vals -> Arrays.stream(vals).forEach(v -> builder.addIndexDefinition(v.value())));

    Optional.ofNullable(valueType.getAnnotationsByType(UniqueColumns.class))
        .ifPresent(
            vals -> Arrays.stream(vals).forEach(v -> builder.addUniqueConstraint(v.value())));

    Optional.ofNullable(valueType.getAnnotationsByType(CheckConstraint.class))
        .ifPresent(vals -> Arrays.stream(vals).forEach(v -> builder.addCheckConstraint(v.value())));

    Annotation[][] parameterAnnotationsOfConstructor =
        getCanonicalConstructor(valueType)
            .map(constructor -> constructor.getParameterAnnotations())
            .orElse(null);

    Field[] fields =
        Stream.of(valueType.getDeclaredFields())
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .toArray(Field[]::new);

    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      List<String> opt = new ArrayList<>();

      Set<Annotation> anns = new LinkedHashSet<>();
      Arrays.stream(field.getAnnotations()).forEach(a -> anns.add(a));

      if (parameterAnnotationsOfConstructor != null) {
        Arrays.stream(parameterAnnotationsOfConstructor[i]).forEach(a -> anns.add(a));
      }

      opt.add(
          anns.stream().filter(ann -> ann instanceof OrmJsonColumnContainer).count() > 0
              ? "json"
              : TableDefinition.toSqlDataType(field.getType()));

      for (Annotation ann : anns) {
        if (ann instanceof PrimaryKey) {
          opt.add("primary key");
        } else if (ann instanceof AutoIncrement) {
          opt.add("auto_increment");
        } else if (ann instanceof NotNull) {
          opt.add("not null");
        } else if (ann instanceof Index) {
          builder.addIndexDefinition(toUpperSnakeCase(field.getName()));
        } else if (ann instanceof Unique) {
          builder.addUniqueConstraint(toUpperSnakeCase(field.getName()));
        } else if (ann instanceof Check) {
          opt.add("check (" + ((Check) ann).value() + ")");
        } else if (ann instanceof Default) {
          opt.add("default " + ((Default) ann).value());
        }
      }
      builder.addColumnDefinition(toUpperSnakeCase(field.getName()), opt.toArray(String[]::new));
    }
    return builder;
  }

  public static TableDefinition.Builder builder(Class<?> valueType) {
    return builder(valueType, toTableName(valueType));
  }

  public static String toTableName(Class<?> valueType) {
    OrmTable ann = valueType.getAnnotation(OrmTable.class);
    if (ann == null || ann.value().length() == 0) {
      return toUpperSnakeCase(valueType.getSimpleName() + "s");
    } else {
      return ann.value();
    }
  }

  public static Optional<Constructor<?>> getCanonicalConstructor(Class<?> recordClass) {
    try {
      Class<?>[] componentTypes =
          Arrays.stream(recordClass.getDeclaredFields())
              .filter(
                  f ->
                      !java.lang.reflect.Modifier.isStatic(f.getModifiers())
                          && !f.getName().startsWith(("this$")))
              .map(f -> f.getType())
              .toArray(Class[]::new);
      return Optional.of(recordClass.getDeclaredConstructor(componentTypes));
    } catch (NoSuchMethodException | SecurityException e) {
      return Optional.empty();
    }
  }

  private final String tableName;
  private final String tableNameAndColumnDefinitions;

  private final List<String> columnNames;

  private final String createTableStatement;

  private final String dropTableStatement;

  private final List<String> createIndexStatements;

  private TableDefinition(
      String tableName,
      String tableSchema,
      List<String> columnNames,
      String createTableStatement,
      String dropTableStatement,
      List<String> createIndexStatements) {
    this.tableName = tableName;
    this.tableNameAndColumnDefinitions = tableSchema;
    this.columnNames = Collections.unmodifiableList(columnNames);
    this.createTableStatement = createTableStatement;
    this.dropTableStatement = dropTableStatement;
    this.createIndexStatements = Collections.unmodifiableList(createIndexStatements);
  }

  @Override
  public String toString() {
    return "TableDefinition [tableName="
        + tableName
        + ", tableNameAndColumnDefinitions="
        + tableNameAndColumnDefinitions
        + ", columnNames="
        + columnNames
        + ", createTableStatement="
        + createTableStatement
        + ", dropTableStatement="
        + dropTableStatement
        + ", createIndexStatements="
        + createIndexStatements
        + "]";
  }

  public TableDefinition createIndexesIfNotExists(Orm orm) {
    getCreateIndexIfNotExistsStatements().forEach(s -> orm.executeUpdate(s));
    return this;
  }

  public TableDefinition createTableIfNotExists(Orm orm) {
    orm.executeUpdate(getCreateTableIfNotExistsStatement());
    return this;
  }

  public TableDefinition dropTableIfExists(Orm orm) {
    orm.executeUpdate(getDropTableIfExistsStatement());
    return this;
  }

  public void dropTableIfExistsCascade(Orm orm) {
    orm.executeUpdate(getDropTableIfExistsStatement() + " cascade");
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
   * TableDefinition.builder("reports") .addColumnDefinition("id", VARCHAR,
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
   * Returns a {@code String} object representing this {@link TableDefinition}'s value.
   *
   * <pre>
   * TableDefinition.builder("reports").addColumnDefinition("id", VARCHAR, PRIMARY_KEY)
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
   * Returns a {@code String} object representing this {@link TableDefinition}'s value.
   *
   * <pre>
   * TableDefinition.builder("reports").addColumnDefinition("id", VARCHAR, PRIMARY_KEY)
   * .addColumnDefinition("score", INT).build().getTableSchema();
   *
   * generates
   *
   * "reports(id varchar primary key, score int)"
   *
   * @return
   */
  public String getTableNameAndColumnDefinitions() {
    return tableNameAndColumnDefinitions;
  }

  public static class Builder {
    private static String createPrimaryKeyConstraint(String[] primaryKeys) {
      return (primaryKeys == null || primaryKeys.length == 0)
          ? ""
          : ", primary key" + "(" + join(", ", primaryKeys) + ")";
    }

    private static String createUniqueConstraint(List<String[]> uniqueColumnPairs) {
      return (uniqueColumnPairs == null || uniqueColumnPairs.size() == 0)
          ? ""
          : ", "
              + String.join(
                  ", ",
                  uniqueColumnPairs.stream()
                      .map(u -> "unique" + "(" + join(", ", u) + ")")
                      .toArray(String[]::new));
    }

    private static String createCheckConstraint(List<String> checkConditions) {
      return (checkConditions == null || checkConditions.size() == 0)
          ? ""
          : ", "
              + String.join(
                  ", ",
                  checkConditions.stream()
                      .map(u -> "check" + "(" + u + ")")
                      .toArray(String[]::new));
    }

    private static List<String> getColumunNames(Map<String, String[]> columnDefinitions) {
      return columnDefinitions.entrySet().stream()
          .map(e -> e.getKey())
          .collect(Collectors.toList());
    }

    private static List<String> getColumuns(Map<String, String[]> columnDefinisions) {
      return columnDefinisions.keySet().stream()
          .map(
              columnName ->
                  columnName
                      + " "
                      + join(
                          " ",
                          Arrays.stream(columnDefinisions.get(columnName))
                              .map(s -> s.trim())
                              .collect(Collectors.toList())))
          .collect(Collectors.toList());
    }

    private static String getCreateIndexOnStatement(
        String indexName, String tableName, String... columns) {
      return "create index if not exists "
          + indexName
          + " on "
          + tableName
          + "("
          + String.join(", ", columns)
          + ")";
    }

    private static String getTableSchema(
        String tableName,
        Map<String, String[]> columns,
        String[] primaryKeys,
        List<String[]> uniqueColumnPairs,
        List<String> checkConditions) {
      String schema =
          tableName
              + "("
              + join(", ", getColumuns(columns))
              + createPrimaryKeyConstraint(primaryKeys)
              + createUniqueConstraint(uniqueColumnPairs)
              + createCheckConstraint(checkConditions)
              + ")";
      return schema;
    }

    private static String[] toStringArray(Enum<?>[] enums) {
      return Arrays.stream(enums).map(e -> e.toString()).toArray(String[]::new);
    }

    private String tableName;

    private final Map<String, String[]> columnDefinitions;

    private String[] primaryKeys;

    private final List<String[]> uniqueColumnPairs;

    private final List<String[]> indexColumns;

    private final List<String> checkConditions;

    private Builder(String tableName) {
      this.columnDefinitions = new LinkedHashMap<>();
      this.uniqueColumnPairs = new ArrayList<>();
      this.indexColumns = new ArrayList<>();
      this.checkConditions = new ArrayList<>();
      this.tableName = tableName;
    }

    /**
     * @see {@link #addColumnDefinition(String, String...)
     * @param columnName
     * @param dataTypeAndOptions
     * @return
     */
    public Builder addColumnDefinition(Enum<?> columnName, String... dataTypeAndOptions) {
      addColumnDefinition(columnName.toString(), dataTypeAndOptions);
      return this;
    }

    /**
     * Adds an column definition.
     *
     * <p>For example,
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
     * TableDefinition.builder("reports") .addColumnDefinition("score",
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
     * <p>For example,
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

    public Builder addCheckConstraint(String... checkConditions) {
      this.checkConditions.addAll(Arrays.asList(checkConditions));
      return this;
    }

    /**
     * Builds a {@link TableDefinition}.
     *
     * @return
     */
    public TableDefinition build() {
      if (columnDefinitions.isEmpty()) {
        return new TableDefinition(
            tableName, "", Collections.emptyList(), "", "", Collections.emptyList());
      } else {
        String tableSchema =
            getTableSchema(
                tableName, columnDefinitions, primaryKeys, uniqueColumnPairs, checkConditions);
        String createTableStatement = "create table if not exists " + tableSchema;
        String dropTableStatement = "drop table if exists " + tableName;
        return new TableDefinition(
            tableName,
            tableSchema,
            getColumunNames(),
            createTableStatement,
            dropTableStatement,
            getCreateIndexIfNotExistsStatements());
      }
    }

    private List<String> getColumunNames() {
      return getColumunNames(columnDefinitions);
    }

    private List<String> getCreateIndexIfNotExistsStatements() {
      return indexColumns.stream()
          .map(
              columns ->
                  getCreateIndexOnStatement(
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
     * <p>For example,
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

  public static String toSqlDataType(Class<?> type) {
    if (type.getAnnotation(OrmJsonColumnContainer.class) != null) {
      return "json";
    }
    if (ArrayUtils.getInternalComponentType(type).getAnnotation(OrmJsonColumnContainer.class)
        != null) {
      return "json";
    }

    switch (type.getName()) {
      case "int":
      case "java.lang.Integer":
        return "integer";
      case "double":
      case "java.lang.Double":
        return "double";
      case "boolean":
      case "java.lang.Boolean":
        return "boolean";
      case "byte":
      case "java.lang.Byte":
        return "tinyint";
      case "short":
      case "java.lang.Short":
        return "smallint";
      case "long":
      case "java.lang.Long":
        return "bigint";
      case "float":
      case "java.lang.Float":
        return "float";
      case "char":
      case "java.lang.Character":
        return "character";
      case "java.lang.String":
        return "varchar";
      case "java.math.BigDecimal":
        return "numeric";
      case "java.sql.Timestamp":
      case "java.time.Instant":
      case "java.time.LocalDateTime":
        return "timestamp";
      case "java.sql.Time":
      case "java.time.LocalTime":
        return "time";
      case "java.sql.Date":
      case "java.time.LocalDate":
        return "date";
      case "java.time.OffsetTime":
        return "time with time zone";
      case "java.time.OffsetDateTime":
        return "timestamp with time zone";
      case "java.sql.Blob":
        return "blob";
      case "java.sql.Clob":
        return "clob";
      case "java.io.InputStream":
        return "longvarbinary";
      case "java.io.Reader":
        return "longvarchar";
      case "org.nkjmlab.sorm4j.util.datatype.GeometryString":
      case "org.nkjmlab.sorm4j.util.jts.GeometryJts":
        return "geometry";
      case "org.nkjmlab.sorm4j.util.datatype.JsonByte":
      case "java.util.List":
      case "java.util.Map":
        return "json";
      default:
        if (type.isArray()) {
          return toSqlDataType(type.getComponentType()) + " array";
        } else if (type.isEnum()) {
          return "varchar";
        } else {
          return "java_object";
        }
    }
  }
}
