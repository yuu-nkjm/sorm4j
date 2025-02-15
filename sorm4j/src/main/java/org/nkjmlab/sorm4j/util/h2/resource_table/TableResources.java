package org.nkjmlab.sorm4j.util.h2.resource_table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.h2.H2BasicTable;

@Experimental
public class TableResources {

  private static String toTableName(File csv) {
    return csv.getName().replace(".csv", "");
  }

  private final File resourcesDir;

  public TableResources(File resourcesDir) {
    this.resourcesDir = resourcesDir;
  }

  public void deleteAllCsvExclude(String... tableNames) {
    try {
      String[] targets =
          Files.list(resourcesDir.toPath())
              .filter(p -> p.toFile().getName().endsWith(".csv"))
              .map(p -> toTableName(p.toFile()))
              .filter(tableName -> !Arrays.asList(tableNames).contains(tableName))
              .toArray(String[]::new);
      deleteCsv(targets);
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  public void deleteCsv(String... tableNames) {
    Arrays.stream(tableNames).forEach(tableName -> getCsvFile(tableName).delete());
  }

  public File getCsvFile(String tableName) {
    return new File(resourcesDir, tableName + ".csv");
  }

  public <T extends H2BasicTable<?>> File getCsvFile(T table) {
    return getCsvFile(table.getTableName());
  }

  public <T extends H2BasicTable<?>> void writeCsv(T table) {
    File csv = getCsvFile(table);
    table.writeCsv(csv);
  }

  public File getResourcesDirectory() {
    return resourcesDir;
  }
}
