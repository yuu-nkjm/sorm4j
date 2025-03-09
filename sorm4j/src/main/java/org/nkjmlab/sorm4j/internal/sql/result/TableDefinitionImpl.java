package org.nkjmlab.sorm4j.internal.sql.result;

import java.util.Collections;
import java.util.List;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public final class TableDefinitionImpl implements TableDefinition {

  private final String tableName;
  private final String tableNameAndColumnDefinitions;

  private final List<String> columnNames;

  private final String createTableStatement;

  private final String dropTableStatement;

  private final List<String> createIndexStatements;

  public TableDefinitionImpl(
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

  @Override
  public TableDefinition createIndexesIfNotExists(Orm orm) {
    getCreateIndexIfNotExistsStatements().forEach(s -> orm.executeUpdate(s));
    return this;
  }

  @Override
  public TableDefinition createTableIfNotExists(Orm orm) {
    orm.executeUpdate(getCreateTableIfNotExistsStatement());
    return this;
  }

  @Override
  public TableDefinition dropTableIfExists(Orm orm) {
    orm.executeUpdate(getDropTableIfExistsStatement());
    return this;
  }

  @Override
  public void dropTableIfExistsCascade(Orm orm) {
    orm.executeUpdate(getDropTableIfExistsStatement() + " cascade");
  }

  @Override
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
  @Override
  public List<String> getCreateIndexIfNotExistsStatements() {
    return createIndexStatements;
  }

  /**
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
  @Override
  public String getCreateTableIfNotExistsStatement() {
    return createTableStatement;
  }

  /**
   * Gets drop table if exists statement.
   *
   * @return
   */
  @Override
  public String getDropTableIfExistsStatement() {
    return dropTableStatement;
  }

  @Override
  public String getTableName() {
    return tableName;
  }

  /**
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
  @Override
  public String getTableNameAndColumnDefinitions() {
    return tableNameAndColumnDefinitions;
  }
}
