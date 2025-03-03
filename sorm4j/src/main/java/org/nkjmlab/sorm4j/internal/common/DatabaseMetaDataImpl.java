package org.nkjmlab.sorm4j.internal.common;

import java.sql.SQLException;

import org.nkjmlab.sorm4j.common.DatabaseMetaData;
import org.nkjmlab.sorm4j.internal.util.Try;

public class DatabaseMetaDataImpl implements DatabaseMetaData {

  private final String databaseProductName;

  public DatabaseMetaDataImpl(java.sql.DatabaseMetaData metaData) {
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
