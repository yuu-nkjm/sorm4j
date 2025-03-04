package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.context.metadata.DbMetaData;
import org.nkjmlab.sorm4j.context.metadata.TableMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
