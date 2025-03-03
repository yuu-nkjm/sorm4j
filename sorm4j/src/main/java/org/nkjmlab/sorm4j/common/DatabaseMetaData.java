package org.nkjmlab.sorm4j.common;

import java.sql.SQLException;

import org.nkjmlab.sorm4j.internal.util.Try;

public interface DatabaseMetaData {

  String getDatabaseProductName();

  static DatabaseMetaData of(java.sql.DatabaseMetaData metaData) {
    return new DatabaseMetaDataImpl(metaData);
  }

  public static class DatabaseMetaDataImpl implements DatabaseMetaData {

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
}
