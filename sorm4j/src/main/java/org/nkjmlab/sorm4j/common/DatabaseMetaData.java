package org.nkjmlab.sorm4j.common;

import org.nkjmlab.sorm4j.internal.common.DatabaseMetaDataImpl;

public interface DatabaseMetaData {

  String getDatabaseProductName();

  static DatabaseMetaData of(java.sql.DatabaseMetaData metaData) {
    return new DatabaseMetaDataImpl(metaData);
  }
}
