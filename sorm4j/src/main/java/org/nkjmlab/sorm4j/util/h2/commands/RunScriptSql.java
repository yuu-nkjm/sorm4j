package org.nkjmlab.sorm4j.util.h2.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.nkjmlab.sorm4j.util.h2.grammar.ScriptCompressionEncryption;
import org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils;

public class RunScriptSql {
  private final String sql;

  public RunScriptSql(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static class Builder {
    private final Map<String, String> props = new LinkedHashMap<>();
    private File fileName;
    private ScriptCompressionEncryption scriptCompressionEncryption = null;

    private void procProp(String key, BiConsumer<String, String> func) {
      String val = props.get(key);
      if (val == null) {
        return;
      }
      func.accept(key, val);
    }

    public Builder scriptCompressionEncryption(
        ScriptCompressionEncryption scriptCompressionEncryption) {
      this.scriptCompressionEncryption = scriptCompressionEncryption;
      return this;
    }

    public Builder charset(String val) {
      this.props.put("charset", val);
      return this;
    }

    public Builder from(File fileName) {
      this.fileName = fileName;
      return this;
    }

    public RunScriptSql build() {
      List<String> ret = new ArrayList<>();
      ret.add("RUNSCRIPT FROM");
      ret.add(LiteralUtils.wrapSingleQuote(fileName.getAbsolutePath()));
      if (scriptCompressionEncryption != null) {
        ret.add(scriptCompressionEncryption.getSql());
      }
      procProp("charset", (key, val) -> ret.add(key + " " + val));
      return new RunScriptSql(String.join(" ", ret));
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
