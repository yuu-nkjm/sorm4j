package org.nkjmlab.sorm4j.extension.h2.commands;

import java.io.File;

import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

/** <a href="https://www.h2database.com/html/commands.html#backup">Commands</a> */
public class BackupSql {
  private final String sql;

  public BackupSql(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static Builder builder(File file) {
    return new Builder(file);
  }

  public static class Builder {

    private File file;

    public Builder(File file) {
      this.file = file;
    }

    public BackupSql build() {
      return new BackupSql("backup to " + SqlStringUtils.quote(file.getAbsolutePath()));
    }
  }
}
