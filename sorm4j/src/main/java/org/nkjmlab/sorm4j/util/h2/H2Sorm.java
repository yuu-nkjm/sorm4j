package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.util.h2.datasource.H2DataSourceFactory;

@Experimental
public class H2Sorm {

  public static Sorm createTemporalInMemory() {
    return Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
  }

  public static Sorm createTemporalInMemory(SormContext context) {
    return Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource(), context);
  }
}
