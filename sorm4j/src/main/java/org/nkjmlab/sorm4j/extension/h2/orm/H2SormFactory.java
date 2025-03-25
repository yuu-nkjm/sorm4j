package org.nkjmlab.sorm4j.extension.h2.orm;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.annotation.Experimental;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory;

@Experimental
public class H2SormFactory {

  private H2SormFactory() {}

  public static Sorm createTemporalInMemory() {
    return Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
  }

  public static Sorm createTemporalInMemory(SormContext context) {
    return Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource(), context);
  }
}
