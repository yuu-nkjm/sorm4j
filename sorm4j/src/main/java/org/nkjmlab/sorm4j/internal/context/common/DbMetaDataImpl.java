package org.nkjmlab.sorm4j.internal.context.common;

import org.nkjmlab.sorm4j.internal.sql.metadata.DbMetaData;
import org.nkjmlab.sorm4j.util.function.exception.Try;

public class DbMetaDataImpl implements DbMetaData {

  private final String databaseProductName;

  public DbMetaDataImpl(java.sql.DatabaseMetaData metaData) {
    this.databaseProductName = Try.getOrElseThrow(() -> metaData.getDatabaseProductName());
  }

  @Override
  public String getDatabaseProductName() {
    return databaseProductName;
  }
}
