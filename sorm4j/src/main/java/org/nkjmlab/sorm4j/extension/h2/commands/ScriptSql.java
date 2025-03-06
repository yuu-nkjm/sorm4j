package org.nkjmlab.sorm4j.extension.h2.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.nkjmlab.sorm4j.extension.h2.grammar.ScriptCompressionEncryption;
import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

public class ScriptSql {
  private final String sql;

  public ScriptSql(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static class Builder {
    private Boolean noData = null;
    private Boolean simple = null;
    private Boolean columns = null;
    private Boolean noPasswords = null;
    private Boolean noSettings = null;
    private Boolean drop = null;
    private Integer blockSize = null;
    private File fileName = null;
    private ScriptCompressionEncryption scriptCompressionEncryption = null;
    private String charset = null;
    private List<String> tables = new ArrayList<>();
    private List<String> schemas = new ArrayList<>();

    public ScriptSql.Builder noData() {
      this.noData = true;
      return this;
    }

    public ScriptSql.Builder simple() {
      this.simple = true;
      return this;
    }

    public ScriptSql.Builder columns() {
      this.columns = true;
      return this;
    }

    public ScriptSql.Builder noPasswords() {
      this.noPasswords = true;
      return this;
    }

    public ScriptSql.Builder noSettings() {
      this.noSettings = true;
      return this;
    }

    public ScriptSql.Builder drop(boolean drop) {
      this.drop = drop;
      return this;
    }

    public ScriptSql.Builder blockSize(int blockSize) {
      this.blockSize = blockSize;
      return this;
    }

    public ScriptSql.Builder to(File fileName) {
      this.fileName = fileName;
      return this;
    }

    public ScriptSql.Builder scriptCompressionEncryption(
        ScriptCompressionEncryption scriptCompressionEncryption) {
      this.scriptCompressionEncryption = scriptCompressionEncryption;
      return this;
    }

    public ScriptSql.Builder charset(String charset) {
      this.charset = charset;
      return this;
    }

    public ScriptSql.Builder addTable(String table) {
      this.tables.add(table);
      return this;
    }

    public ScriptSql.Builder addSchema(String schema) {
      this.schemas.add(schema);
      return this;
    }

    public ScriptSql build() {
      List<String> ret = new ArrayList<>();
      ret.add("script");
      Optional.ofNullable(noData).filter(b -> b).ifPresent(c -> ret.add("nodata"));
      Optional.ofNullable(simple).ifPresent(c -> ret.add("simple"));
      Optional.ofNullable(columns).filter(b -> b).ifPresent(c -> ret.add("columns"));
      Optional.ofNullable(noPasswords).filter(b -> b).ifPresent(c -> ret.add("noPasswords"));
      Optional.ofNullable(noSettings).filter(b -> b).ifPresent(c -> ret.add("noSettings"));
      Optional.ofNullable(drop).filter(b -> b).ifPresent(c -> ret.add("drop"));
      Optional.ofNullable(blockSize)
          .filter(b -> b != null)
          .ifPresent(c -> ret.add("blocksize " + blockSize));
      if (fileName != null) {
        ret.add("to");
        ret.add(SqlStringUtils.quote(fileName.getAbsolutePath()));
        if (scriptCompressionEncryption != null) {
          ret.add(scriptCompressionEncryption.getSql());
        }
        if (charset != null) {
          ret.add("charset");
          ret.add(SqlStringUtils.quote(charset));
        }
      }

      if (tables.size() != 0) {
        ret.add("table");
        ret.add(String.join(",", tables));
      }
      if (schemas.size() != 0) {
        ret.add("schema");
        ret.add(String.join(",", schemas));
      }

      return new ScriptSql(String.join(" ", ret));
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
