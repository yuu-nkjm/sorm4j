package org.nkjmlab.sorm4j.util.datatype.jts;

import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.context.SormContext;

@Experimental
public class JtsSormContext {

  private JtsSormContext() {}

  public static SormContext.Builder builder() {
    return SormContext.builder()
        .addColumnValueToJavaObjectConverter(new JtsColumnValueToJavaObjectConverter())
        .addSqlParameterSetter(new JtsSqlParameterSetter());
  }
}
