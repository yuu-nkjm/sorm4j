package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.nkjmlab.sorm4j.context.SormContext;

public class JtsContext {

  public static SormContext.Builder builder() {
    return SormContext.builder()
        .addColumnValueToJavaObjectConverter(new JtsColumnValueToJavaObjectConverter())
        .addSqlParameterSetter(new JtsSqlParameterSetter());
  }
}
