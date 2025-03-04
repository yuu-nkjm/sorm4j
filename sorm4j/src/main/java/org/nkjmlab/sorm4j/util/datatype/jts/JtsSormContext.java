package org.nkjmlab.sorm4j.util.datatype.jts;

import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.context.DefaultColumnValueToJavaObjectConverters;
import org.nkjmlab.sorm4j.context.DefaultSqlParametersSetter;
import org.nkjmlab.sorm4j.context.SormContext;

@Experimental
public class JtsSormContext {

  private JtsSormContext() {}

  public static SormContext.Builder builder() {
    return SormContext.builder()
        .setColumnValueToJavaObjectConverters(
            new DefaultColumnValueToJavaObjectConverters(new JtsColumnValueToJavaObjectConverter()))
        .setSqlParametersSetter(new DefaultSqlParametersSetter(new JtsSqlParameterSetter()));
  }
}
