package org.nkjmlab.sorm4j.internal.context.common;

import java.sql.SQLException;

import org.nkjmlab.sorm4j.internal.container.sql.metadata.DbMetaData;
import org.nkjmlab.sorm4j.internal.util.Try;

public class DbMetaDataImpl implements DbMetaData {

  private final String databaseProductName;

  public DbMetaDataImpl(java.sql.DatabaseMetaData metaData) {
    try {
      this.databaseProductName = metaData.getDatabaseProductName();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public String getDatabaseProductName() {
    return databaseProductName;
  }
}
