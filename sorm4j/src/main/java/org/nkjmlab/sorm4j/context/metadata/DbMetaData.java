package org.nkjmlab.sorm4j.context.metadata;

import java.sql.DatabaseMetaData;

import org.nkjmlab.sorm4j.internal.context.metadata.DbMetaDataImpl;

public interface DbMetaData {

  String getDatabaseProductName();

  static DbMetaData of(DatabaseMetaData metaData) {
    return new DbMetaDataImpl(metaData);
  }
}
