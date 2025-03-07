package org.nkjmlab.sorm4j.table.definition;

import static java.lang.String.join;

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
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.jackson.annotation.OrmJacksonColumn;
import org.nkjmlab.sorm4j.internal.container.sql.result.TableDefinitionImpl;
import org.nkjmlab.sorm4j.internal.util.ArrayUtils;
import org.nkjmlab.sorm4j.mapping.annotation.OrmTable;
import org.nkjmlab.sorm4j.table.definition.annotation.AutoIncrement;
import org.nkjmlab.sorm4j.table.definition.annotation.Check;
import org.nkjmlab.sorm4j.table.definition.annotation.Default;
import org.nkjmlab.sorm4j.table.definition.annotation.Index;
import org.nkjmlab.sorm4j.table.definition.annotation.IndexColumnPair;
import org.nkjmlab.sorm4j.table.definition.annotation.NotNull;
import org.nkjmlab.sorm4j.table.definition.annotation.PrimaryKey;
import org.nkjmlab.sorm4j.table.definition.annotation.PrimaryKeyConstraint;
import org.nkjmlab.sorm4j.table.definition.annotation.Unique;
import org.nkjmlab.sorm4j.table.definition.annotation.UniqueConstraint;

/**
 * This class represent a table schema and indexes. This class is a utility for users to define
 * tables and indexes. It should be noted that there is no guarantee that this object will match the
 * table definition in the database.
 *
 * @author nkjm
 */
public interface TableDefinition {

  TableDefinition createIndexesIfNotExists(Orm orm);

  TableDefinition createTableIfNotExists(Orm orm);

  TableDefinition dropTableIfExists(Orm orm);

  void dropTableIfExistsCascade(Orm orm);

  List<String> getColumnNames();

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
  List<String> getCreateIndexIfNotExistsStatements();

  /**
   * Returns a {@code String} object representing this {@link TableDefinitionImpl}'s value.
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
  String getCreateTableIfNotExistsStatement();

  /**
   * Gets drop table if exists statement.
   *
   * @return
   */
  String getDropTableIfExistsStatement();

  String getTableName();

  /**
   * Returns a {@code String} object representing this {@link TableDefinitionImpl}'s value.
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
  String getTableNameAndColumnDefinitions();

  static String toSqlDataType(Class<?> type) {
    if (type.getAnnotation(OrmJacksonColumn.class) != null) {
      return "json";
    }
    if (ArrayUtils.getInternalComponentType(type).getAnnotation(OrmJacksonColumn.class) != null) {
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
      case "org.nkjmlab.sorm4j.extension.datatype.container.GeometryString":
      case "org.nkjmlab.sorm4j.extension.datatype.jts.GeometryJts":
        return "geometry";
      case "org.nkjmlab.sorm4j.extension.datatype.container.JsonByte":
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

  /**
   * Retrieves the canonical constructor of the given record class, if available.
   *
   * @param recordClass the record class to retrieve the canonical constructor from
   * @return an {@code Optional} containing the canonical constructor if found, otherwise an empty
   *     {@code Optional}
   */
  static Optional<Constructor<?>> getCanonicalConstructor(Class<?> recordClass) {
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

  static String toTableName(Class<?> valueType) {
    OrmTable ann = valueType.getAnnotation(OrmTable.class);
    if (ann == null || ann.value().length() == 0) {
      return SormContext.getDefaultCanonicalStringCache()
          .toCanonicalName(valueType.getSimpleName() + "s");
    } else {
      return ann.value();
    }
  }

  static TableDefinition.Builder builder(Class<?> valueType, String tableName) {
    TableDefinitionImpl.Builder builder = builder(tableName);

    Optional.ofNullable(valueType.getAnnotation(PrimaryKeyConstraint.class))
        .map(a -> a.value())
        .ifPresent(val -> builder.setPrimaryKey(val));

    Optional.ofNullable(valueType.getAnnotationsByType(IndexColumnPair.class))
        .ifPresent(vals -> Arrays.stream(vals).forEach(v -> builder.addIndexDefinition(v.value())));

    Optional.ofNullable(valueType.getAnnotationsByType(UniqueConstraint.class))
        .ifPresent(
            vals -> Arrays.stream(vals).forEach(v -> builder.addUniqueConstraint(v.value())));

    Optional.ofNullable(valueType.getAnnotationsByType(Check.class))
        .ifPresent(vals -> Arrays.stream(vals).forEach(v -> builder.addCheckConstraint(v.value())));

    Annotation[][] parameterAnnotationsOfConstructor =
        TableDefinition.getCanonicalConstructor(valueType)
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
          anns.stream().filter(ann -> ann instanceof OrmJacksonColumn).count() > 0
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
          builder.addIndexDefinition(
              SormContext.getDefaultCanonicalStringCache().toCanonicalName(field.getName()));
        } else if (ann instanceof Unique) {
          builder.addUniqueConstraint(
              SormContext.getDefaultCanonicalStringCache().toCanonicalName(field.getName()));
        } else if (ann instanceof Check) {
          opt.add("check (" + ((Check) ann).value() + ")");
        } else if (ann instanceof Default) {
          opt.add("default " + ((Default) ann).value());
        }
      }
      builder.addColumnDefinition(
          SormContext.getDefaultCanonicalStringCache().toCanonicalName(field.getName()),
          opt.toArray(String[]::new));
    }
    return builder;
  }

  static TableDefinitionImpl.Builder builder(Class<?> valueType) {
    return TableDefinition.builder(valueType, TableDefinition.toTableName(valueType));
  }

  /**
   * Creates a new {@link TableDefinitionImpl.Builder} with the given table name.
   *
   * @return
   */
  static TableDefinitionImpl.Builder builder(String tableName) {
    return new TableDefinitionImpl.Builder(tableName);
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

    Builder(String tableName) {
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
     * Builds a {@link TableDefinitionImpl}.
     *
     * @return
     */
    public TableDefinition build() {
      if (columnDefinitions.isEmpty()) {
        return new TableDefinitionImpl(
            tableName, "", Collections.emptyList(), "", "", Collections.emptyList());
      } else {
        String tableSchema =
            getTableSchema(
                tableName, columnDefinitions, primaryKeys, uniqueColumnPairs, checkConditions);
        String createTableStatement = "create table if not exists " + tableSchema;
        String dropTableStatement = "drop table if exists " + tableName;
        return new TableDefinitionImpl(
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
}
