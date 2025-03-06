package org.nkjmlab.sorm4j.extension.datatype;

import org.nkjmlab.sorm4j.context.SormContext;

public interface DataTypeSupport {

  SormContext.Builder addSupport(SormContext.Builder contextBuilder);
}
