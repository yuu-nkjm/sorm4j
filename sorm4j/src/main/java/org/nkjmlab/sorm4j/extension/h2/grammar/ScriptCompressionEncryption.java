package org.nkjmlab.sorm4j.extension.h2.grammar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.nkjmlab.sorm4j.sql.statement.SqlStringUtils;

public class ScriptCompressionEncryption {

  private final String sql;

  public ScriptCompressionEncryption(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, String> props = new LinkedHashMap<>();

    private Builder() {}

    private void procProp(String key, BiConsumer<String, String> func) {
      String val = props.get(key);
      if (val == null) {
        return;
      }
      func.accept(key, val);
    }

    public Builder compression(String compression) {
      this.props.put("compression", compression);
      return this;
    }

    public Builder cipher(String cipher) {
      this.props.put("cipher", cipher);
      return this;
    }

    public Builder password(String password) {
      this.props.put("password", password);
      return this;
    }

    public ScriptCompressionEncryption build() {
      List<String> ret = new ArrayList<>();

      procProp("compression", (key, val) -> ret.add(key + " " + val));
      procProp("cipher", (key, val) -> ret.add(key + " " + val));
      procProp("password", (key, val) -> ret.add(key + " " + SqlStringUtils.quote(val)));

      return new ScriptCompressionEncryption(String.join(" ", ret));
    }
  }

  public static Builder defaultBuilder(String password) {
    return new Builder().compression("DEFLATE").cipher("AES").password(password);
  }
}
