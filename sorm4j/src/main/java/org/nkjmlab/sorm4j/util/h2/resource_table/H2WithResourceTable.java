package org.nkjmlab.sorm4j.util.h2.resource_table;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.h2.H2BasicTable;
import org.nkjmlab.sorm4j.util.h2.functions.table.CsvRead;

@Experimental
public class H2WithResourceTable<T> extends H2BasicTable<T> {

  private final TableResources tableResources;
  private final SqlResources sqlResources;

  public H2WithResourceTable(
      Sorm orm, Class<T> valueType, TableResources tableResources, SqlResources sqlResources) {
    super(orm, valueType);
    this.tableResources = tableResources;
    this.sqlResources = sqlResources;
  }

  public void deleteCsv() {
    tableResources.deleteCsv(getTableName());
  }

  public File getCsvFile() {
    return tableResources.getCsvFile(this);
  }

  public void writeCsv() {
    tableResources.writeCsv(this);
  }

  public void executeInitializationSql() {
    String sqlFileName = getTableName() + ".sql";
    executeInitializationSql(sqlFileName);
  }

  public void executeCreateIndexSql() {
    String sqlFileName = getTableName() + "_INDEX.sql";
    Path indexSql = sqlResources.getSqlPath(sqlFileName);
    if (indexSql.toFile().exists()) {
      getOrm().executeUpdate(readSql(sqlFileName));
    }
  }

  public void executeInitializationSql(Function<String, String> sqlTemplateHandler) {
    String tableName = getTableName();
    String sqlTemplate = readSql(tableName + "_TEMPLATE.sql");
    String handledSqlFileName = tableName + "_GEN.sql";
    String handledSql = sqlTemplateHandler.apply(sqlTemplate);

    try {
      Files.writeString(
          sqlResources.getSqlPath(handledSqlFileName),
          handledSql,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);
      executeInitializationSql(handledSqlFileName);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  private void executeInitializationSql(String sqlFileName) {
    getOrm().executeUpdate(readSql(sqlFileName));
    executeCreateIndexSql();
  }

  public SqlResources getSqlResource() {
    return sqlResources;
  }

  public void loadCsv() {
    File csv = getCsvFile();
    createTableIfNotExists(CsvRead.builderForCsvWithHeader(csv).build());
  }

  public void loadCsvAndExecuteCreateIndexSql() {
    loadCsv();
    executeCreateIndexSql();
  }

  public boolean existsCsv() {
    return getCsvFile().exists();
  }

  private String readSql(String fileName) {
    return sqlResources.readSql(fileName);
  }

  public TableResources getTableResources() {
    return tableResources;
  }

  public SqlResources getSqlResources() {
    return sqlResources;
  }
}
