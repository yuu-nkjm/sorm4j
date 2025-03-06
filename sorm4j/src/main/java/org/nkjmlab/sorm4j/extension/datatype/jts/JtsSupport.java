package org.nkjmlab.sorm4j.extension.datatype.jts;

import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.datatype.DataTypeSupport;

public class JtsSupport implements DataTypeSupport {

  @Override
  public SormContext.Builder addSupport(SormContext.Builder builder) {
    return builder
        .addColumnValueToJavaObjectConverter(new JtsColumnValueToJavaObjectConverter())
        .addSqlParameterSetter(new JtsSqlParameterSetter());
  }
}
