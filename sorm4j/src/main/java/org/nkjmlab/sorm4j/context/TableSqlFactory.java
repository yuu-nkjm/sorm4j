package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.common.TableMetaData;

public interface TableSqlFactory {

  TableSql create(
      TableMetaData tableMetaData, org.nkjmlab.sorm4j.common.DatabaseMetaData databaseMetaData);
}
