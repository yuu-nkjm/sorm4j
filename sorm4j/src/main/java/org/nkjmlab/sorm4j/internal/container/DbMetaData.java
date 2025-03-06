package org.nkjmlab.sorm4j.internal.container;

import java.sql.DatabaseMetaData;

import org.nkjmlab.sorm4j.internal.context.common.DbMetaDataImpl;

public interface DbMetaData {

  String getDatabaseProductName();

  static DbMetaData of(DatabaseMetaData metaData) {
    return new DbMetaDataImpl(metaData);
  }
}
