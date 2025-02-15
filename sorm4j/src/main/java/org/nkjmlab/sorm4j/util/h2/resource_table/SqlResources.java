package org.nkjmlab.sorm4j.util.h2.resource_table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.Try;

@Experimental
public class SqlResources {

  private final File resourcesDir;

  public SqlResources(File resourcesDir) {
    this.resourcesDir = resourcesDir;
  }

  public String readSql(String fileName) {
    return readSql(getSqlPath(fileName));
  }

  public Path getSqlPath(String fileName) {
    return new File(resourcesDir, fileName).toPath();
  }

  private String readSql(Path path) {
    try {
      return String.join(
          System.lineSeparator(),
          Files.readAllLines(path).stream().filter(l -> !l.startsWith("--")).toList());
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }
}
